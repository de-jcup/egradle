package de.jcup.egradle.core.util;

import java.io.File;

public class GradleInfoSupport {
	
	public static GradleInfoSupport DEFAULT = new GradleInfoSupport();
	
	public boolean isGradleRootProjectFolder(File folder){
		if (folder==null){
			return false;
		}
		File rootFolder = resolveGradleRootProjectFolder(folder);
		if (rootFolder==null){
			return false;
		}
		if (! rootFolder.equals(folder)){
			return false;
		}
		return true;
	}
	
	/**
	 * Resolves gradle root project folder or <code>null</code>
	 * @param folder
	 * @return root project folder or <code>null</code>
	 */
	public File resolveGradleRootProjectFolder(File folder){
		if (folder==null){
			return null;
		}
		boolean multiProjectRoot = isMultiProjectRoot(folder);
		if (multiProjectRoot){
			/* is a root of multi project */
			return folder;
		}
		
		/* check if at least a build.gadle file is available */
		if (! isFolderContainingFileWithName(folder, "build.gradle")){
			/*
			 * cannot be a a single project - missing build file
			 */
			return null;
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
				return parent;
			}
		}
		/* seems to be a single project - build.gradle exists and not in multi project */
		return folder;
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
