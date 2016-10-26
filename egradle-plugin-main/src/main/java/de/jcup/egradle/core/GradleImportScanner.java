package de.jcup.egradle.core;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GradleImportScanner {

	private static final FileFilter ECLIPSE_PROJECT_FILTER = new EclipseProjectFileFilter();
	
	
	public GradleImportScanner() {
	}

	public List<File> scanEclipseProjectFolders(File folder) {
		if (folder==null){
			return Collections.emptyList();
		}
		List<File> list = new ArrayList<>();
		for (File file: folder.listFiles()){
			if (file.isDirectory()){
				File[] projectFiles= file.listFiles(ECLIPSE_PROJECT_FILTER);
				if (projectFiles.length>0){
					list.add(file);
				}
			}
		}
		return list;
	}
	
	private static class EclipseProjectFileFilter implements FileFilter{

		@Override
		public boolean accept(File file) {
			if (file==null){
				return false;
			}
			if (file.isDirectory()){
				return false;
			}
			return ".project".equals(file.getName());
		}
		
	}

}
