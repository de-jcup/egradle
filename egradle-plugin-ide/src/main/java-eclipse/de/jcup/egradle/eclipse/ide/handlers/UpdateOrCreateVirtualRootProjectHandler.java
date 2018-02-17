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

import static de.jcup.egradle.eclipse.ide.IDEUtil.*;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.virtualroot.VirtualRootProjectException;
import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.util.EclipseUtil;

public class UpdateOrCreateVirtualRootProjectHandler extends AbstractHandler implements IElementUpdater {

	public static final String COMMAND_ID = "egradle.commands.updateOrCreateVirtualRootProject";

	private boolean running = false;

	@Override
	public boolean isEnabled() {
		return super.isEnabled();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (running) {
			getDialogSupport().showWarning("Virtual root project (re-)creation already running. Please wait");
			return null;
		}
		running = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					createOrRecreateVirtualRootProject();
				} catch (VirtualRootProjectException e) {
					IDEUtil.logError("Was not able to (re)create virtual root project", e);
					getDialogSupport().showError("Virtual root project not (re)createable. Please try again");
				} finally {
					running = false;
				}
			}
		}, "Update virtual root project").start();
		;

		return null;
	}

	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		GradleRootProject rootProject = IDEUtil.getRootProject();
		if (rootProject == null) {
			element.setTooltip("Disabled because currently no egradle root project set!");
			this.setBaseEnabled(false);
		} else 
			
			if (!rootProject.isMultiProject()) {
			element.setTooltip("Disabled because a virtual rooot project is not necessary for single project '"+rootProject.getName()+"'");
			this.setBaseEnabled(false);
		} else {
			element.setTooltip("Creates or updates virtual root project for your gradle multi project '"+rootProject.getName()+"'");
			this.setBaseEnabled(true);
		}
	}

	public static void requestRefresh() {
		IWorkbenchWindow window = EclipseUtil.getActiveWorkbenchWindow();
		if (window == null) {
			IDEUtil.logWarning("Cannot handle refresh for update/create vroot project - no active window");
			return;
		}
		ICommandService commandService = (ICommandService) window.getService(ICommandService.class);
		if (commandService == null) {
			IDEUtil.logWarning("Cannot handle refresh for update/create vroot project - no command service");
			return;
		}
		commandService.refreshElements(COMMAND_ID, null);
	}

}
