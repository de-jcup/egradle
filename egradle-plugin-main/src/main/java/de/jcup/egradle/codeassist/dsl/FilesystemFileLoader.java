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
 package de.jcup.egradle.codeassist.dsl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class FilesystemFileLoader implements DSLFileLoader {
	private File dslFolder;
	private XMLTypeImporter typeImporter;
	private XMLPluginsImporter pluginsImporter;
	private ApiMappingImporter apiMappingImporter;
	private static final Pattern PATTERN_EVERY_DOT = Pattern.compile("\\.");

	public FilesystemFileLoader(XMLTypeImporter typeImporter, XMLPluginsImporter pluginsImporter, ApiMappingImporter apiMappingImporter) {
		if (typeImporter == null) {
			throw new IllegalArgumentException("typeImporter must not be null!");
		}
		if (pluginsImporter == null) {
			throw new IllegalArgumentException("pluginsImporter must not be null!");
		}
		if (apiMappingImporter == null) {
			throw new IllegalArgumentException("apiMappingImporter must not be null!");
		}
		this.typeImporter = typeImporter;
		this.pluginsImporter = pluginsImporter;
		this.apiMappingImporter=apiMappingImporter;
	}

	public void setDSLFolder(File folder) {
		this.dslFolder = folder;
	}

	@Override
	public Type loadType(String name) throws IOException {
		if (dslFolder == null) {
			throw new DSLFolderNotSetException();
		}
		if (StringUtils.isBlank(name)) {
			return null;
		}
		if (!dslFolder.exists()) {
			throw new DSLFolderNotValidException("DSL folder does not exist:" + dslFolder);
		}
		if (!dslFolder.isDirectory()) {
			throw new DSLFolderNotValidException("DSL folder ist not a directory:" + dslFolder);
		}
		String path = PATTERN_EVERY_DOT.matcher(name).replaceAll("/");
		path += ".xml";
		File sourceFile = new File(dslFolder, path);
		if (!sourceFile.exists()) {
			throw new FileNotFoundException("Did not found:" + sourceFile);
		}
		InputStream stream = new FileInputStream(sourceFile);

		Type type = typeImporter.importType(stream);

		return type;
	}

	@Override
	public Set<Plugin> loadPlugins() throws IOException {
		if (dslFolder == null) {
			throw new DSLFolderNotSetException();
		}
		if (!dslFolder.exists()) {
			throw new DSLFolderNotValidException("DSL folder does not exist:" + dslFolder);
		}
		if (!dslFolder.isDirectory()) {
			throw new DSLFolderNotValidException("DSL folder ist not a directory:" + dslFolder);
		}
		File sourceFile = new File(dslFolder, "plugins.xml");
		if (!sourceFile.exists()) {
			throw new FileNotFoundException("Did not found:" + sourceFile);
		}
		try(InputStream stream = new FileInputStream(sourceFile)){
			XMLPlugins xmlPlugins = pluginsImporter.importPlugins(stream);
			Set<Plugin> plugins = xmlPlugins.getPlugins();
			return plugins;
			
		}

	}

	@Override
	public Map<String, String> loadApiMappings() throws IOException {
		Map<String, String> alternative;
		Map<String, String> origin;
		/* first load alternate origin mapping */
		File apiMappingFile = new File(dslFolder,"api-mapping.txt");
		try(InputStream stream = new FileInputStream(apiMappingFile)){
			origin = apiMappingImporter.importMapping(stream);
		}
		File alternativeApiMappingFile = new File(dslFolder,"alternative-api-mapping.txt");
		if (! alternativeApiMappingFile.exists()){
			return origin; 
		}
		try(InputStream stream = new FileInputStream(alternativeApiMappingFile)){
			alternative = apiMappingImporter.importMapping(stream);
		}
		/* merge results, origin api wins, if there are conflicts */
		Map<String, String> result= alternative;
		result.putAll(origin);
		return result;
	}

	public class DSLFolderNotSetException extends IOException {

		private static final long serialVersionUID = 1L;

	}

	private class DSLFolderNotValidException extends IOException {

		private DSLFolderNotValidException(String message) {
			super(message);
		}

		private static final long serialVersionUID = 1L;

	}

}
