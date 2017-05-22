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
package de.jcup.egradle.core;

import static org.apache.commons.lang3.Validate.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.config.GradleConfiguration;
import de.jcup.egradle.core.domain.CancelStateProvider;
import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.process.EGradleShellType;
import de.jcup.egradle.core.process.ProcessExecutor;

/**
 * Simple executor mechansim for gradle command
 * 
 * @author Albert Tregnaghi
 *
 */
public class GradleExecutor {

	private ProcessExecutor processExecutor;

	public GradleExecutor(ProcessExecutor processExecutor) {
		notNull(processExecutor, "Process executor may not be null!");
		this.processExecutor = processExecutor;
	}

	/**
	 * Executes gradle
	 * 
	 * @param context
	 * @return result
	 */
	public ProcessExecutionResult execute(GradleContext context) {
		ProcessExecutionResult processExecutionResult = new ProcessExecutionResult();

		String[] commandStrings = createExecutionCommand(context);

		/* execute process */
		int processResult;
		try {
			processResult = processExecutor.execute(context.getConfiguration(), context, context, commandStrings);
			processExecutionResult.setCommands(context.getCommandString());
			processExecutionResult.setProcessResult(processResult);
		} catch (IOException e) {
			processExecutionResult.setException(e);
		}
		CancelStateProvider cancelStateProvider = context.getCancelStateProvider();
		if (cancelStateProvider.isCanceled()){
			processExecutionResult.setCanceledByuser(true);
		}
		return processExecutionResult;
	}

	String[] createExecutionCommand(GradleContext context) {
		/* build command string */
		int pos = 0;
		GradleCommand[] commands = context.getCommands();
		int arraySize = commands.length + 1;
		/* expand arraysize for command arguments too*/
		for (GradleCommand c : commands) {
			arraySize += c.getCommandArguments().size();
		}
		GradleConfiguration config = context.getConfiguration();
		EGradleShellType shell = config.getShellType();
		if (shell == null) {
			shell = EGradleShellType.NONE;
		}
		List<String> shellCommands = shell.createCommands();
		arraySize += shellCommands.size();// we must call shell executor too
		String[] options = context.getOptions();
		if (options == null) {
			options = new String[] {};
		}
		/* be aware of empty content */
		List<String> safeOptions = new ArrayList<>();
		for (String opt : options) {
			if (StringUtils.isNotBlank(opt)) {
				safeOptions.add(opt);
			}
		}
		arraySize += safeOptions.size();

		Map<String, String> gradleProperties = context.getGradleProperties();
		Map<String, String> systemProperties = context.getSystemProperties();

		arraySize += gradleProperties.size();
		arraySize += systemProperties.size();

		String[] commandStrings = new String[arraySize];
		for (String shellCommand : shellCommands) {
			commandStrings[pos++] = shellCommand;
		}
		commandStrings[pos++] = config.getGradleCommandFullPath();
		/* raw options */
		for (String rawOption : safeOptions) {
			commandStrings[pos++] = rawOption;
		}
		/* gradle properties */
		for (String key : gradleProperties.keySet()) {
			commandStrings[pos++] = "-P" + key + "=" + gradleProperties.get(key);
		}
		/* system properties */
		for (String key : systemProperties.keySet()) {
			commandStrings[pos++] = "-D" + key + "=" + systemProperties.get(key);
		}
		/* commands */
		for (int i = 0; i < commands.length; i++) {
			GradleCommand gradleCommand = commands[i];
			commandStrings[pos++] = gradleCommand.getCommand();
			List<String> commandArguments = gradleCommand.getCommandArguments();
			if (commandArguments.size()>1){
				commandStrings[pos++] = commandArguments.get(0);
				commandStrings[pos++] = commandArguments.get(1);
			}
			
		}
		return commandStrings;
	}
}
