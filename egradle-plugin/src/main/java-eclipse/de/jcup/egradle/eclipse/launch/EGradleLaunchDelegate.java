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
package de.jcup.egradle.eclipse.launch;

import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.IParameterValues;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterValuesException;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.services.IServiceLocator;

import de.jcup.egradle.eclipse.Activator;
import de.jcup.egradle.eclipse.handlers.LaunchGradleCommandHandler;

public class EGradleLaunchDelegate implements ILaunchConfigurationDelegate {

	public static final String LAUNCH_ARGUMENT = "createRuntimeProcess";

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		String projectName = configuration.getAttribute(EGradleLaunchConfigurationMainTab.PROPERTY_PROJECTNAME, "");
		String arguments = configuration.getAttribute(EGradleLaunchConfigurationMainTab.PROPERTY_TASKS, "");

		executeByHandler(launch, projectName, arguments);

	}

	private void executeByHandler(ILaunch launch, String projectName, String arguments) throws CoreException {
		
		IServiceLocator serviceLocator = (IServiceLocator) PlatformUI.getWorkbench();
		IHandlerService handlerService = (IHandlerService) serviceLocator.getService(IHandlerService.class);
		ICommandService commandService = (ICommandService) serviceLocator.getService(ICommandService.class);

		Command command = commandService.getCommand(LaunchGradleCommandHandler.COMMAND_ID);

		try {
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					try {
						IParameter parameter = command.getParameter(LaunchGradleCommandHandler.PARAMETER_LAUNCHCONFIG);
						IParameterValues values = parameter.getValues();
						@SuppressWarnings("unchecked")
						Map<Object, Object> map = values.getParameterValues();
						map.put(EGradleLaunchConfigurationMainTab.PROPERTY_PROJECTNAME, projectName);
						map.put(EGradleLaunchConfigurationMainTab.PROPERTY_TASKS, arguments);
						map.put(LAUNCH_ARGUMENT, launch);

						Parameterization[] params = new Parameterization[] { new Parameterization(parameter, "true") };
						ParameterizedCommand parametrizedCommand = new ParameterizedCommand(command, params);

						/* execute launch command with parameters - will show progress etc. as well*/
						handlerService.executeCommand(parametrizedCommand, null);

					} catch (NotDefinedException | ParameterValuesException e) {
						throw new IllegalStateException("Parameter failed!", e);
					} catch (ExecutionException e) {
						throw new IllegalStateException("Execution failed!", e);
					} catch (NotEnabledException e) {
						throw new IllegalStateException("Not enabled!", e);
					} catch (NotHandledException e) {
						throw new IllegalStateException("Not handled!", e);
					}

				}

			});
		} catch (Exception e) {
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Not handled!", e));
		}
	}

}
