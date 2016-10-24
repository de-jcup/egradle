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

import java.util.List;

import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.RememberLastLinesOutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.execution.GradleExecutionDelegate;
import de.jcup.egradle.eclipse.execution.GradleExecutionException;

public class ValidateGradleScriptHandler extends AbstractEGradleCommandHandler  {


	
	
	@Override
	public void prepare(GradleContext context) {
		context.setAmountOfWorkToDo(2);
		context.setCommands(GradleCommand.build("tasks"));
	}

	@Override
	protected GradleExecutionDelegate createGradleExecution(OutputHandler outputHandler)
			throws GradleExecutionException {
			EGradleUtil.removeAllValidationErrorsOfConsoleOutput();
		RememberLastLinesOutputHandler validationOutputHandler = EGradleUtil.createOutputHandlerForValidationErrorsOnConsole();
		validationOutputHandler.setChainedOutputHandler(outputHandler);
		/* no errors, so erase if there were former ...*/
		GradleExecutionDelegate ui = new GradleExecutionDelegate(outputHandler,
				new SimpleProcessExecutor(validationOutputHandler, true, 10), this) {
			protected void afterExecutionDone(org.eclipse.core.runtime.IProgressMonitor monitor) throws Exception {
				/* we do always remove all buildscript problem markers - so we got only new ones remaining!*/
				
				List<String> list = validationOutputHandler.createOutputToValidate();
				EGradleUtil.showValidationErrorsOfConsoleOutput(list);
			}
		};
		return ui;
	}

	

}
