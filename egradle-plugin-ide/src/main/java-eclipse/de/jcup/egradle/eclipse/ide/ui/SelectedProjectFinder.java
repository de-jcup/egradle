/*
 * Copyright 2022 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.egradle.eclipse.ide.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class SelectedProjectFinder {

    public IProject findSelectedProject() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return null;
        }

        IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
        if (selection == null) {
            return null;
        }

        Object firstElement = selection.getFirstElement();
        if (!(firstElement instanceof IAdaptable)) {
            return null;
        }
        IAdaptable adaptable = (IAdaptable) firstElement;
        IResource resource = (IResource) adaptable.getAdapter(IResource.class);
        if (resource != null) {
            return resource.getProject();
        }
        return null;
    }
}
