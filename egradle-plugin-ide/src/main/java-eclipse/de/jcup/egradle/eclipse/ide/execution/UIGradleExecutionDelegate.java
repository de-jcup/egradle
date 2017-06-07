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
import de.jcup.egradle.eclipse.util.EclipseUtil;

public class UIGradleExecutionDelegate extends GradleExecutionDelegate {

	private boolean refreshAllProjects = true;
	private boolean showEGradleSystemConsole = true;
	private boolean cleanAllProjects;
	private boolean buildAfterClean;

	
	public UIGradleExecutionDelegate(OutputHandler outputHandler, ProcessExecutor processExecutor,
			GradleContextPreparator additionalContextPreparator) throws GradleExecutionException {
		this(outputHandler, processExecutor, additionalContextPreparator,null);
	}

	public UIGradleExecutionDelegate(OutputHandler outputHandler, ProcessExecutor processExecutor,
			GradleContextPreparator additionalContextPreparator, GradleRootProject rootProject) throws GradleExecutionException {
		super(outputHandler, processExecutor, additionalContextPreparator,rootProject);
	}

	public void setRefreshAllProjects(boolean refreshAllProjects) {
		this.refreshAllProjects = refreshAllProjects;
	}
	
	public void setShowEGradleSystemConsole(boolean showEGradleSystemConsole) {
		this.showEGradleSystemConsole = showEGradleSystemConsole;
	}
	
	
	public void setCleanAllProjects(boolean cleanAllProjects, boolean buildAfterClean) {
		this.cleanAllProjects=cleanAllProjects;
		this.buildAfterClean=buildAfterClean;
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
		if (refreshAllProjects) {
			refreshAllProjects(monitor);
		}
		if (cleanAllProjects) {
			IWorkbenchWindow window = EclipseUtil.getActiveWorkbenchWindow();
			cleanAllProjects(buildAfterClean, window, monitor);
		}
		super.afterExecutionDone(monitor);
		monitor.worked(2);
	}

	
}