package de.jcup.egradle.eclipse.filehandling;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import de.jcup.egradle.eclipse.preferences.EGradlePreferences;

public class AutomaticalDeriveBuildFoldersHandler {

	private static final IProgressMonitor NULL_PROGRESS = new NullProgressMonitor();

	/**
	 * Will derive build folders of given project automatically, if the feature
	 * is enabled in preferences
	 * 
	 * @param project
	 * @param monitor
	 * @throws CoreException
	 *             - if derive fails
	 */
	public void deriveBuildFolders(IProject project, IProgressMonitor monitor) throws CoreException {
		if (!EGradlePreferences.EGRADLE_IDE_PREFERENCES.isAutomaticallyDeriveBuildFoldersEnabled()) {
			return;
		}
		if (!project.exists()) {
			/* project not existing, so ignore */
			return;
		}
		if (!project.isOpen()) {
			/* project not opened , so ignore */
			return;
		}
		IFolder folder = project.getFolder("build");
		if (!folder.exists()) {
			/* no build folder so ignore */
			return;
		}
		if (folder.isDerived()){
			/* already derived... so ignore */ 
			return;
		}
		if (monitor == null) {
			monitor = NULL_PROGRESS;
		}
		folder.setDerived(true, monitor);
	}
}
