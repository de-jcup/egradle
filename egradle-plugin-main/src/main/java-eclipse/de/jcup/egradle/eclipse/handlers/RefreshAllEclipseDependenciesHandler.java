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

import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.process.ProcessOutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.eclipse.execution.GradleExecutionDelegate;
import de.jcup.egradle.eclipse.execution.UIGradleExecutionDelegate;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class RefreshAllEclipseDependenciesHandler extends AbstractEGradleCommandHandler {

	@Override
	public void prepare(GradleContext context) {
		context.setAmountOfWorkToDo(2);
	}

	@Override
	protected GradleExecutionDelegate createGradleExecution(ProcessOutputHandler processOutputHandler) {
		return new UIGradleExecutionDelegate(processOutputHandler,new SimpleProcessExecutor(processOutputHandler),this,createCommands());
	}

	@Override
	protected GradleCommand[] createCommands() {
		return GradleCommand.build("cleanEclipse", "eclipse");
	}

}
