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

import static de.jcup.egradle.eclipse.launch.EGradleLauncherConstants.*;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.IParameterValues;
import org.eclipse.core.commands.ParameterValuesException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.domain.GradleSubproject;
import de.jcup.egradle.core.process.ProcessOutputHandler;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.execution.EclipseLaunchProcessExecutor;
import de.jcup.egradle.eclipse.execution.GradleExecutionDelegate;

/**
 * This handler is only for launching. So complete mechanism is same as on
 * normal handlers but it supports (and needs) a launch object as parameter as
 * well! <br>
 * </br>
 * The handler does produce a RuntimeProcess object which consumes console
 * output to handler
 * 
 * @author Albert Tregnaghi
 *
 */
public class LaunchGradleCommandHandler extends AbstractEGradleCommandHandler {

	public static final String COMMAND_ID = "egradle.commands.launch";
	public static final String PARAMETER_LAUNCHCONFIG = "egradle.command.launch.config";

	
	private ILaunch launch;
	private Job postJob;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IParameter configparameter = event.getCommand().getParameter(PARAMETER_LAUNCHCONFIG);
			IParameterValues configParamValues = configparameter.getValues();
			Map<?, ?> values = configParamValues.getParameterValues();

			launch = (ILaunch) values.get(LAUNCH_ARGUMENT);
			postJob = (Job) values.get(LAUNCH_POST_JOB);

		} catch (NotDefinedException | ParameterValuesException e) {
			throw new IllegalStateException("Cannot fetch command parameter!", e);
		}
		return super.execute(event);
	}

	@Override
	public void prepare(GradleContext context) {
		if (launch != null) {
			ILaunchConfiguration configuration = launch.getLaunchConfiguration();
			try {
				/* commands */
				String projectName = configuration.getAttribute(PROPERTY_PROJECTNAME,
						"");
				String commandString = configuration.getAttribute(PROPERTY_TASKS, "");

				String[] commandStrings = commandString.split(" ");
				GradleCommand[] commands = null;
				if (StringUtils.isEmpty(projectName)) {
					commands = GradleCommand.build(commandStrings);
				} else {
					commands = GradleCommand.build(new GradleSubproject(projectName), commandStrings);
				}
				context.setCommands(commands);

				/* raw options */
				String options = configuration.getAttribute(PROPERTY_OPTIONS, "");
				String[] splittedOptions = options.split(" ");
				context.setOptions(splittedOptions);

				/*
				 * system properties, gradle project properties and enviroment
				 */
				Map<String, String> gradleProperties = configuration
						.getAttribute(GRADLE_PROPERTIES, Collections.emptyMap());
				Map<String, String> systemProperties = configuration
						.getAttribute(SYSTEM_PROPERTIES, Collections.emptyMap());
				Map<String, String> environment = configuration.getAttribute(
						ENVIRONMENT_PROPERTIES, Collections.emptyMap());

				context.getGradleProperties().putAll(gradleProperties);
				context.getSystemProperties().putAll(systemProperties);
				context.getEnvironment().putAll(environment);

				/* replace variables with content */
				extractVariables(context.getGradleProperties());
				extractVariables(context.getSystemProperties());
				extractVariables(context.getEnvironment());

			} catch (CoreException e) {
				EGradleUtil.log(e);
			}
		}
	}

	private void extractVariables(Map<String, String> map) throws CoreException {
		IStringVariableManager svManager = VariablesPlugin.getDefault().getStringVariableManager();
		for (String key : map.keySet()) {
			String value = map.get(key);
			String newValue = svManager.performStringSubstitution(value);
			map.put(key, newValue);
		}
	}

	protected GradleExecutionDelegate createGradleExecution(ProcessOutputHandler processOutputHandler) {
		return new GradleExecutionDelegate(processOutputHandler,
				new EclipseLaunchProcessExecutor(processOutputHandler, launch){
			@Override
			protected void handleProcessEnd(Process p) {
				if (postJob!=null){
					postJob.schedule();
				}
			}
		}, this);
	}

}
