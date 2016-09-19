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

	public Collection<File> findTestFilesInRootProjectFolder(File rootFolder) throws IOException {
		Set<File> set = new TreeSet<>();
		
		if (! rootFolder.exists()){
			throw new FileNotFoundException("Directory file not exists:"+rootFolder.getAbsolutePath());
		}
		if (! rootFolder.isDirectory()){
			throw new FileNotFoundException("Not a directory file :"+rootFolder.getAbsolutePath());
		}
		scanJunitResultsInFolder(set, rootFolder);
		
		return set;
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
	private class JUnitFileFilter implements FileFilter{

		@Override
		public boolean accept(File file) {
			String name = file.getName();
			if (name==null){
				return false;
			}
			if (file.isDirectory()){
				/* TODO Albert, 20.09.2016 - Directory performance filtering could be improved - the ... name.equals looks ugly */
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
				return true;
			}
			/* file found*/
			File parentFile = file.getParentFile();
			String parent = parentFile.getName();
			if (!"test-results".equals(parent)){
				return false;
			}
			parentFile = parentFile.getParentFile();
			parent = parentFile.getName();
			if (!"build".equals(parent)){
				return false;
			}
			if (name.startsWith("TEST-")){
				if (name.endsWith(".xml")){
					return true;
				}
			}
			return false;
		}
		
	}

	
}
