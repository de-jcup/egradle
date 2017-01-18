package de.jcup.egradle.core.codecompletion.model.gradledsl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.codecompletion.model.Type;

public class FilesystemFileLoader implements GradleDSLFileLoader {
	/* FIXME ATR, 13.01.2017: continue implmentation: create code completion model integration for generated dsl parts of gradle */
	private File dslFolder;
	private XMLDSLTypeImporter importer;
	private static final Pattern PATTERN_EVERY_DOT = Pattern.compile("\\.");
	
	public FilesystemFileLoader(File dslFolder, XMLDSLTypeImporter importer){
		if (dslFolder==null){
			throw new IllegalArgumentException("folder must not be null!");
		}
		if (importer==null){
			throw new IllegalArgumentException("importer must not be null!");
		}
		this.importer=importer;
		this.dslFolder=dslFolder;
	}
	
	@Override
	public Type load(String name) throws IOException{
		if (StringUtils.isBlank(name)){
			return null;
		}
		if (! dslFolder.exists()){
			return null;
		}
		if (! dslFolder.isDirectory()){
			return null;
		}
		String path = PATTERN_EVERY_DOT.matcher(name).replaceAll("/");
		path+=".xml";
		File sourceFile = new File(dslFolder, path);
		InputStream stream = new FileInputStream(sourceFile);
		
		Type type = importer.importType(stream);
		
		return type;
	}

}
