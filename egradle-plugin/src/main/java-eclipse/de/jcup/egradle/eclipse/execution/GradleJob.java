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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.jcup.egradle.eclipse.Activator;
import de.jcup.egradle.eclipse.EGradleMessageDialog;

public class GradleJob extends Job{
	private GradleExecution execution;
	
	public GradleJob(String name, GradleExecution execution) {
		super(name);
		this.execution=execution;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			execution.execute(monitor);
			if (!execution.getResult().isOkay()) {
				EGradleMessageDialog.INSTANCE.showWarning("Result of job was not okay:" + execution.getResult().getResultCode());
			}
		} catch (Exception e) {
			return new Status(Status.ERROR, Activator.PLUGIN_ID, "Cannot execute "+getName(), e);
		}
		return Status.OK_STATUS;
	}
	
}