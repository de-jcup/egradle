package de.jcup.egradle.eclipse.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import de.jcup.egradle.core.BuildFilesFilter;
import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.api.ResourceHelper;

import static de.jcup.egradle.core.Constants.*;

public class UpdateOrCreateVirtualRootProjectHandler extends AbstractHandler {

	
	private BuildFilesFilter filter = new BuildFilesFilter(false);

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {

		ResourceHelper r = ResourceHelper.SHARED;
		GradleRootProject rootProject = EGradleUtil.getRootProject();
		if (rootProject == null) {
			return null;
		}
		File rootFolder = rootProject.getFolder();
		if (rootFolder == null) {
			return null;
		}

		try {
			r.deleteProject(VIRTUAL_ROOTPROJECT_NAME);
			IProject project = r.createOrRefreshProject(VIRTUAL_ROOTPROJECT_NAME);
			visit(r, project, rootFolder);

		} catch (CoreException e) {
			throw new ExecutionException("Cannot create virtual root project", e);
		}
		return null;
	}

	private void visit(ResourceHelper r, IContainer container, File folderToScan) throws CoreException {
		File[] buildFiles = folderToScan.listFiles(filter);
		if (buildFiles == null) {
			return;
		}
		for (File file : buildFiles) {
			if (file.isDirectory()) {
				/*
				 * ignore currently, because difficult to separate gradle
				 * projects from subfolders with addtional scripts, but maybe we 
				 * should show suprojects as well? Eg. like
				 */
				/** <pre>
				 * rootProject
				 *    |
				 *    x-build.gradle
				 *    x-scripts
				 *    |   |
				 *    |	  x-childscript.gradle
				 *    x-subproject
				 *    |   |
				 *    |   x-build.gradle
				 *    ...
				 *       
				 * </pre>
				 */
				// visit(..,..)
			} else {
				IPath path = Path.fromPortableString(file.getName());
				IFile f = r.createLinkedFile(container, path, file);
				System.out.println("created:"+f);
			}
		}
	}

}
