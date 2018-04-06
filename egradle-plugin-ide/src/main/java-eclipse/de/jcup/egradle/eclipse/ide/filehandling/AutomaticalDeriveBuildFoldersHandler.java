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
package de.jcup.egradle.eclipse.ide.filehandling;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import de.jcup.egradle.eclipse.ide.IDEUtil;

public class AutomaticalDeriveBuildFoldersHandler {

	private static final IProgressMonitor NULL_PROGRESS = new NullProgressMonitor();

	/**
	 * Will derive build folders of given project automatically, if the feature
	 * is enabled in preferences
	 * 
	 * @param project
	 * @param monitor
	 * @throws CoreException
	 *             - if derive fails
	 */
	public void deriveBuildFolders(IProject project, IProgressMonitor monitor) throws CoreException {
		if (!IDEUtil.getPreferences().isAutomaticallyDeriveBuildFoldersEnabled()) {
			return;
		}
		if (!project.exists()) {
			/* project not existing, so ignore */
			return;
		}
		if (!project.isOpen()) {
			/* project not opened , so ignore */
			return;
		}
		IFolder folder = project.getFolder("build");
		if (!folder.exists()) {
			/* no build folder so ignore */
			return;
		}
		if (folder.isDerived()) {
			/* already derived... so ignore */
			return;
		}
		if (monitor == null) {
			monitor = NULL_PROGRESS;
		}
		folder.setDerived(true, monitor);
	}
}
