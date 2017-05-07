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

import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.eclipse.ide.execution.GradleExecutionDelegate;
import de.jcup.egradle.eclipse.ide.execution.GradleExecutionException;
import de.jcup.egradle.eclipse.ide.execution.UIGradleExecutionDelegate;

public class RefreshAllEclipseDependenciesHandler extends AbstractEGradleCommandHandler {

	public static final String COMMAND_ID = "egradle.commands.refreshEclipse";

	@Override
	public void prepare(GradleContext context) {
		context.setAmountOfWorkToDo(2);
		context.setCommands(GradleCommand.build("cleanEclipse eclipse"));
	}

	@Override
	protected GradleExecutionDelegate createGradleExecution(OutputHandler outputHandler)
			throws GradleExecutionException {
		UIGradleExecutionDelegate ui = new UIGradleExecutionDelegate(outputHandler,
				new SimpleProcessExecutor(outputHandler, true, SimpleProcessExecutor.ENDLESS_RUNNING), this);
		ui.setRefreshAllProjects(true);
		ui.setCleanAllProjects(true, false);
		ui.setShowEGradleSystemConsole(true);
		return ui;
	}

}
