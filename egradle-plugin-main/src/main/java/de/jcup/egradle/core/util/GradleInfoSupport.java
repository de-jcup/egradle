package de.jcup.egradle.core.util;

import java.io.File;

public class GradleInfoSupport {

	public boolean isGradleRootProjectFolder(File folder){
		if (folder==null){
			return false;
		}
		boolean multiProjectRoot = isMultiProjectRoot(folder);
		if (multiProjectRoot){
			/* is a root of multi project */
			return true;
		}
		
		/* check if at least a build.gadle file is available */
		if (! isFolderContainingFileWithName(folder, "build.gradle")){
			/*
			 * cannot be a a single project - missing build file
			 */
			return false;
		}
		
		/* ------------------------ */
		/* Check for single project */
		/* ------------------------ */
		
		/* scan parent directories*/
		File parent = folder;
		while (true){

			parent = parent.getParentFile();
			if (parent==null){
				break;
			}
			if (isMultiProjectRoot(parent)){
				/* found a multi project directory, so this is a sub project */
				return false;
			}
		}
		/* seems to be a single project - build.gradle exists and not in multi project */
		return true;
	}

	private boolean isMultiProjectRoot(File folder) {
		if (folder==null){
			return false;
		}
		if (!folder.isDirectory()){
			return false;
		}
		if (!folder.exists()){
			return false;
		}

		boolean found = isFolderContainingFileWithName(folder, "settings.gradle");
		return found;
	}

	private boolean isFolderContainingFileWithName(File folder, String searchedFileName) {
		File[] files = folder.listFiles();
		for (File file: files){
			if (searchedFileName.equals(file.getName())){
				return true;
			}
		}
		return false;
	}
}
