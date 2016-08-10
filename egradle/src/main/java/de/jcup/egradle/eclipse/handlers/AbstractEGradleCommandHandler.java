package de.jcup.egradle.eclipse.handlers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.IProgressService;

import de.jcup.egradle.core.config.AlwaysBashWithGradleWrapperConfiguration;
import de.jcup.egradle.core.config.GradleConfiguration;
import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.process.ProcessOutputHandler;
import de.jcup.egradle.eclipse.Activator;
import de.jcup.egradle.eclipse.console.EGradleConsoleProcessOutputHandler;
import de.jcup.egradle.eclipse.execution.GradleJob;
import de.jcup.egradle.eclipse.execution.GradleRunnableWithProgress;
import de.jcup.egradle.eclipse.execution.GradleExecution;
import de.jcup.egradle.eclipse.preferences.PreferenceConstants;

/**
 * Abstract base handler for egradle command executions
 * 
 * @author Albert Tregnaghi
 *
 */
public abstract class AbstractEGradleCommandHandler extends AbstractHandler {

	protected EGradleConsoleProcessOutputHandler processOutputHandler;

	public AbstractEGradleCommandHandler() {
		this.processOutputHandler = new EGradleConsoleProcessOutputHandler();
	}

	protected abstract GradleCommand[] createCommands();

	private void prepareContext(GradleContext context) {
		String javaHome = getStringPreference(PreferenceConstants.P_JAVA_HOME_PATH);
		context.setEnvironment("JAVA_HOME", javaHome);
		context.setCommands(createCommands());
		context.setAmountOfWorkToDo(1);
		
		additionalPrepareContext(context);
		
	}
	protected void additionalPrepareContext(GradleContext context) {
		/* can be overriden*/
	}

	protected enum ExecutionMode {
		BLOCK_UI__CANCEABLE, RUN_IN_BACKGROUND__CANCEABLE
	}

	protected ExecutionMode getExecutionMode() {
		return ExecutionMode.RUN_IN_BACKGROUND__CANCEABLE;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		String path = getStringPreference(PreferenceConstants.P_ROOTPROJECT_PATH);
		GradleRootProject rootProject = new GradleRootProject(new File(path));

		GradleConfiguration config = new AlwaysBashWithGradleWrapperConfiguration();

		GradleContext context = new GradleContext(rootProject, config);
		prepareContext(context);
		
		GradleExecution execution = createGradleExecution(processOutputHandler, context);
		
		ExecutionMode mode = getExecutionMode();
		
			switch (mode) {
			case BLOCK_UI__CANCEABLE:
				try {
					GradleRunnableWithProgress runnable = new GradleRunnableWithProgress(execution);
				IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
				progressService.busyCursorWhile(runnable);
				
				} catch (InvocationTargetException | InterruptedException e) {
					throw new ExecutionException("Cannot refresh all projects ...", e);
				}
				break;
			case RUN_IN_BACKGROUND__CANCEABLE:
				GradleJob job = new GradleJob("gradle execution",execution);
				job.schedule();
				break;

			default:
				throw new IllegalArgumentException("Not implemented for mode:" + mode);
			}

		return null;
	}

	protected GradleExecution createGradleExecution(ProcessOutputHandler processOutputHandler, GradleContext context) {
		return new GradleExecution(processOutputHandler, context);
	}
	
	private String getStringPreference(String id){
		/*
		 * TODO ATR, use preferences correctly!InstanceScope.INSTANCE is the new
		 * way. maybe preference page must be refactored to use the instance
		 * scope too
		 */
		IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();	
		String result = prefs.getString(id);
		return result;
	}
	
	
	
	

	

}
