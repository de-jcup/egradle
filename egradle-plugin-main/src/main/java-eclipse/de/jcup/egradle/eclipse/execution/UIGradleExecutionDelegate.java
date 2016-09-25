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
 package de.jcup.egradle.eclipse.execution;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;

import de.jcup.egradle.core.api.GradleContextPreparator;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.ProcessExecutor;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.console.EGradleSystemConsoleFactory;

public class UIGradleExecutionDelegate extends GradleExecutionDelegate {

	boolean refreshAllProjects = true;
	boolean showEGradleSystemConsole = true;

	public UIGradleExecutionDelegate(OutputHandler outputHandler, ProcessExecutor processExecutor, GradleContextPreparator additionalContextPreparator) {
		super(outputHandler,processExecutor,additionalContextPreparator);
	}

	@Override
	protected void beforeExecutionDone(IProgressMonitor monitor) throws Exception {
		if (showEGradleSystemConsole) {
			EGradleUtil.safeAsyncExec(new Runnable() {

				@Override
				public void run() {
					IConsole eGradleSystemConsole = EGradleSystemConsoleFactory.INSTANCE.getConsole();
					IWorkbenchPage page = EGradleUtil.getActivePage();
					String id = IConsoleConstants.ID_CONSOLE_VIEW;
					IConsoleView view;
					try {
						view = (IConsoleView) page.showView(id);
						view.display(eGradleSystemConsole);
					} catch (PartInitException e) {
						EGradleUtil.log(e);
					}
				}

			});

		}
	}

	protected void afterExecutionDone(IProgressMonitor monitor) throws Exception {
		monitor.worked(1);
		if (refreshAllProjects) {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (IProject project : projects) {
				try {
					if (monitor.isCanceled()) {
						break;
					}
					monitor.subTask("refreshing project " + project.getName());
					project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				}
			}
		}
		monitor.worked(2);
	}
}