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
package de.jcup.egradle.eclipse.ide.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import de.jcup.egradle.core.GradleRootProjectParentScanner;
import de.jcup.egradle.eclipse.ide.EGradleMessageDialogSupport;
import de.jcup.egradle.eclipse.ide.IdeUtil;
import de.jcup.egradle.eclipse.util.EclipseResourceHelper;

/**
 * Detects new root project
 * 
 * @author Albert Tregnaghi
 *
 */
public class DetectNewRootProjectHandler extends AbstractHandler {

	private GradleRootProjectParentScanner scanner;

	public DetectNewRootProjectHandler() {
		scanner = new GradleRootProjectParentScanner();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		/* get project from selection or do guard close */
		IStructuredSelection ssel = (IStructuredSelection) selection;
		Object element = ssel.getFirstElement();
		IProject project = null;
		if (element instanceof IProject) {
			project = (IProject) element;
		} else if (element instanceof IResource) {
			project = ((IResource) element).getProject();
		} else if (element instanceof IAdaptable){
			IAdaptable adaptable = (IAdaptable) element;
			project = adaptable.getAdapter(IProject.class);
		}
		if (project == null) {
			/* guard close */
			return null;
		}
		File start = null;
		try {
			start = EclipseResourceHelper.DEFAULT.toFile(project);
		} catch (CoreException e) {
			IdeUtil.logError("Was not able to convert to file - project:"+project, e);
			EGradleMessageDialogSupport.INSTANCE.showError("Cannot setup because project conversion problems");
			return null;
		}
		if (start == null) {
			/* should never happen */
			EGradleMessageDialogSupport.INSTANCE.showError("Cannot setup because project file NULL");
			return null;
		}
		/* resolve next root project path */
		File newRootFolder = scanner.scanForParentRootProject(start, 3);
		if (newRootFolder == null) {
			EGradleMessageDialogSupport.INSTANCE
					.showWarning("Was not able to locate a new rootproject from '" + project.getName() + "'");
			return null;
		}
		/* okay - root folder available */
		try {
			IdeUtil.setNewRootProjectFolder(newRootFolder);
		} catch (CoreException e) {
			IdeUtil.logError("Was not able to set new root project folder:"+newRootFolder, e);
			EGradleMessageDialogSupport.INSTANCE.showError(e.getMessage());
			return null;
		}
		return null;
	}

}
