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

public class GradleRootProject extends AbstractGradleProject {

	private final static FileFilter FILTER = new GradleSettingsFileFilter();
	private File file;
	private boolean multiProject;
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
		
		File[] files = file.listFiles(FILTER);
		if (files == null || files.length != 1) {
			return false;
		}
		File gradleSettings = files[0];
		if (gradleSettings==null || ! gradleSettings.exists() || ! gradleSettings.isFile()){
			return false;
		}
		try(FileInputStream fis = new FileInputStream(gradleSettings)){
			GradleModelBuilder builder = new GradleModelBuilder(fis);
			Model settingsModel = builder.build(null);
			Item root = settingsModel.getRoot();
			if (root==null){
				return false;
			}
			Item[] children = root.getChildren();
			for (Item item: children){
				if (item==null){
					continue;
				}
				String identifier = item.getIdentifier();
				if ("include".equals(identifier)){
					return true;
				}
			}
			return false;
		}catch(Exception e){
			return false;
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
