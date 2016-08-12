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
 package de.jcup.egradle.eclipse.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.process.ProcessOutputHandler;
import de.jcup.egradle.eclipse.execution.GradleExecutionDelegate;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class RefreshAllEclipseDependenciesHandler extends AbstractEGradleCommandHandler {

	@Override
	protected void additionalPrepareContext(GradleContext context) {
		context.setAmountOfWorkToDo(2);
	}

	@Override
	protected GradleExecutionDelegate createGradleExecution(ProcessOutputHandler processOutputHandler, GradleContext context) {
		return new RefreshAllEclipseProjectsGradleExecution(processOutputHandler, context);
	}

	@Override
	protected GradleCommand[] createCommands() {
		return GradleCommand.build("cleanEclipse", "eclipse");
	}

	private final class RefreshAllEclipseProjectsGradleExecution extends GradleExecutionDelegate {
		private RefreshAllEclipseProjectsGradleExecution(ProcessOutputHandler processOutputHandler,
				GradleContext context) {
			super(processOutputHandler, context);
		}

		protected void afterExecutionDone(IProgressMonitor monitor) throws Exception {
			monitor.worked(1);
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
			monitor.worked(2);
		}
	}

}
