package de.jcup.egradle.core.util;

import java.io.File;
import java.io.FileFilter;

public class RootProjectFinder {

	private static SettingsFilter FILE_FILTER = new SettingsFilter();
	
	public File findRootProjectFolder(File file) {
		if (file==null){
			return null;
		}
		if (! file.isDirectory()){
			return scan(file.getParentFile());
		}
		return scan(file);
	}

	private File scan(File parentFolder) {
		if (parentFolder==null){
			return null;
		}
		File[] result = parentFolder.listFiles(FILE_FILTER);
		if (result!=null && result.length==1){
			return parentFolder;
		}
		return scan(parentFolder.getParentFile());
	}
	
	private static class SettingsFilter implements FileFilter{

		@Override
		public boolean accept(File file) {
			if (file==null){
				return false;
			}
			if ("settings.gradle".equals(file.getName())){
				return true;
			}
			return false;
		}
		
	}
	
	
}
