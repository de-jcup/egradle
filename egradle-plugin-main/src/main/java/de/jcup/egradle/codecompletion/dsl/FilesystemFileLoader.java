package de.jcup.egradle.codecompletion.dsl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class FilesystemFileLoader implements DSLFileLoader {
	private File dslFolder;
	private XMLDSLTypeImporter importer;
	private static final Pattern PATTERN_EVERY_DOT = Pattern.compile("\\.");
	
	public FilesystemFileLoader(XMLDSLTypeImporter importer){
		if (importer==null){
			throw new IllegalArgumentException("importer must not be null!");
		}
		this.importer=importer;
	}
	
	public void setDSLFolder(File folder) {
		this.dslFolder=folder;
	}
	
	@Override
	public Type load(String name) throws IOException{
		if (dslFolder==null){
			throw new DSLFolderNotSetException();
		}
		if (StringUtils.isBlank(name)){
			return null;
		}
		if (! dslFolder.exists()){
			throw new DSLFolderNotValidException("DSL folder does not exist:"+dslFolder);
		}
		if (! dslFolder.isDirectory()){
			throw new DSLFolderNotValidException("DSL folder ist not a directory:"+dslFolder);
		}
		String path = PATTERN_EVERY_DOT.matcher(name).replaceAll("/");
		path+=".xml";
		File sourceFile = new File(dslFolder, path);
		InputStream stream = new FileInputStream(sourceFile);
		
		Type type = importer.importType(stream);
		
		return type;
	}

	public class DSLFolderNotSetException extends IOException{

		private static final long serialVersionUID = 1L;
		
	}
	
	public class DSLFolderNotValidException extends IOException{

		public DSLFolderNotValidException(String message) {
			super(message);
		}

		private static final long serialVersionUID = 1L;
		
	}

}
