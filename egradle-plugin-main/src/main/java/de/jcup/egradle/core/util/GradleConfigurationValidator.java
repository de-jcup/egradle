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
 package de.jcup.egradle.core.util;


import static org.apache.commons.lang3.Validate.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.config.GradleConfiguration;
import de.jcup.egradle.core.domain.CancelStateProvider;
import de.jcup.egradle.core.process.EGradleShellType;
import de.jcup.egradle.core.process.EnvironmentProvider;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.ProcessContext;
import de.jcup.egradle.core.process.ProcessExecutor;

public class GradleConfigurationValidator implements Validator<GradleConfiguration>{
	
	public static final String GRADLE_INSTALLATION_DIR_NOT_EXISTING = "gradle installation directory not existing";
	public static final String GRADLE_INSTALLATION_DIR_IS_NOT_A_DIRECTORY = "gradle installation directory is not a directory";
	public static final String GRADLE_INSTALLATION_DIR_CONTAINS_NO_GRADLE="gradle installation directory does NOT contain the defined gradle command";

	public static final String SHELL_NOT_EXECUTABLE_STANDALONE = "configured shell cannot be executed";
	public static final String GRADLE_COMMAND_MISSING = "gradle command missing";
	public static final String GRADLE_VERSON_NOT_CALLABLE = "gradle cannot be called to fetch at least version";
	public static final String GRADLE_VERSON_CALLABLE_BUT_DID_RETURN_FAILURE = "called --version. But did not return "+ProcessExecutor.PROCESS_RESULT_OK;
	
	private ProcessExecutor executor;
	private OutputHandler outputHandler;
	private CancelStateProvider cancelStateProvider;

	public GradleConfigurationValidator(ProcessExecutor executor){
		notNull(executor, "'executor' may not be null");
		this.executor=executor;
	}

	@Override
	public void validate(GradleConfiguration configuration) throws ValidationException {
		handleCanceled();
		/* validate gradle call not empty*/
		String gradleCommand = configuration.getGradleCommandFullPath();
		if (StringUtils.isBlank(gradleCommand)){
			throw new ValidationException(GRADLE_COMMAND_MISSING);
		}
		/* validate gradle installation dir*/
		String gradleInstallDir = configuration.getGradleBinDirectory();
		if (! StringUtils.isEmpty(gradleInstallDir)){
			output("+ Test gradle bin folder");
			/* validate directory exists*/
			File file = new File(gradleInstallDir);
			if (! file.exists()){
				throw new ValidationException(GRADLE_INSTALLATION_DIR_NOT_EXISTING);
			}
			if (!file.isDirectory()){
				throw new ValidationException(GRADLE_INSTALLATION_DIR_IS_NOT_A_DIRECTORY);
			}
			List<String> subFiles = Arrays.asList(file.list());
			if (!subFiles.contains(gradleCommand)){
				throw new ValidationException(GRADLE_INSTALLATION_DIR_CONTAINS_NO_GRADLE);	
			}
		}
		EnvironmentProvider environmentProvider = new EnvironmentProvider() {
			
			@Override
			public Map<String, String> getEnvironment() {
				Map<String,String> map = new HashMap<>();
				String javaHome = configuration.getJavaHome();
				if (StringUtils.isNotBlank(javaHome)){
					map.put("JAVA_HOME",javaHome);
				}
				return map;
			}

		};
		
		/* validate shell call*/
		EGradleShellType shell = configuration.getShellType();
		if (shell==null){
			shell = EGradleShellType.NONE;
		}
		List<String> shellStandaloneCommands = shell.createCheckStandaloneCommands();
		handleCanceled();
		ProcessContext context = new ProcessContext() {
			
			@Override
			public CancelStateProvider getCancelStateProvider() {
				return GradleConfigurationValidator.this.getCancelStateProvider();
			}
		};
		if (! shellStandaloneCommands.isEmpty()){
			output("  Starting shell standalone with "+ shellStandaloneCommands);
			/* try to execute shell standalone */
			try {
				executor.execute(configuration, environmentProvider , context, shellStandaloneCommands.toArray(new String[shellStandaloneCommands.size()]));
				output("  [OK]");
			} catch (IOException e) {
				output("  [FAILED]");
				throw new ValidationException(SHELL_NOT_EXECUTABLE_STANDALONE);
			}
		}
		output("+ Test gradle is working");
		/* validate gradle call  with --version ( does not validate projects but returns 0)*/
		List<String> commands = new ArrayList<>();
		commands.addAll(shell.createCommands());
		String gradleCommandWithPathIfNecessary = configuration.getGradleCommandFullPath();
		commands.add(gradleCommandWithPathIfNecessary);
		commands.add("--version");
		output("  Executing:"+commands);
		handleCanceled();
		try {
			int result = executor.execute(configuration, environmentProvider, context, commands.toArray(new String[commands.size()]));
			if (result!=ProcessExecutor.PROCESS_RESULT_OK){
				output("  [FAILED]");
				throw new ValidationException(GRADLE_VERSON_CALLABLE_BUT_DID_RETURN_FAILURE, "Result was:"+result);
			}
			output("  [OK]");
		} catch (IOException e) {
			output("  [FAILED]");
			throw new ValidationException(GRADLE_VERSON_NOT_CALLABLE);
		}
	}
	

	private void handleCanceled() throws ValidationException {
		if (getCancelStateProvider().isCanceled()){
			throw new ValidationException("Canceled by user");
		}
	}

	public void setOutputHandler(OutputHandler outputHandler) {
		this.outputHandler=outputHandler;
	}
	
	private void output(String message){
		if (outputHandler==null){
			return;
		}
		outputHandler.output(message);
	}

	public void setCancelStateProvider(CancelStateProvider cancelStateProvider) {
		this.cancelStateProvider=cancelStateProvider;
	}
	
	public CancelStateProvider getCancelStateProvider() {
		if (cancelStateProvider==null){
			return CancelStateProvider.NEVER_CANCELED;
		}
		return cancelStateProvider;
	}

}
