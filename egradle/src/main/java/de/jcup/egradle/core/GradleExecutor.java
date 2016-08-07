package de.jcup.egradle.core;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.util.Map;

import de.jcup.egradle.core.config.GradleConfiguration;
import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.process.ProcessExecutor;

/**
 * Simple executor mechansim to execute a gradlew command
 * @author atrigna
 *
 */
public class GradleExecutor {

	private ProcessExecutor processExecutor;
	
	public GradleExecutor(ProcessExecutor processExecutor){
		notNull(processExecutor,"Process executor may not be null!");
		this.processExecutor=processExecutor;
	}

	
	/**
	 * Executes gradle
	 * @param context
	 * @param commands - if no command is set an {@link IllegalArgumentException} is thrown
	 * @return result
	 */
	public Result execute(GradleContext context, GradleCommand ...commands){
		notEmpty(commands, "'commands' may not be empty");
		Result result = new Result();

		GradleConfiguration config = context.getConfiguration();
		GradleRootProject rootProject = context.getRootProject();
		Map<String, String> env = context.getEnvironment();
		
		/* build command string*/
		int pos=0;
		int arraySize = commands.length+1;
		if (config.isUsingGradleWrapper()){
			arraySize+=1;// we must call wrapper executor too
		}
		String[] commandStrings = new String[arraySize];
		if (config.isUsingGradleWrapper()){
			commandStrings[pos++]=config.getShellForGradleWrapper();
			commandStrings[pos++]="gradlew";
		}else{
			throw new UnsupportedOperationException("Currently only gradle wrapper usage is supported!");
		}
		
		for (int i=0;i<commands.length;i++){
			commandStrings[pos++]=commands[i].getCommand();
		}

		/* execute process*/
		int processResult;
		try {
			processResult = processExecutor.execute(rootProject.getFolder(), env,commandStrings);
			
			result.setProcessResult(processResult);
		} catch (IOException e) {
			result.setException(e);
		} 
		
		return result;
	}
	
	public class Result{

		private Integer processResult;
		private Exception exception;

		public boolean isOkay() {
			return ProcessExecutor.PROCESS_RESULT_OK.equals(processResult);
		}
		

		public void setException(Exception e) {
			this.exception=e;
			
		}

		public void setProcessResult(int processResult) {
			this.processResult=processResult;
		}


		@Override
		public String toString() {
			return "Result [processResult=" + processResult + ", exception=" + exception + "]";
		}
		
	}
}
