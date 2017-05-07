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

import static org.apache.commons.lang3.Validate.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;

import de.jcup.egradle.core.GradleExecutor;
import de.jcup.egradle.core.ProcessExecutionResult;
import de.jcup.egradle.core.api.GradleContextPreparator;
import de.jcup.egradle.core.config.MutableGradleConfiguration;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.process.EGradleShellType;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.ProcessExecutor;
import de.jcup.egradle.core.process.RememberLastLinesOutputHandler;
import de.jcup.egradle.eclipse.ide.IdeUtil;
import de.jcup.egradle.eclipse.preferences.EGradleIdePreferences;
import de.jcup.egradle.eclipse.ui.ProgressMonitorCancelStateProvider;

/**
 * Execution delegate, used by {@link GradleJob} and
 * {@link GradleRunnableWithProgress}
 * 
 * @author Albert Tregnaghi
 *
 */
public class GradleExecutionDelegate {

	private GradleContext context;
	private OutputHandler outputHandler;
	private ProcessExecutionResult processExecutionResult;
	protected GradleExecutor executor;

	public ProcessExecutionResult getResult() {
		return processExecutionResult;
	}

	/**
	 * Creates a new gradle execution delegate
	 * @param outputHandler
	 * @param processExecutor
	 * @param additionalContextPreparator
	 * @param rootProject if null rootProject will be resolved by preferences
	 * @throws GradleExecutionException
	 */
	public GradleExecutionDelegate(OutputHandler outputHandler, ProcessExecutor processExecutor,
			GradleContextPreparator additionalContextPreparator, GradleRootProject rootProject) throws GradleExecutionException {
		notNull(outputHandler, "'systemConsoleOutputHandler' may not be null");
		notNull(processExecutor, "'processExecutor' may not be null");

		if (rootProject==null){
			rootProject = IdeUtil.getRootProject(false);
		}
		if (rootProject == null) {
			/*
			 * we handle the error on creation time by own exception thrown -
			 * without IdeUtil error dialog
			 */
			throw new GradleExecutionException("Execution not possible - undefined or unexisting root project!");
		}
		this.outputHandler = outputHandler;

		context = createContext(rootProject);
		if (additionalContextPreparator != null) {
			additionalContextPreparator.prepare(context);
		}
		executor = new GradleExecutor(processExecutor);
	}

	protected GradleContext createContext(GradleRootProject rootProject) throws GradleExecutionException {
		EGradleIdePreferences preferences = IdeUtil.getPreferences();
		/* Default JAVA_HOME */
		String globalJavaHome = preferences.getGlobalJavaHomePath();
		/* Call gradle settings */
		String gradleCommand = preferences.getGradleCallCommand();
		String gradleInstallPath = preferences.getGradleBinInstallFolder();
		
		String shellId = preferences.getGradleShellId();
		
		return createContext(rootProject, globalJavaHome, gradleCommand, gradleInstallPath, shellId);
	}

	protected final GradleContext createContext(GradleRootProject rootProject, String globalJavaHome, String gradleCommand,
			String gradleInstallPath, String shellId) throws GradleExecutionException {
		/* build configuration for gradle run */
		MutableGradleConfiguration config = new MutableGradleConfiguration();
		/* build context */
		GradleContext context = new GradleContext(rootProject, config);
		
		if (!StringUtils.isEmpty(globalJavaHome)) {
			context.setEnvironment("JAVA_HOME", globalJavaHome); // JAVA_HOME
																	// still can
																	// be
																	// overriden
																	// by
																	// context
																	// preparator
																	// see below
		}
		context.setAmountOfWorkToDo(1);

		if (StringUtils.isEmpty(gradleCommand)) {
			throw new GradleExecutionException("Preferences have no gradle command set, cannot execute!");
		}

		config.setShellCommand(EGradleShellType.findById(shellId));
		config.setGradleBinDirectory(gradleInstallPath);
		config.setGradleCommand(gradleCommand);
		config.setWorkingDirectory(rootProject.getFolder().getAbsolutePath());
		return context;
	}

	/**
	 * Execute and give output by given progress monitor
	 * 
	 * @param monitor
	 *            - progress monitor
	 * @throws Exception
	 */
	public void execute(IProgressMonitor monitor) throws Exception {
		processExecutionResult = new ProcessExecutionResult();
		try {
			GradleRootProject rootProject = context.getRootProject();
			String commandString = context.getCommandString();
			String progressDescription = "Executing gradle commands:" + commandString + " in "
					+ context.getRootProject().getFolder().getAbsolutePath();

			File folder = rootProject.getFolder();
			String rootProjectFolderName = folder.getName();
			String executionStartTime = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
					.format(new Date());

			monitor.beginTask(progressDescription, context.getAmountOfWorkToDo());
			if (monitor.isCanceled()) {
				return;
			}
			beforeExecutionDone(monitor);
			if (monitor.isCanceled()) {
				return;
			}
			outputHandler.output("\n" + executionStartTime + " " + progressDescription);
			outputHandler
					.output("Root project '" + rootProjectFolderName + "' executing " + commandString);

			if (monitor.isCanceled()) {
				outputHandler.output("[CANCELED]");
				processExecutionResult.setCanceledByuser(true);
				return;
			}
			ProgressMonitorCancelStateProvider cancelStateProvider = new ProgressMonitorCancelStateProvider(monitor);
			context.register(cancelStateProvider);
			
			processExecutionResult = executor.execute(context);
			if (processExecutionResult.isOkay()) {
				outputHandler.output("[OK]");
			} else {
				outputHandler.output("[FAILED]");
			}
			afterExecutionDone(monitor);
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		} finally {
			monitor.done();
		}

	}

	protected void beforeExecutionDone(IProgressMonitor monitor) throws Exception {
		if (outputHandler instanceof RememberLastLinesOutputHandler){
			IdeUtil.removeAllValidationErrorsOfConsoleOutput();
		}
	}

	protected void afterExecutionDone(IProgressMonitor monitor) throws Exception {
		if (outputHandler instanceof RememberLastLinesOutputHandler){
			RememberLastLinesOutputHandler validationOutputHandler = (RememberLastLinesOutputHandler) outputHandler;
			List<String> list = validationOutputHandler.createOutputToValidate();
			IdeUtil.showValidationErrorsOfConsoleOutput(list);
		}
	}

}