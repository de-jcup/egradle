package de.jcup.egradle.eclipse.handlers;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.IParameterValues;
import org.eclipse.core.commands.ParameterValuesException;
import org.eclipse.core.commands.common.NotDefinedException;

import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleSubproject;
import de.jcup.egradle.eclipse.launch.EGradleLaunchConfigurationMainTab;

public class LaunchGradleCommandHandler extends AbstractEGradleCommandHandler {

	public static final String COMMAND_ID = "egradle.commands.launch";
	public static final String PARAMETER_LAUNCHCONFIG = "egradle.command.launch.config";

	private GradleCommand[] commands;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IParameter configparameter = event.getCommand().getParameter(PARAMETER_LAUNCHCONFIG);
			IParameterValues configParamValues = configparameter.getValues();
			Map<?,?> values = configParamValues.getParameterValues();
			String projectName = (String) values.get(EGradleLaunchConfigurationMainTab.PROPERTY_PROJECTNAME);
			String commandString = (String) values.get(EGradleLaunchConfigurationMainTab.PROPERTY_ARGUMENTS);
			
			String[] commandStrings = commandString.split(" ");
			if (StringUtils.isEmpty(projectName)){
				this.commands=GradleCommand.build(commandStrings);
			}else{
				this.commands=GradleCommand.build(new GradleSubproject(projectName),commandStrings);
			}
			
		} catch (NotDefinedException | ParameterValuesException e) {
			throw new IllegalStateException("Cannot fetch command parameter!", e);
		}
		return super.execute(event);
	}


	@Override
	protected GradleCommand[] createCommands() {
		return commands;
	}

}
