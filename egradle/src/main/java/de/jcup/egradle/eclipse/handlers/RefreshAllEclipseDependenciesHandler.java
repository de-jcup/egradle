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

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class RefreshAllEclipseDependenciesHandler extends AbstractEGradleCommandHandler {

	protected void prepareContext(GradleContext context) {
		/* do nothing more, can be overriden */
	}
	
	@Override
	protected int getTasksToDo() {
		return 2;
	}

	protected void afterExecutionOutsideUI(IProgressMonitor monitor) throws Exception {
		monitor.worked(1);
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects) {
			try {
				if (monitor.isCanceled()){
					break;
				}
				monitor.subTask("refreshing project "+project.getName());
				project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			}
		}
		monitor.worked(2);
	}

	@Override
	protected GradleCommand[] createCommands() {
		return GradleCommand.build("cleanEclipse","eclipse");
	}

}
