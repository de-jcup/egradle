/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.egradle.junit;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class JUnitResultFilesFinder {
	
	private JUnitFileFilter filter = new JUnitFileFilter();

	public Collection<File> findTestFilesInFolder(File rootFolder, String subProject) throws IOException {
		Set<File> set = new TreeSet<>();
		
		assertDirectoryExists(rootFolder);
		File directoryToScan = rootFolder;
		if (subProject!=null){
			directoryToScan=new File(rootFolder,subProject);
			assertDirectoryExists(directoryToScan);
		}
		scanJunitResultsInFolder(set, directoryToScan);
		
		return set;
	}

	private void assertDirectoryExists(File rootFolder) throws FileNotFoundException {
		if (! rootFolder.exists()){
			throw new FileNotFoundException("Directory file not exists:"+rootFolder.getAbsolutePath());
		}
		if (! rootFolder.isDirectory()){
			throw new FileNotFoundException("Not a directory file :"+rootFolder.getAbsolutePath());
		}
	}

	private void scanJunitResultsInFolder(Set<File> set, File folder)  throws IOException {
		File[] files = folder.listFiles(filter);
		for (File file: files){
			if (file.isDirectory()){
				scanJunitResultsInFolder(set, file);
			}else{
				set.add(file);
			}
		}
		
	}
	private static final String FILE_SEP = System.getProperty("file.separator");
	private static final String BUILD_FOLDER_PART = FILE_SEP+"build"+FILE_SEP;
	private static final String TEST_RESULTS_FOLDER_PART = FILE_SEP+"test-results"+FILE_SEP;
	
	private class JUnitFileFilter implements FileFilter{

		

		@Override
		public boolean accept(File file) {
			String name = file.getName();
			if (name==null){
				return false;
			}
			if (file.isDirectory()){
				/* optimize performance*/
				if (name.equals("src")){ // we ignore all content in src
					return false;
				}
				if (name.equals(".settings")){ // we ignore settings for eclipse
					return false;
				}
				if (name.equals(".gradle")){ // we ignore gradle cache folder
					return false;
				}
				if (name.equals(".git")){ // we ignore git data folder
					return false;
				}
				if (name.equals("bin")){ // we ignore eclipse bin output folder
					return false;
				}
				if (name.equals("classes")){ // we ignore gradle bin output folder
					return false;
				}
				if (name.equals("binary")){ // we ignore gradle binary parts inside test output folder
					return false;
				}
				return true;
			}
			/* file found*/
			if (!name.startsWith("TEST-")){
				return false;
			}
			if (!name.endsWith(".xml")){
				return false;
			}
			/* test file. but check if its really a test result and not something else:*/
			String path = file.getPath();
			if (path.indexOf(TEST_RESULTS_FOLDER_PART)==-1){
				return false;
			}
			if (path.indexOf(BUILD_FOLDER_PART)==-1){
				return false;
			}
			return true;
		}
		
	}

	
}
