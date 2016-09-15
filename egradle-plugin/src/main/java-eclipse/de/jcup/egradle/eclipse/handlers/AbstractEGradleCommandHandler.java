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

import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.PREFERENCES;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import de.jcup.egradle.core.config.AlwaysBashWithGradleWrapperConfiguration;
import de.jcup.egradle.core.config.GradleConfiguration;
import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.process.ProcessOutputHandler;
import de.jcup.egradle.eclipse.EGradleMessageDialog;
import de.jcup.egradle.eclipse.console.EGradleSystemConsoleProcessOutputHandler;
import de.jcup.egradle.eclipse.execution.GradleExecutionDelegate;
import de.jcup.egradle.eclipse.execution.GradleJob;
import de.jcup.egradle.eclipse.execution.GradleRunnableWithProgress;
import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.PreferenceConstants.*;

/**
 * Abstract base handler for egradle command executions
 * 
 * @author Albert Tregnaghi
 *
 */
public abstract class AbstractEGradleCommandHandler extends AbstractHandler {

	protected ProcessOutputHandler processOutputHandler;

	public AbstractEGradleCommandHandler() {
		init();
	}

	protected void init() {
		this.processOutputHandler = new EGradleSystemConsoleProcessOutputHandler();
	}

	protected abstract GradleCommand[] createCommands();

	private void prepareContext(GradleContext context) {
		String javaHome = PREFERENCES.getStringPreference(P_JAVA_HOME_PATH);
		if (StringUtils.isEmpty(javaHome)) {
			EGradleMessageDialog.INSTANCE.showError("No java home path set. Please setup in preferences!");
			throw new IllegalStateException("Java home not set");
		}
		context.setEnvironment("JAVA_HOME", javaHome);
		context.setCommands(createCommands());
		context.setAmountOfWorkToDo(1);

		additionalPrepareContext(context);

	}

	protected void additionalPrepareContext(GradleContext context) {
		/* can be overriden */
	}

	protected enum ExecutionMode {
		BLOCK_UI__CANCEABLE, RUN_IN_BACKGROUND__CANCEABLE
	}

	protected ExecutionMode getExecutionMode() {
		return ExecutionMode.RUN_IN_BACKGROUND__CANCEABLE;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		String path = PREFERENCES.getStringPreference(P_ROOTPROJECT_PATH);
		if (StringUtils.isEmpty(path)) {
			EGradleMessageDialog.INSTANCE.showError("No root project path set. Please setup in preferences!");
			return null;
		}
		GradleRootProject rootProject;
		try {
			rootProject = new GradleRootProject(new File(path));
		} catch (IOException e1) {
			EGradleMessageDialog.INSTANCE.showError(e1.getMessage());
			return null;
		}

		/* build configuration for gradle run */
		GradleConfiguration config = new AlwaysBashWithGradleWrapperConfiguration();

		/* build context */
		GradleContext context = new GradleContext(rootProject, config);
		prepareContext(context);

		/* create execution and fetch mode */
		GradleExecutionDelegate execution = createGradleExecution(processOutputHandler, context);
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

	protected GradleExecutionDelegate createGradleExecution(ProcessOutputHandler processOutputHandler,
			GradleContext context) {
		return new GradleExecutionDelegate(processOutputHandler, context);
	}

}
