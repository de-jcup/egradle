package de.jcup.egradle.core.virtualroot;

import static de.jcup.egradle.core.Constants.*;

import java.io.File;
import java.io.FileFilter;

import de.jcup.egradle.core.AcceptAllFilesFilter;
import de.jcup.egradle.core.domain.GradleRootProject;

public class VirtualProjectSupport {

	private static final FileFilter FILES_AND_FOLDERS = new AcceptAllFilesFilter(true);
	
	public void createOrUpdateVirtualRootProject(GradleRootProject rootProject, RootProjectVisitor visitor)
			throws VirtualRootProjectException {
		if (rootProject == null) {
			return;
		}
		File rootFolder = rootProject.getFolder();
		if (rootFolder == null) {
			return;
		}
		if (!rootFolder.exists()){
			return;
		}
		Object project = visitor.createOrRecreateProject(VIRTUAL_ROOTPROJECT_NAME);
		addLinksAndMissingFolders(project, visitor, rootFolder,FILES_AND_FOLDERS, rootProject);

	}

	private void addLinksAndMissingFolders(Object targetParentFolder, RootProjectVisitor v, File folderToScan,FileFilter filter, GradleRootProject rootProject) throws VirtualRootProjectException {
		File[] buildFiles = folderToScan.listFiles();
		if (buildFiles == null) {
			return;
		}
		for (File file : buildFiles) {
			if (file.isDirectory()) {
				/**
				 * <pre>
				 * rootProject
				 *    |
				 *    x-build.gradle
				 *    x-scripts --- only a folder link has to be  created...
				 *    |   |
				 *    |	  x-childscript.gradle
				 *    x-subproject 
				 *    |   |
				 *    |   x-build.gradle
				 *    ...
				 * 
				 * </pre>
				 */
				if (v.needsFolderToBeCreated(targetParentFolder, file)){
					v.createLink(targetParentFolder, file);
				}
			} else {
				v.createLink(targetParentFolder, file);
			}
		}
	}

}
