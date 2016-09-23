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
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.config.GradleConfiguration;
import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.domain.GradleRootProject;
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
	public Result execute(GradleContext context) {
		Result result = new Result();

		GradleRootProject rootProject = context.getRootProject();

		String[] commandStrings = createExecutionCommand(context);

		/* execute process */
		int processResult;
		try {
			processResult = processExecutor.execute(rootProject.getFolder(), context, commandStrings);

			result.setProcessResult(processResult);
		} catch (IOException e) {
			result.setException(e);
		}

		return result;
	}

	String[] createExecutionCommand(GradleContext context) {
		/* build command string */
		int pos = 0;
		GradleCommand[] commands = context.getCommands();
		int arraySize = commands.length + 1;
		GradleConfiguration config = context.getConfiguration();
		String shell = config.getShellCommand();
		if (!StringUtils.isEmpty(shell)) {
			arraySize += 1;// we must call shell executor too
		}
		String[] options = context.getOptions();
		arraySize += options.length;
		
		Map<String, String> gradleProperties = context.getGradleProperties();
		Map<String, String> systemProperties = context.getSystemProperties();

		arraySize += gradleProperties.size();
		arraySize += systemProperties.size();
		
		String[] commandStrings = new String[arraySize];
		if (!StringUtils.isEmpty(shell)) {
			commandStrings[pos++] = shell;
		}
		commandStrings[pos++] = config.getGradleInstallDirectory()+config.getGradleCommand();
		/* raw options */
		for (int i = 0; i < options.length; i++) {
			commandStrings[pos++] = options[i];
		}
		/* gradle properties*/
		for (String key: gradleProperties.keySet()) {
			commandStrings[pos++] = "-P"+key+"="+gradleProperties.get(key);
		}
		/* system properties*/
		for (String key: systemProperties.keySet()) {
			commandStrings[pos++] = "-D"+key+"="+systemProperties.get(key);
		}
		/* commands */
		for (int i = 0; i < commands.length; i++) {
			commandStrings[pos++] = commands[i].getCommand();
		}
		return commandStrings;
	}

	public class Result {

		private Integer processResult;
		private Exception exception;

		public boolean isOkay() {
			return ProcessExecutor.PROCESS_RESULT_OK.equals(processResult);
		}

		public void setException(Exception e) {
			this.exception = e;

		}

		public void setProcessResult(int processResult) {
			this.processResult = processResult;
		}

		@Override
		public String toString() {
			return "Result [processResult=" + processResult + ", exception=" + exception + "]";
		}

		public int getResultCode() {
			if (processResult == null) {
				return -1;
			}
			return processResult;
		}

	}
}
