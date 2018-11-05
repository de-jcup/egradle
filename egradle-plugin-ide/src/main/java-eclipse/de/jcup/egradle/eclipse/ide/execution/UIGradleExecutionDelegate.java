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
package de.jcup.egradle.eclipse.ide.execution;

import static de.jcup.egradle.eclipse.ide.IDEUtil.*;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchWindow;

import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.ProcessExecutor;
import de.jcup.egradle.core.util.GradleContextPreparator;
import de.jcup.egradle.eclipse.ide.ProjectContext;
import de.jcup.egradle.eclipse.util.EclipseUtil;

public class UIGradleExecutionDelegate extends GradleExecutionDelegate {

	private boolean refreshProjects = true;
	private boolean showEGradleSystemConsole = true;
	private boolean cleanProjects;
	private boolean buildAfterClean;
	private ProjectContext projectContext;
	private Runnable afterExecution;

	public UIGradleExecutionDelegate(OutputHandler outputHandler, ProcessExecutor processExecutor,
			GradleContextPreparator additionalContextPreparator) throws GradleExecutionException {
		this(outputHandler, processExecutor, additionalContextPreparator, null);
	}

	public UIGradleExecutionDelegate(OutputHandler outputHandler, ProcessExecutor processExecutor,
			GradleContextPreparator additionalContextPreparator, GradleRootProject rootProject)
			throws GradleExecutionException {
		super(outputHandler, processExecutor, additionalContextPreparator, rootProject);
	}

	/**
	 * Set project context
	 * 
	 * @param projectContext
	 *            - if null every project in workspace is targeted on multi
	 *            project operations (e.g. clean/build etc.)
	 */
	public void setProjectContext(ProjectContext projectContext) {
		this.projectContext = projectContext;
	}

	public void setRefreshProjects(boolean refreshAllProjects) {
		this.refreshProjects = refreshAllProjects;
	}

	public void setShowEGradleSystemConsole(boolean showEGradleSystemConsole) {
		this.showEGradleSystemConsole = showEGradleSystemConsole;
	}

	public void setCleanProjects(boolean cleanProjects, boolean buildAfterClean) {
		this.cleanProjects = cleanProjects;
		this.buildAfterClean = buildAfterClean;
	}

	@Override
	protected void beforeExecutionDone(IProgressMonitor monitor) throws Exception {
		super.beforeExecutionDone(monitor);
		if (showEGradleSystemConsole) {
			openSystemConsole(true);
		}
	}

	protected void afterExecutionDone(IProgressMonitor monitor) throws Exception {
		monitor.worked(1);
		if (refreshProjects) {
			refreshProjects(projectContext, monitor);
		}
		if (cleanProjects) {
			IWorkbenchWindow window = EclipseUtil.getActiveWorkbenchWindow();
			cleanProjects(projectContext, buildAfterClean, window, monitor);
		}
		super.afterExecutionDone(monitor);
		monitor.worked(2);

		if (afterExecution != null) {
			afterExecution.run();
		}
	}

	/**
	 * When set, given runnable will executed after execution was successfully
	 * done
	 * 
	 * @param runnable
	 */
	public void setAfterExecution(Runnable runnable) {
		this.afterExecution = runnable;
	}

}