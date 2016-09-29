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
 package de.jcup.egradle.core.virtualroot;

import static de.jcup.egradle.core.Constants.*;

import java.io.File;
import java.io.FileFilter;

import de.jcup.egradle.core.AcceptAllFilesFilter;
import de.jcup.egradle.core.domain.GradleRootProject;

public class VirtualProjectSupport {

	private static final FileFilter FILES_AND_FOLDERS = new AcceptAllFilesFilter(true);
	
	public void createOrUpdateVirtualRootProject(GradleRootProject rootProject, RootProjectVisitor visitor)
			throws VirtualRootProjectException {
		if (rootProject == null) {
			return;
		}
		File rootFolder = rootProject.getFolder();
		if (rootFolder == null) {
			return;
		}
		if (!rootFolder.exists()){
			return;
		}
		Object project = visitor.createOrRecreateProject(VIRTUAL_ROOTPROJECT_NAME);
		addLinksAndMissingFolders(project, visitor, rootFolder,FILES_AND_FOLDERS, rootProject);

	}

	private void addLinksAndMissingFolders(Object targetParentFolder, RootProjectVisitor v, File folderToScan,FileFilter filter, GradleRootProject rootProject) throws VirtualRootProjectException {
		File[] buildFiles = folderToScan.listFiles();
		if (buildFiles == null) {
			return;
		}
		for (File file : buildFiles) {
			if (file.isDirectory()) {
				/**
				 * <pre>
				 * rootProject
				 *    |
				 *    x-build.gradle
				 *    x-scripts --- only a folder link has to be  created...
				 *    |   |
				 *    |	  x-childscript.gradle
				 *    x-subproject 
				 *    |   |
				 *    |   x-build.gradle
				 *    ...
				 * 
				 * </pre>
				 */
				if (v.needsFolderToBeCreated(targetParentFolder, file)){
					v.createLink(targetParentFolder, file);
				}
			} else {
				v.createLink(targetParentFolder, file);
			}
		}
	}

}
