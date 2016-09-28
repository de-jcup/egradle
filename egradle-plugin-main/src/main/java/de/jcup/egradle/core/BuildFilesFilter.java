package de.jcup.egradle.core;

import java.io.File;
import java.io.FileFilter;

/**
 * Filter for all kind of build files (*.gradle) and gradle.properties
 * @author Albert Tregnaghi
 *
 */
public class BuildFilesFilter implements FileFilter {
	
	private boolean allowSubFolders;

	/**
	 * Creates new build files filter. 
	 * @param allowSubFolders- when <code>true</code> sub folders of current directory are all accepted, otherwise not
	 */
	public BuildFilesFilter(boolean allowSubFolders){
		this.allowSubFolders=allowSubFolders;
	}


	@Override
	public boolean accept(File file) {
		if (file==null){
			return false;
		}
		/* ----------- DIRECTORY ----------*/
		if (file.isDirectory()){
			if (allowSubFolders){
				return true;
			}
			return false;
		}
		
		/* ----------- FILE ---------------*/
		String name = file.getName();
		if (name == null) {
			return false;
		}
		if (name.endsWith(".gradle")) {
			return true;
		}
		if ("gradle.properties".equals(name)) {
			return true;
		}
		return false;
	}

}