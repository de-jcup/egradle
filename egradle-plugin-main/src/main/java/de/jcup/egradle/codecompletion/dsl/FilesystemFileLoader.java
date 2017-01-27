package de.jcup.egradle.codecompletion.dsl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class FilesystemFileLoader implements DSLFileLoader {
	private File dslFolder;
	private XMLDSLTypeImporter typeImporter;
	private XMLDSLPluginsImporter pluginsImporter;
	private static final Pattern PATTERN_EVERY_DOT = Pattern.compile("\\.");

	public FilesystemFileLoader(XMLDSLTypeImporter typeImporter, XMLDSLPluginsImporter pluginsImporter) {
		if (typeImporter == null) {
			throw new IllegalArgumentException("typeImporter must not be null!");
		}
		if (pluginsImporter == null) {
			throw new IllegalArgumentException("pluginsImporter must not be null!");
		}
		this.typeImporter = typeImporter;
		this.pluginsImporter=pluginsImporter;
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
		if (!sourceFile.exists()){
			throw new FileNotFoundException("Did not found:"+sourceFile);
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
		if (!sourceFile.exists()){
			throw new FileNotFoundException("Did not found:"+sourceFile);
		}
		InputStream stream = new FileInputStream(sourceFile);

		Set<Plugin> plugins = pluginsImporter.importPlugins(stream);

		return plugins;
	}

	public class DSLFolderNotSetException extends IOException {

		private static final long serialVersionUID = 1L;

	}

	public class DSLFolderNotValidException extends IOException {

		public DSLFolderNotValidException(String message) {
			super(message);
		}

		private static final long serialVersionUID = 1L;

	}

}
