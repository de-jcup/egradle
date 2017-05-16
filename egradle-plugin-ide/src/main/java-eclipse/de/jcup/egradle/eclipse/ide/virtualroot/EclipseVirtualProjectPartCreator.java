/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.egradle.eclipse.ide.virtualroot;

import static de.jcup.egradle.eclipse.ide.IDEUtil.*;
import static de.jcup.egradle.eclipse.util.EclipseUtil.*;
import static org.apache.commons.lang3.Validate.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;

import de.jcup.egradle.core.Constants;
import de.jcup.egradle.core.ExceptionUtils;
import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.virtualroot.VirtualProjectPartCreator;
import de.jcup.egradle.core.virtualroot.VirtualRootProjectException;
import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.util.EclipseUtil;
import de.jcup.egradle.eclipse.util.ProjectDescriptionCreator;

public class EclipseVirtualProjectPartCreator implements VirtualProjectPartCreator {
	private IProject newProject;

	private List<File> foldersToIgnore;
	private File rootFolder;
	private SubMonitor monitor;
	private IProgressMonitor creationMonitor;
	private int createdLinks;
	private File newProjectFile;
	/*
	 * These two files are already inside the project and cannot be shown`
	 */
	private static final List<String> FILENAMES_NOT_TO_LINK = Arrays.asList(".project", ".gitignore");
	/*
	 * gradle, git subfolders are always
	 */
	private static final List<String> FOLDERNAMES_NOT_TO_LINK = Arrays.asList(".gradle", ".git");

	/**
	 * Deletes virtual root project completely - if there exists one
	 * 
	 * @param monitor
	 * @throws CoreException
	 * @return <code>true</code> when a virtual root was existing
	 */
	public static boolean deleteVirtualRootProjectFull(IProgressMonitor monitor) throws CoreException {
		IProject virtualRootProject = getVirtualRootProject();
		if (virtualRootProject == null) {
			/*
			 * okay, not in workspace, but what's about sleeping around in file
			 * system? It must be deleted too:
			 */
			File folder = createVirtualRootFolderFile();
			if (folder != null) {
				try {
					getFileHelper().delete(folder);
				} catch (IOException e) {
					EclipseUtil.throwCoreException("Cannot delete old virtual root project on filesystem!", e);
				}
			}
			return false;
		}
		deleteVirtualRootProjectFull(monitor, virtualRootProject);
		virtualRootProject = getVirtualRootProject();
		if (virtualRootProject != null) {
			throwCoreException("virtual root project should be full deleted, but was still found in workspace!");
		}
		return true;
	}

	private static void deleteVirtualRootProjectFull(IProgressMonitor monitor, IProject projectToDelete)
			throws CoreException {
		/*
		 * to check delete with content happens only on virtual root projects we
		 * do check on another way too - this is only to prevent coding failures
		 * and should never happen!
		 */
		String name = projectToDelete.getName();
		if (!Constants.VIRTUAL_ROOTPROJECT_NAME.equals(name)) {
			throw new IllegalArgumentException(
					"Trying to delete full, with content, but this seems not to be a virtual root project?!?!?");
		}
		projectToDelete.delete(true, true, monitor);
	} // ignored

	public EclipseVirtualProjectPartCreator(GradleRootProject rootProject, IProgressMonitor monitor) {
		notNull(rootProject, "'rootProject' may not be null");
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		this.monitor = SubMonitor.convert(monitor);
		this.rootFolder = rootProject.getFolder();
	}

	@Override
	public Object createOrRecreateProject(String projectName) throws VirtualRootProjectException {
		notNull(projectName, "'projectName' may not be null");
		monitor.beginTask("virtual root project (re)creation", 3);
		File rootprojectFolder = getRootProjectFolderWithoutErrorHandling();
		if (rootprojectFolder == null) {
			throw new VirtualRootProjectException(
					"Cannot create virtual root project, because root folder is not configured");
		}
		try {
			/* setup folder creation */
			foldersToIgnore = new ArrayList<>();
			/* check if already an eclipse project */
			IProject[] projects = EclipseUtil.getAllProjects();
			for (IProject p : projects) {
				try {
					File projectPath = getResourceHelper().toFile(p.getLocation());
					if (projectPath.getParentFile().equals(rootFolder)) {
						/* already an eclipse project! */
						foldersToIgnore.add(projectPath);
					}

				} catch (CoreException e) {
					throw new VirtualRootProjectException("Was not able to check project:" + p, e);
				}
			}
			monitor.worked(1);

			monitor.subTask("delete project");
			try {
				getResourceHelper().deleteProject(projectName);
			} catch (CoreException e) {
				throw new VirtualRootProjectException("Cannot delete newProject:" + projectName, e);
			}
			monitor.worked(2);
			monitor.subTask("create project");
			try {
				File newProjectFolder = createVirtualRootFolderFile(rootprojectFolder);
				URI creationPath = newProjectFolder.toURI();
				ProjectDescriptionCreator projectDescriptionCreator = new ProjectDescriptionCreator() {

					@Override
					public IProjectDescription createNewProjectDescription(String projectName) {
						IWorkspace workspace = ResourcesPlugin.getWorkspace();
						IProjectDescription initialDescription = workspace.newProjectDescription(projectName);
						initialDescription.setLocationURI(creationPath);
						initialDescription.setComment("EGradle virtual root project - only a temporary project.\n"
								+ "There are  only two files: .gitignore and .project which will be created,\n"
								+ "all other files are just links.\n" + "\n"
								+ "Please do NOT change these two generated files!!");
						return initialDescription;
					}

				};
				newProject = IDEUtil.createOrRefreshProject(projectName, monitor, projectDescriptionCreator,
						VirtualRootProjectNature.NATURE_ID);

				newProjectFile = newProject.getLocation().toFile();

				/* create .gitignore file */
				File parentFolder = new File(newProject.getLocationURI());
				try {
					getFileHelper().createTextFile(parentFolder, ".gitignore", "*");
				} catch (IOException e) {
					throw new VirtualRootProjectException("cannot create gitignore for virtual root");
				}

				getResourceHelper().addFileFilter(newProject, ".gitignore", creationMonitor); // ok.
																								// git
																								// ignore
																								// is
																								// no
																								// more
																								// seen
																								// on
																								// navigator
				// r.addFileFilter(newProject, ".project",creationMonitor);
				// //.project seems to be not filterable...
				/*
				 * I had the idea to make .project readonly but this does not
				 * work, because .project is necessary to contain new created
				 * links etc.
				 */
			} catch (CoreException e) {
				Throwable rootCause =  ExceptionUtils.getRootCause(e);
				throw new VirtualRootProjectException("Cannot (re)create newProject:" + projectName, rootCause);
			}

			monitor.worked(3);
			monitor.done();

			return newProject;

		} catch (VirtualRootProjectException e) {
			throw e;
		} finally {
			monitor.done();
		}
	}

	/**
	 * Creates virtual root folder file
	 * 
	 * @return root folder or <code>null</code>
	 */
	public static File createVirtualRootFolderFile() {
		return createVirtualRootFolderFile(getRootProjectFolderWithoutErrorHandling());
	}

	/**
	 * Creates virtual root folder file
	 * 
	 * @param rootprojectFolder
	 * @return root folder or <code>null</code>
	 */
	public static File createVirtualRootFolderFile(File rootprojectFolder) {
		if (rootprojectFolder == null) {
			return null;
		}
		File newProjectFolder = new File(rootprojectFolder, Constants.VIRTUAL_ROOTPROJECT_FOLDERNAME);
		return newProjectFolder;
	}

	@Override
	public boolean isLinkCreationNeeded(Object targetFolder, File file) throws VirtualRootProjectException {
		if (targetFolder == null) {
			String message = "Cannot create link for file " + file + ", because target folder is null!";
			IDEUtil.logWarning(message);
			return false;
		}
		notNull(file, "'file' may not be null");
		boolean creationNeeded = internalCheckIfLinkMustBeCreated(targetFolder, file);
		if (!creationNeeded) {
			/* increase the counter for progress */
			getCreationMonitor().worked(++createdLinks);
		}
		return creationNeeded;

	}

	private boolean internalCheckIfLinkMustBeCreated(Object targetFolder, File file) {
		return isLinkCandidate(newProject, newProjectFile, foldersToIgnore, targetFolder, file);
	}

	/**
	 * Check if given file should be linked inside the projects root
	 * 
	 * @param projectItSelf
	 * @param file
	 * @return <code>true</code> when a link candidate
	 */
	public static boolean isLinkCandidate(IProject projectItSelf, File file) {
		return isLinkCandidate(projectItSelf, projectItSelf.getLocation().toFile(), new ArrayList<>(), projectItSelf,
				file);
	}

	/**
	 * Check if given file should be linked inside project
	 * 
	 * @param virtualRootProject
	 * @param newProjectFile
	 * @param foldersToIgnore
	 *            a list of folders to ignore for inspection
	 * @param targetFolder
	 *            where to create the link
	 * @param file
	 *            - origin file/folder
	 * @return <code>true</code> when a link candidate
	 */
	protected static boolean isLinkCandidate(IProject virtualRootProject, File newProjectFile,
			List<File> foldersToIgnore, Object targetFolder, File file) {
		if (newProjectFile.equals(file)) {
			/* root project cannot link to itself - infinite loop... */
			return false;
		}
		if (file.getParentFile().equals(newProjectFile)) {
			/* we also do not link content of virtual root project */
			return false;
		}
		String fileName = file.getName();
		boolean fileIsDirectory = file.isDirectory();
		boolean fileExists = file.exists();
		if (!fileExists) {
			return false;
		}
		if (fileIsDirectory) {
			if (FOLDERNAMES_NOT_TO_LINK.contains(fileName)) {
				return false;
			}
			if (targetFolder == virtualRootProject) { // inside root
				if (foldersToIgnore.contains(file)) {
					/* ignored - normally because eclipse project inside */
					return false;
				}
				/* okay, directory link has to be created */
				return true;
			}
			/*
			 * we do not dive into folders deeper than root folder - because
			 * links to folders does all the job
			 */
			return false;
		} else {
			/* not a directory but a normal file */
			if (FILENAMES_NOT_TO_LINK.contains(fileName)) {
				return false;
			}
			return true;
		}
	}

	@Override
	public void createLink(Object targetParentFolder, File file) throws VirtualRootProjectException {
		notNull(targetParentFolder, "'targetParentFolder' may not be null");
		notNull(file, "'file' may not be null");

		if (!(targetParentFolder instanceof IContainer)) {
			throw new VirtualRootProjectException("Target folder clazz not supported:" + targetParentFolder);
		}

		IContainer container = (IContainer) targetParentFolder;
		IPath path = Path.fromPortableString(file.getName());
		try {
			if (file.isDirectory()) {
				getCreationMonitor().subTask("Create link to folder '" + file.getName() + "'");
				getResourceHelper().createLinkedFolder(container, path, file);
			} else if (file.isFile()) {
				getCreationMonitor().subTask("Create link to file '" + file.getName() + "'");
				getResourceHelper().createLinkedFile(container, path, file);
			}
			getCreationMonitor().worked(++createdLinks);
		} catch (CoreException e) {
			IDEUtil.logError("Was not able to create link to file:" + file, e);
		}

	}

	@Override
	public void setMaximumLinksToCreate(int max) {
		creationMonitor = monitor.newChild(max);
	}

	public IProgressMonitor getCreationMonitor() {
		if (creationMonitor == null) {
			creationMonitor = new NullProgressMonitor();
		}
		return creationMonitor;
	}

}