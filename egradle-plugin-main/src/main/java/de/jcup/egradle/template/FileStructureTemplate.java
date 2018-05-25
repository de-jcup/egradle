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
package de.jcup.egradle.template;

import static org.apache.commons.lang3.Validate.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.util.DirectoryCopySupport;
import de.jcup.egradle.core.util.FileSupport;

public class FileStructureTemplate {

	static final String PROP_NAME = "name";
	static final String PROP_DESCRIPTION = "description";
	static final String PROP_PRIORITY = "priority";
	static final String PROP_PREDEFINED_SUBPROJECTS="predefined.subprojects";

	private File pathToContent;
	DirectoryCopySupport copySupport;
	private String name;
	private String description;
	FileSupport fileSupport;
	TemplateContentTransformerFactory contentTransformerFactory;
	private List<String> predefinedSubprojects;

	public FileStructureTemplate(String name, File contentRootFolder, String description, int priority,List<String>predefinedSubprojects) {
		notNull(contentRootFolder, "'pathToContent' may not be null");
		if (name == null) {
			name = contentRootFolder.getName();
		}
		if (description == null) {
			description = "No description available";
		}
		this.pathToContent = contentRootFolder;
		this.copySupport = new DirectoryCopySupport();
		this.fileSupport = new FileSupport();
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.predefinedSubprojects=predefinedSubprojects;

		/* own internal factory so easier to test */
		this.contentTransformerFactory = new TemplateContentTransformerFactory();
	}

	public String getName() {
		return name;
	}

	public File getPathToContent() {
		return pathToContent;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Applies content of template to given target folder. The target folder
	 * name itself is NOT replaced by variables etc. <br>
	 * <br>
	 * But sub directories and file names will be replaced in following way:
	 * <ul>
	 * <li>_xy will be replaced to xy</li>
	 * <li>$my.property will be tried to be replaced by property key</li>
	 * </ul>
	 * 
	 * @param targetFolder
	 * @param properties
	 *            - properties containing data for replacement
	 * @throws IOException
	 */
	public void applyTo(File targetFolder, Properties properties) throws IOException {
		if (targetFolder == null) {
			throw new IllegalArgumentException("target folder may not be null!");
		}
		if (!targetFolder.isDirectory()) {
			throw new IOException("Given file is no folder:" + targetFolder);
		}
		if (properties == null) {
			properties = new Properties();
		}

		try {
			copyFiles(targetFolder, properties);
		} catch (IOException e) {
			throw new IOException("Cannot copy files from:" + pathToContent + " to " + targetFolder, e);
		}
		try {
			transformFiles(targetFolder, properties);
		} catch (IOException e) {
			throw new IOException("Cannot transform files from:" + pathToContent + " to " + targetFolder, e);
		}

	}

	
	private void copyFiles(File targetFolder, Properties properties) throws IOException {
		TemplateFileNameTransformer targetFileNameTransformer = new TemplateFileNameTransformer(properties);
		copySupport.copyDirectories(pathToContent, targetFolder, targetFileNameTransformer, true,
				"template.properties");
	}

	private void transformFiles(File targetFolder, Properties properties) throws IOException {
		TemplateContentTransformer templateContentTransformer = contentTransformerFactory
				.createTemplateContentTransformer(properties);

		if (templateContentTransformer == null) {
			throw new IllegalStateException(
					"Factory wrong implemented, returns null for transformer:" + contentTransformerFactory.getClass());
		}

		transformContentRecursive(targetFolder, templateContentTransformer);
	}

	private void transformContentRecursive(File file, TemplateContentTransformer transformer) throws IOException {
		if (file.isFile()) {
			String source = fileSupport.readTextFile(file);
			String transformed = transformer.transform(source);
			if (StringUtils.equals(source, transformed)) {
				// ignore - same content as before
				return;
			}
			fileSupport.writeTextFile(file, transformed);
		} else if (file.isDirectory()) {
			for (File subFile : file.listFiles()) {
				transformContentRecursive(subFile, transformer);
			}
		}

	}

	static final FileFilter ONLY_DIRECTORIES = new FileFilter() {

		@Override
		public boolean accept(File file) {
			if (file == null) {
				return false;
			}
			return file.isDirectory();
		}
	};

	static class TemplateContentTransformerFactory {

		public TemplateContentTransformer createTemplateContentTransformer(Properties properties) {
			return new TemplateContentTransformer(properties);
		}
	}

	private Set<Feature> enabledFeatures = new HashSet<>();
	private int priority;

	public void enableFeature(Feature f) {
		if (f == null) {
			return;
		}
		enabledFeatures.add(f);
	}

	public boolean hasFeature(Feature feature) {
		if (feature == null) {
			return false;
		}
		return enabledFeatures.contains(feature);
	}

	public int getPriority() {
		return priority;
	}

	public List<String> getPredefinedSubprojects() {
		return predefinedSubprojects;
	}

	public void copyPredefinedSubProjects(File targetRootFolder) throws IOException {
		/* add predefined parts */
		for(String predefinedProject: predefinedSubprojects){
			copySupport.copyDirectories(new File(pathToContent,predefinedProject), new File(targetRootFolder,predefinedProject), null, true);
		}
		
	}
}
