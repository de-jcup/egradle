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

import static org.apache.commons.lang3.Validate.notNull;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;

import de.jcup.egradle.core.GradleExecutor;
import de.jcup.egradle.core.GradleExecutor.Result;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.process.ProcessExecutor;
import de.jcup.egradle.core.process.ProcessOutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;

public class EclipseGradleExecution {

	private GradleContext context;
	private ProcessOutputHandler processOutputHandler;
	private Result result;
	private ProcessExecutor processExecutor;

	public Result getResult() {
		return result;
	}

	public EclipseGradleExecution(ProcessOutputHandler processOutputHandler, GradleContext context) {
		this(processOutputHandler, context, new SimpleProcessExecutor(processOutputHandler));
	}

	public EclipseGradleExecution(ProcessOutputHandler processOutputHandler, GradleContext context,
			ProcessExecutor processExecutor) {
		notNull(context, "'context' may not be null");
		notNull(processOutputHandler, "'processOutputHandler' may not be null");
		notNull(processExecutor, "'processExecutor' may not be null");
		this.context = context;
		this.processOutputHandler = processOutputHandler;
		this.processExecutor=processExecutor;
	}

	public void execute(IProgressMonitor monitor) throws Exception {
		GradleExecutor executor = new GradleExecutor(processExecutor);

		// do non-UI work
		String commandString = context.getCommandString();
		monitor.beginTask("Executing gradle commands:" + commandString, context.getAmountOfWorkToDo());
		processOutputHandler.output("\n" + DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()));
		processOutputHandler.output(
				"Root project '" + context.getRootProject().getFolder().getName() + "' executing " + commandString);

		result = executor.execute(context);
		if (!result.isOkay()) {
			return;
		}
		try {
			afterExecutionDone(monitor);
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}

		monitor.done();
	}

	protected void afterExecutionDone(IProgressMonitor monitor) throws Exception {
		/* per default do nothing */
	}
}