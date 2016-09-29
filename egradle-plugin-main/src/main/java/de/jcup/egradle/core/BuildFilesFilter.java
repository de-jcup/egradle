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
	 * @param allowSubFolders - when <code>true</code> sub folders of current directory are all accepted, otherwise not
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