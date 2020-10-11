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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GradleImportScanner {

    public static final String ECLIPSE_PROJECTFILE_NAME = ".project";
    private static final FileFilter ECLIPSE_PROJECT_FILTER = new EclipseProjectFileFilter();

    public GradleImportScanner() {
    }

    /**
     * Returns folders containing eclipse .project files. If its a multiproject only
     * subproject folders are returned. when its a single project only the folder
     * itself will be returned
     * 
     * @param folder
     * @return folder list, never <code>null</code>
     */
    public List<File> scanEclipseProjectFolders(File folder) {
        if (folder == null) {
            return Collections.emptyList();
        }

        List<File> list = new ArrayList<>();
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                /* check we do not import virtual root projects... */
                if (Constants.VIRTUAL_ROOTPROJECT_FOLDERNAME.equals(file.getName())) {
                    continue;
                }
                File[] projectFiles = file.listFiles(ECLIPSE_PROJECT_FILTER);
                if (projectFiles.length > 0) {
                    list.add(file);
                }
            }
        }

        /* single project support: */
        if (list.isEmpty()) {
            /* if no sub projects found scan if this is a single project */

            File[] projectFiles = folder.listFiles(ECLIPSE_PROJECT_FILTER);
            if (projectFiles.length > 0) {
                list.add(folder);
            }
        } else {
            /*
             * we have found children being importable - so we ignore the root project at
             * all because prefering always virtual root project...
             */
        }
        return list;
    }

    private static class EclipseProjectFileFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            if (file == null) {
                return false;
            }
            if (file.isDirectory()) {
                return false;
            }
            return ECLIPSE_PROJECTFILE_NAME.equals(file.getName());
        }

    }

}
