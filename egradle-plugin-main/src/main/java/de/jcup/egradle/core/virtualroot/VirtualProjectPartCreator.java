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

import java.io.File;

/**
 * Creates parts for virtual project
 * 
 * @author Albert Tregnaghi
 *
 */
public interface VirtualProjectPartCreator {

    /**
     * Creates the project representation itself
     * 
     * @param projectName
     * @return project
     * @throws VirtualRootProjectException
     */
    public Object createOrRecreateProject(String projectName) throws VirtualRootProjectException;

    /**
     * Creates a link to a folder or a file
     * 
     * @param targetParentFolder
     * @param file
     * @throws VirtualRootProjectException
     */
    public void createLink(Object targetParentFolder, File file) throws VirtualRootProjectException;

    /**
     * Check if given file has to be linked or not inside root project
     * 
     * @param targetParentFolder
     * @param file
     * @return <code>true</code> when file shall be linked into virtual project,
     *         otherwise <code>false</code>
     * @throws VirtualRootProjectException
     */
    public boolean isLinkCreationNeeded(Object targetParentFolder, File file) throws VirtualRootProjectException;

    /**
     * Set maximum of work
     * 
     * @param max
     */
    public void setMaximumLinksToCreate(int max);

}