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

import org.eclipse.core.runtime.IProgressMonitor;

import de.jcup.egradle.core.api.GradleContextPreparator;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.ProcessExecutor;
import de.jcup.egradle.eclipse.api.EGradleUtil;

public class UIGradleExecutionDelegate extends GradleExecutionDelegate {

	boolean refreshAllProjects = true;
	boolean showEGradleSystemConsole = true;

	public UIGradleExecutionDelegate(OutputHandler outputHandler, ProcessExecutor processExecutor, GradleContextPreparator additionalContextPreparator) {
		super(outputHandler,processExecutor,additionalContextPreparator);
	}

	@Override
	protected void beforeExecutionDone(IProgressMonitor monitor) throws Exception {
		if (showEGradleSystemConsole) {
			EGradleUtil.openSystemConsole();
		}
	}

	protected void afterExecutionDone(IProgressMonitor monitor) throws Exception {
		monitor.worked(1);
		if (refreshAllProjects) {
			EGradleUtil.refreshAllProjects(monitor);
		}
		monitor.worked(2);
	}
}