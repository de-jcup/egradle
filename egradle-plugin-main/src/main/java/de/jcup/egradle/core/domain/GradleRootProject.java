/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.egradle.core.domain;

import static org.apache.commons.lang3.Validate.*;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.model.groovyantlr.GradleModelBuilder;
import de.jcup.egradle.core.util.FileSupport;
import de.jcup.egradle.core.util.StringUtilsAccess;

public class GradleRootProject extends AbstractGradleProject {

	private final static FileFilter FILTER = new GradleSettingsFileFilter();
	private File file;
	private boolean multiProject;
	private Model settingsModel;
	private Item includeItem;
	/**
	 * Creates a gradle root project
	 * 
	 * @param file
	 * @throws IOException
	 *             when given fils is not a directory or does not exists
	 */
	public GradleRootProject(File file) throws IOException {
		notNull(file);
		if (!file.exists()) {
			throw new IOException("Given root project folder does not exist:" + file);
		}
		if (!file.isDirectory()) {
			throw new IOException("Given root project folder is not a directory:" + file);
		}
		this.file = file;
		multiProject = gradleSettingsFileContainsAnIncludeItem();
	}

	public String getName(){
		return file.getName();
	}
	
	/* check there is a settings.gradle file. Check content having an item "include".
	 * If "include" is contained it is a gradle mutli project.
	 */
	private boolean gradleSettingsFileContainsAnIncludeItem() {
		tryToLoadIncludeItemInGradleSettings();
		
		return (includeItem!=null);
	}
	
	public void createNewSubProject(String subProjectName) throws GradleProjectException{
		File folder = getFolder();
		if (!isMultiProject()){
			throw new GradleProjectException("Project is not a multi project, so cannot add sub project:"+subProjectName+" at "+folder);
		}
		
		File subProjectFolder = new File(folder,subProjectName);
		if (! subProjectFolder.exists()){
			if (!subProjectFolder.mkdirs()){
				throw new GradleProjectException("Was not able to create sub project folder:"+subProjectFolder);
			}
		}
		
		File settingsGradle = new File(folder,"settings.gradle");
		if (! settingsGradle.exists()){
			throw new GradleProjectException("did not found settings.gradle any more at:"+settingsGradle);
		}
		
		try{
			String content = FileSupport.DEFAULT.readTextFile(settingsGradle);
			int offset = includeItem.getOffset()+includeItem.getLength();
			
			String start = StringUtilsAccess.substring(content, 0, offset);
			String end = StringUtilsAccess.substring(content, offset);
			StringBuilder sb = new StringBuilder();
			sb.append(start);
			sb.append(", '");
			sb.append(subProjectName);
			sb.append("'");
			sb.append(end);
		
			FileSupport.DEFAULT.createTextFile(settingsGradle, sb.toString());
		
		}catch(IOException e){
			throw new GradleProjectException("Problems occurred on addding subproject infromation to setttings.gradle",e);
		}
		
	}
	
	private void tryToLoadIncludeItemInGradleSettings(){
		/* reset include item if reload */
		includeItem=null;
		
		File[] files = file.listFiles(FILTER);
		if (files == null || files.length != 1) {
			return;
		}
		File gradleSettings = files[0];
		if (gradleSettings==null || ! gradleSettings.exists() || ! gradleSettings.isFile()){
			return;
		}
		
		try(FileInputStream fis = new FileInputStream(gradleSettings)){
			GradleModelBuilder builder = new GradleModelBuilder(fis);
			settingsModel = builder.build(null);
			Item root = settingsModel.getRoot();
			if (root==null){
				return;
			}
			Item[] children = root.getChildren();
			for (Item item: children){
				if (item==null){
					continue;
				}
				String identifier = item.getIdentifier();
				if ("include".equals(identifier)){
					includeItem = item;
					break;
				}
			}
		}catch(Exception e){
			/* ignore */
		}
	}

	public File getFolder() {
		return file;
	}

	public boolean isMultiProject() {
		return multiProject;
	}

	private static class GradleSettingsFileFilter implements FileFilter {

		@Override
		public boolean accept(File file) {
			if (file == null) {
				return false;
			}
			if (file.isDirectory()) {
				return false;
			}
			String name = file.getName();
			return "settings.gradle".equals(name);
		}

	}
}
