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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;

import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.domain.GradleSubproject;
import de.jcup.egradle.core.process.ProcessOutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.execution.EclipseLaunchProcessExecutor;
import de.jcup.egradle.eclipse.execution.GradleExecutionDelegate;
import de.jcup.egradle.eclipse.launch.EGradleLaunchConfigurationMainTab;
import de.jcup.egradle.eclipse.launch.EGradleLaunchConfigurationTabGroup;
import de.jcup.egradle.eclipse.launch.EGradleLaunchDelegate;
import de.jcup.egradle.eclipse.launch.EGradleRuntimeProcess;

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

	private GradleCommand[] commands;
	private ILaunch launch;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IParameter configparameter = event.getCommand().getParameter(PARAMETER_LAUNCHCONFIG);
			IParameterValues configParamValues = configparameter.getValues();
			Map<?, ?> values = configParamValues.getParameterValues();
			String projectName = (String) values.get(EGradleLaunchConfigurationMainTab.PROPERTY_PROJECTNAME);
			String commandString = (String) values.get(EGradleLaunchConfigurationMainTab.PROPERTY_TASKS);
			launch = (ILaunch) values.get(EGradleLaunchDelegate.LAUNCH_ARGUMENT);

			String[] commandStrings = commandString.split(" ");
			if (StringUtils.isEmpty(projectName)) {
				this.commands = GradleCommand.build(commandStrings);
			} else {
				this.commands = GradleCommand.build(new GradleSubproject(projectName), commandStrings);
			}

		} catch (NotDefinedException | ParameterValuesException e) {
			throw new IllegalStateException("Cannot fetch command parameter!", e);
		}
		return super.execute(event);
	}

	@Override
	protected void additionalPrepareContext(GradleContext context) {
		if (launch != null) {
			ILaunchConfiguration configuration = launch.getLaunchConfiguration();
			try {
				Map<String, String> gradleProperties = configuration
						.getAttribute(EGradleLaunchConfigurationTabGroup.GRADLE_PROPERTIES, Collections.emptyMap());
				Map<String, String> systemProperties = configuration
						.getAttribute(EGradleLaunchConfigurationTabGroup.SYSTEM_PROPERTIES, Collections.emptyMap());

				context.getGradleProperties().putAll(gradleProperties);
				context.getSystemProperties().putAll(systemProperties);

				/* replace variables with content */
				extractVariables(context.getGradleProperties());
				extractVariables(context.getSystemProperties());

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

	protected GradleExecutionDelegate createGradleExecution(ProcessOutputHandler processOutputHandler,
			GradleContext context) {
		return new GradleExecutionDelegate(processOutputHandler, context,
				new EclipseLaunchProcessExecutor(processOutputHandler, context, launch));
	}

	@Override
	protected GradleCommand[] createCommands() {
		return commands;
	}

}
