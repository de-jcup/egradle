package de.jcup.egradle.eclipse.handlers;

import static org.apache.commons.lang3.Validate.notEmpty;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import de.jcup.egradle.core.GradleExecutor;
import de.jcup.egradle.core.GradleExecutor.Result;
import de.jcup.egradle.core.config.AlwaysBashWithGradleWrapperConfiguration;
import de.jcup.egradle.core.config.GradleConfiguration;
import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.process.ProcessExecutor;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.eclipse.Activator;
import de.jcup.egradle.eclipse.console.EGradleConsoleProcessOutputHandler;
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

	protected void prepareContext(GradleContext context) {
		/* do nothing more, can be overriden */
	}

	protected void afterExecutionOutsideUI(IProgressMonitor monitor) throws Exception {
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			try {
				project.refreshLocal(IResource.DEPTH_INFINITE,
						/* new NullProgressMonitor() */monitor);
			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			}
		}
	}

	protected int getTasksToDo() {
		return 1;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ProcessExecutor processExecutor = new SimpleProcessExecutor(processOutputHandler);
		GradleExecutor executor = new GradleExecutor(processExecutor);
		String path = null;
		/* TODO  ATR, use preferences correctly!InstanceScope.INSTANCE is the new way. maybe preference page must be refactored to use the instance scope too  */
		
//		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
//		path = prefs.get(PreferenceConstants.P_ROOTPROJECT_PATH, "V:/5100_Workspace");
		IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		path = prefs.getString(PreferenceConstants.P_ROOTPROJECT_PATH);
		String javaHome = prefs.getString(PreferenceConstants.P_JAVA_HOME_PATH);
		
		GradleRootProject rootProject = new GradleRootProject(new File(path));

		GradleConfiguration config = new AlwaysBashWithGradleWrapperConfiguration();

		GradleContext context = new GradleContext(rootProject, config);
		context.setEnvironment("JAVA_HOME", javaHome);
		prepareContext(context);
		GradleCommand[] commands = createCommands();
		notEmpty(commands, "'commands' may not be empty");

		IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		try {
//			progressService.busyCursorWhile(new IRunnableWithProgress() {
			progressService.run(false, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					// do non-UI work
					List<GradleCommand> commandList = Arrays.asList(commands);
					monitor.beginTask("Executing gradle commands:" + commandList, getTasksToDo());
					processOutputHandler.output("In root project:"+rootProject.getFolder().getName()+":"+commandList);
					
					Result result = executor.execute(context, commands);
					if (!result.isOkay()) {
						return;
					}
					try {
						afterExecutionOutsideUI(monitor);
					} catch (Exception e) {
						throw new InvocationTargetException(e);
					}

					monitor.done();
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			throw new ExecutionException("Cannot refresh all projects ...", e);
		}
		return null;
	}

}
