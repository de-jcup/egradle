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

import de.jcup.egradle.core.domain.GradleRootProject;

public class VirtualProjectCreator {

    /**
     * Creates or update a virtual root project. If a former root project exists, it
     * will be removed!
     * 
     * @param rootProject
     * @param partCreator
     * @return virtual root project or null when noot existing;
     * @throws VirtualRootProjectException
     */
    public Object createOrUpdate(GradleRootProject rootProject, VirtualProjectPartCreator partCreator) throws VirtualRootProjectException {
        if (rootProject == null) {
            return null;
        }
        File rootFolder = rootProject.getFolder();
        if (rootFolder == null) {
            return null;
        }
        if (!rootFolder.exists()) {
            return null;
        }

        Object project = partCreator.createOrRecreateProject(VIRTUAL_ROOTPROJECT_NAME);
        if (project == null) {
            throw new VirtualRootProjectException("Was not able create or recreate '" + VIRTUAL_ROOTPROJECT_NAME + "'");
        }
        addLinksAndMissingFolders(project, partCreator, rootFolder, rootProject);
        return project;
    }

    private void addLinksAndMissingFolders(Object targetParentFolder, VirtualProjectPartCreator v, File folderToScan, GradleRootProject rootProject) throws VirtualRootProjectException {
        File[] buildFiles = folderToScan.listFiles();
        if (buildFiles == null) {
            return;
        }
        v.setMaximumLinksToCreate(buildFiles.length);
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
                if (v.isLinkCreationNeeded(targetParentFolder, file)) {
                    v.createLink(targetParentFolder, file);
                }
            } else {
                if (v.isLinkCreationNeeded(targetParentFolder, file)) {
                    v.createLink(targetParentFolder, file);
                }
            }
        }
    }

}
