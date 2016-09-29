package de.jcup.egradle.core;

import java.io.File;
import java.io.FileFilter;

public class AcceptAllFilesFilter implements FileFilter{
	
	private boolean allowSubFolders;
	
	public AcceptAllFilesFilter(boolean allowSubFolders){
		this.allowSubFolders=allowSubFolders;
	}
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()){
			if (allowSubFolders){
				return true;
			}
			return false;
		}
		return true;
	}

}
