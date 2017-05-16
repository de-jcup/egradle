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

import static de.jcup.egradle.eclipse.ide.launch.EGradleLauncherConstants.*;

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
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.domain.GradleSubproject;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.ide.execution.EclipseLaunchProcessExecutor;
import de.jcup.egradle.eclipse.ide.execution.GradleExecutionDelegate;
import de.jcup.egradle.eclipse.ide.execution.GradleExecutionException;
import de.jcup.egradle.eclipse.ide.launch.LaunchParameterValues;
import de.jcup.egradle.eclipse.util.EGradlePostBuildJob;

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
	private EGradlePostBuildJob postJob;
	private String taskAttributeOverride;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IParameter configparameter = event.getCommand().getParameter(PARAMETER_LAUNCHCONFIG);
			IParameterValues values = configparameter.getValues();
			if (values instanceof LaunchParameterValues){
				LaunchParameterValues launchParameterValues = (LaunchParameterValues) values;
				taskAttributeOverride = launchParameterValues.getOverriddenTasks();
				launch = launchParameterValues.getLaunch();
				postJob = launchParameterValues.getPostJob();
				
			}else{
				IDEUtil.logWarning(getClass().getSimpleName()+":parameter values without being a launch parameter value was used !??! :"+ values);
			}

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
				String commandString = null;
				if (taskAttributeOverride==null){
					commandString= configuration.getAttribute(PROPERTY_TASKS, "");
				}else{
					commandString=taskAttributeOverride;
				}

				GradleCommand[] commands = null;
				if (StringUtils.isEmpty(projectName)) {
					commands = GradleCommand.build(commandString);
				} else {
					commands = GradleCommand.build(new GradleSubproject(projectName), commandString);
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
				IDEUtil.logError("Was not able to prepare launchconfiguration", e);
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

	protected GradleExecutionDelegate createGradleExecution(OutputHandler outputHandler) throws GradleExecutionException {
		return new GradleExecutionDelegate(outputHandler,
				new EclipseLaunchProcessExecutor(outputHandler, launch,postJob), this, null);
	}

}
