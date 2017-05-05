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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import de.jcup.egradle.core.api.GradleContextPreparator;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.RememberLastLinesOutputHandler;
import de.jcup.egradle.eclipse.api.EclipseUtil;
import de.jcup.egradle.eclipse.execution.GradleExecutionDelegate;
import de.jcup.egradle.eclipse.execution.GradleExecutionException;
import de.jcup.egradle.eclipse.execution.GradleJob;
import de.jcup.egradle.eclipse.execution.GradleRunnableWithProgress;
import static de.jcup.egradle.eclipse.ide.IdeUtil.*;
/**
 * Abstract base handler for egradle command executions
 * 
 * @author Albert Tregnaghi
 *
 */
public abstract class AbstractEGradleCommandHandler extends AbstractHandler implements GradleContextPreparator {

	public AbstractEGradleCommandHandler() {
		init();
	}

	protected void init() {
	}

	protected enum ExecutionMode {
		BLOCK_UI__CANCEABLE,

		RUN_IN_BACKGROUND__CANCEABLE
	}

	protected ExecutionMode getExecutionMode() {
		return ExecutionMode.RUN_IN_BACKGROUND__CANCEABLE;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		/* create execution and fetch mode */
		GradleExecutionDelegate execution = null;
		try{
			RememberLastLinesOutputHandler validationOutputHandler = EclipseUtil.createOutputHandlerForValidationErrorsOnConsole();
			validationOutputHandler.setChainedOutputHandler(getSystemConsoleOutputHandler());
			execution = createGradleExecution(validationOutputHandler);
		}catch(GradleExecutionException e){
			getDialogSupport().showError(e.getMessage());
			return null;
		}
		ExecutionMode mode = getExecutionMode();

		/* execute */
		switch (mode) {
		case BLOCK_UI__CANCEABLE:
			try {
				GradleRunnableWithProgress runnable = new GradleRunnableWithProgress(execution);
				IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
				progressService.busyCursorWhile(runnable);

			} catch (InvocationTargetException | InterruptedException e) {
				throw new ExecutionException("Cannot execute action...", e);
			}
			break;
		case RUN_IN_BACKGROUND__CANCEABLE:
			GradleJob job = new GradleJob("gradle execution", execution);
			job.schedule();
			break;

		default:
			throw new IllegalArgumentException("Not implemented for mode:" + mode);
		}

		return null;
	}

	protected abstract GradleExecutionDelegate createGradleExecution(OutputHandler outputHandler) throws GradleExecutionException;

}
