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
package de.jcup.egradle.eclipse.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.services.IServiceLocator;

import de.jcup.egradle.eclipse.api.EGradleUtil;

/**
 * Workaround to get a action running a command - a little bit ugly, but currently I didn't find another way for popup adding.
 * @author Albert Tregnaghi
 *
 */
public abstract class CommandActionDelegate implements IObjectActionDelegate {

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {

		IServiceLocator serviceLocator = (IServiceLocator) PlatformUI.getWorkbench();
		IHandlerService handlerService = (IHandlerService) serviceLocator.getService(IHandlerService.class);

		try {
			handlerService.executeCommand(getCommandId(), null);
		} catch (Exception e) {
			EGradleUtil.log(e);
		}
	}

	protected abstract String getCommandId();
	
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
