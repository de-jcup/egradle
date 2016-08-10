package de.jcup.egradle.eclipse.execution;

import static org.apache.commons.lang3.Validate.notNull;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;

import de.jcup.egradle.core.GradleExecutor;
import de.jcup.egradle.core.GradleExecutor.Result;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.process.ProcessExecutor;
import de.jcup.egradle.core.process.ProcessOutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;

public class GradleExecution{
	
	private GradleContext context;
	private ProcessOutputHandler processOutputHandler;
	private Result result;
	
	public Result getResult() {
		return result;
	}
	
	public GradleExecution(ProcessOutputHandler processOutputHandler, GradleContext context){
			notNull(context, "'context' may not be null");
			notNull(processOutputHandler, "'processOutputHandler' may not be null");
			
			this.context = context;
			this.processOutputHandler=processOutputHandler;
	}
	
	public void execute(IProgressMonitor monitor) throws Exception{
		ProcessExecutor processExecutor = new SimpleProcessExecutor(processOutputHandler);
		GradleExecutor executor = new GradleExecutor(processExecutor);

		// do non-UI work
		String commandString = context.getCommandString();
		monitor.beginTask("Executing gradle commands:" + commandString, context.getAmountOfWorkToDo());
		processOutputHandler.output(
				"Root project '" + context.getRootProject().getFolder().getName() + "' executing " + commandString);

		result = executor.execute(context);
		if (!result.isOkay()) {
			return;
		}
		try {
			afterExecutionDone(monitor);
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}

		monitor.done();
	}

	protected void afterExecutionDone(IProgressMonitor monitor) throws Exception{
		/* per default do nothing*/
	}
}