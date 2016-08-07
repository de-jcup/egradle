package de.jcup.egradle.core.domain;

import java.io.File;

import static org.apache.commons.lang3.Validate.*;

public class GradleRootProject extends AbstractGradleProject{

	private File file;

	public GradleRootProject(File file){
		notNull(file);
		if (!file.isDirectory()){
			throw new IllegalArgumentException("File is not a directory:"+file);
		}
		this.file=file;
	}
	
	public File getFolder() {
		return file;
	}
}
