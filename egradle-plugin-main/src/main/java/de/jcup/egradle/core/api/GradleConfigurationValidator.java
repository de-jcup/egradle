package de.jcup.egradle.core.api;


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
import de.jcup.egradle.core.process.EnvironmentProvider;
import de.jcup.egradle.core.process.OutputHandler;
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

	public GradleConfigurationValidator(ProcessExecutor executor){
		notNull(executor, "'executor' may not be null");
		this.executor=executor;
	}

	@Override
	public void validate(GradleConfiguration configuration) throws ValidationException {
		/* validate gradle call not empty*/
		String gradleCommand = configuration.getGradleCommand();
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
		String shell = configuration.getShellCommand();
		if (!StringUtils.isBlank(shell)){
			output("+ Test shell usable");
			String shellCloseCommand="";
			if ("bash".equals(shell) || "sh".equals(shell)){
				shellCloseCommand="--version"; // simple command - so shell is not in user mode
			}else if ("cmd.exe".equals(shell)){
				shellCloseCommand="/C"; // simple command - so shell is not in user mode
			}
			else{
				throw new ValidationException(SHELL_NOT_EXECUTABLE_STANDALONE,"Currently only supported:'bash','sh','cmd.exe");
			}
			output("  Starting shell standalone with "+shell+" "+shellCloseCommand);
			/* try to execute shell standalone */
			try {
				executor.execute(configuration, environmentProvider , shell,shellCloseCommand);
				output("  [OK]");
			} catch (IOException e) {
				output("  [FAILED]");
				throw new ValidationException(SHELL_NOT_EXECUTABLE_STANDALONE);
			}
		}
		output("+ Test gradle is working");
		/* validate gradle call  with --version ( does not validate projects but returns 0)*/
		List<String> commands = new ArrayList<>();
		String gradleCommandWithPathIfNecessary = FileUtil.createGradleCommandFullPath(configuration);
		if (!StringUtils.isBlank(shell)){
			commands.add(shell);
		}
		commands.add(gradleCommandWithPathIfNecessary);
		commands.add("--version");
		output("  Executing:"+commands);
		try {
			int result = executor.execute(configuration, environmentProvider, commands.toArray(new String[commands.size()]));
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
	

	public void setOutputHandler(OutputHandler outputHandler) {
		this.outputHandler=outputHandler;
	}
	
	private void output(String message){
		if (outputHandler==null){
			return;
		}
		outputHandler.output(message);
	}

}
