package de.jcup.egradle.core;

import static org.apache.commons.lang3.Validate.*;

import java.io.File;
import java.io.IOException;

public class SimpleRootFolderProvider implements RootFolderProvider{

	private File rootFolder;

	public SimpleRootFolderProvider(File rootFolder){
		notNull(rootFolder, "'rootFolder' may not be null");
		this.rootFolder=rootFolder;
	}
	
	@Override
	public File getRootFolder() throws IOException {
		return rootFolder;
	}

}
