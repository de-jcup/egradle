package de.jcup.egradle.eclipse.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.process.ProcessOutputHandler;
import de.jcup.egradle.eclipse.execution.GradleExecution;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class RefreshAllEclipseDependenciesHandler extends AbstractEGradleCommandHandler {

	@Override
	protected void additionalPrepareContext(GradleContext context) {
		context.setAmountOfWorkToDo(2);
	}

	@Override
	protected GradleExecution createGradleExecution(ProcessOutputHandler processOutputHandler, GradleContext context) {
		return new RefreshAllEclipseProjectsGradleExecution(processOutputHandler, context);
	}

	@Override
	protected GradleCommand[] createCommands() {
		return GradleCommand.build("cleanEclipse", "eclipse");
	}

	private final class RefreshAllEclipseProjectsGradleExecution extends GradleExecution {
		private RefreshAllEclipseProjectsGradleExecution(ProcessOutputHandler processOutputHandler,
				GradleContext context) {
			super(processOutputHandler, context);
		}

		protected void afterExecutionDone(IProgressMonitor monitor) throws Exception {
			monitor.worked(1);
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (IProject project : projects) {
				try {
					if (monitor.isCanceled()) {
						break;
					}
					monitor.subTask("refreshing project " + project.getName());
					project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				}
			}
			monitor.worked(2);
		}
	}

}
