package de.jcup.egradle.core.codecompletion.model.gradledsl;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.codecompletion.model.Type;

public class FilesystemFileLoader implements GradleDSLFileLoader {
	/* FIXME ATR, 13.01.2017: continue implmentation: create code completion model integration for generated dsl parts of gradle */
	private File folder;
	
	public FilesystemFileLoader(File folder){
		if (folder==null){
			throw new IllegalArgumentException("folder must not be null!");
		}
		this.folder=folder;
	}
	
	@Override
	public Type load(String name) {
		if (StringUtils.isBlank(name)){
			return null;
		}
		if (! folder.exists()){
			return null;
		}
		if (! folder.isDirectory()){
			return null;
		}
		
		
		return null;
	}

}
