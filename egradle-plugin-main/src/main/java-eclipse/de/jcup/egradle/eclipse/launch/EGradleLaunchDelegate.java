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

import static de.jcup.egradle.eclipse.launch.EGradleLauncherConstants.*;

import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.IParameterValues;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.services.IServiceLocator;

import de.jcup.egradle.core.api.ForgetMe;
import de.jcup.egradle.eclipse.Activator;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.handlers.LaunchGradleCommandHandler;

public class EGradleLaunchDelegate implements ILaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		String projectName = configuration.getAttribute(PROPERTY_PROJECTNAME, "");
		String options = configuration.getAttribute(PROPERTY_OPTIONS, "");

		executeByHandler(launch, projectName, options);

	}

	private void executeByHandler(ILaunch launch, String projectName, String options)
			throws CoreException {

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
						map.clear();// Bugfix #79: We always clear the map. It
									// seems it is reused between every command
									// call - the junit import job was added by
									// other plugin, but the map was reused, so
									// the job was always there!!!!!
						map.put(LAUNCH_ARGUMENT, launch);
						appendAdditionalLaunchParameters(map);
						Parameterization[] params = new Parameterization[] { new Parameterization(parameter, "true") };
						ParameterizedCommand parametrizedCommand = new ParameterizedCommand(command, params);

						/*
						 * execute launch command with parameters - will show
						 * progress etc. as well
						 */
						handlerService.executeCommand(parametrizedCommand, null);

					} catch (Exception e) {
						if (ExceptionUtils.getRootCause(e) instanceof ForgetMe) {
							/* do nothing, already handled */
						} else {
							throw new IllegalStateException("EGradle launch command execution failed", e);
						}
					} finally {
						/*
						 * TODO ATR, 23.09.2016: when exceptions are occurring
						 * while launching the old launches are still existing
						 * in debug view
						 */
						/*
						 * the following workaround does really work, but it is
						 * the correct place
						 */
						if (!launch.isTerminated()) {
							try {
								launch.terminate();
							} catch (DebugException e) {
								EGradleUtil.log(e);
							}
						}
					}

				}

			});
		} catch (Exception e) {
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Not handled!", e));
		}
	}

	/**
	 * Append additional launch parameters for gradle command handler. This is
	 * done inside UI Thread
	 * 
	 * @param map
	 */
	protected void appendAdditionalLaunchParameters(Map<Object, Object> map) {

	}

}
