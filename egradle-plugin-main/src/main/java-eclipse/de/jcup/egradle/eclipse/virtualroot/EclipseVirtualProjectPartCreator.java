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
package de.jcup.egradle.eclipse.virtualroot;

import static org.apache.commons.lang3.Validate.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.virtualroot.VirtualProjectPartCreator;
import de.jcup.egradle.core.virtualroot.VirtualRootProjectException;
import de.jcup.egradle.eclipse.Activator;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.api.ResourceHelper;

public class EclipseVirtualProjectPartCreator implements VirtualProjectPartCreator {
	private ResourceHelper r = ResourceHelper.SHARED;
	private IProject newProject;

	private List<File> foldersToIgnore;
	private File rootFolder;
	private SubMonitor monitor;
	private IProgressMonitor creationMonitor;
	private int createdLinks;
	private File newProjectFile;
	private static final List<String> FILENAMES_NOT_TO_LINK = Arrays.asList(".project",".gitignore"); // These two files are already inside the project and cannot be shown
	private static final List<String> FOLDERNAMES_NOT_TO_LINK = Arrays.asList(".gradle",".git"); // gradle, git subfolders are always ignored

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
		File rootprojectFolder = EGradleUtil.getRootProjectFolderWithoutErrorHandling();
		if (rootprojectFolder == null) {
			throw new VirtualRootProjectException(
					"Cannot create virtual root project, because root folder is not configured");
		}
		try {
			/* setup folder creation */
			foldersToIgnore = new ArrayList<>();
			/* check if already an eclipse project */
			IProject[] projects = EGradleUtil.getAllProjects();
			for (IProject p : projects) {
				try {
					File projectPath = r.getFileHelper().toFile(p.getLocation());
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
				
				r.deleteProject(projectName);
			} catch (CoreException e) {
				throw new VirtualRootProjectException("Cannot delete newProject:" + projectName, e);
			}
			monitor.worked(2);
			monitor.subTask("create project");
			try {
				File newProjectFolder = new File(rootprojectFolder,".egradle");
				newProject = r.createOrRefreshProject(projectName, monitor,  newProjectFolder.toURI(), VirtualRootProjectNature.NATURE_ID);
				newProjectFile = newProject.getLocation().toFile();

				/* create .gitignore file*/
				File parentFolder = new File(newProject.getLocationURI());
				r.getFileHelper().createTextFile(parentFolder, ".gitignore", "*"); // ignore .egradle completely

			} catch (CoreException e) {
				new VirtualRootProjectException("Cannot (re)create newProject:" + projectName, e);
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


	@Override
	public boolean isLinkCreationNeeded(Object targetFolder, File file) throws VirtualRootProjectException {
		if (targetFolder==null){
			String message = "Cannot create link for file"+file+", because target folder is null!";
			EGradleUtil.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, message));
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
	 * @param projectItSelf
	 * @param file
	 * @return <code>true</code> when a link candidate
	 */
	public static boolean isLinkCandidate(IProject projectItSelf, File file) {
		return isLinkCandidate(projectItSelf, projectItSelf.getLocation().toFile(), new ArrayList<>(), projectItSelf, file);
	}
	/**
	 * Check if given file should be linked inside project
	 * @param projectItSelf
	 * @param newProjectFile 
	 * @param foldersToIgnore - a list of folders to ignore for inspection
	 * @param targetFolder - where to create the link
	 * @param file
	 * @return <code>true</code> when a link candidate
	 */
	protected static boolean isLinkCandidate(IProject projectItSelf, File newProjectFile, List<File> foldersToIgnore, Object targetFolder, File file) {
		if (newProjectFile.equals(file)){
			/* root project cannot link to itself - infinite loop...*/
			return false;
		}
		if (file.getParentFile().equals(newProjectFile)){
			/* we also do not link content of virtual root project*/
			return false;
		}
		String fileName = file.getName();
		boolean directory = file.isDirectory();
		boolean fileExists = file.exists();
		if (!fileExists){
			return false;
		}
		if (directory) {
			if (FOLDERNAMES_NOT_TO_LINK.contains(fileName)) {
				return false;
			}
			if (targetFolder == projectItSelf) {
				if (foldersToIgnore.contains(file)) {
					return false;
				}
				return true;
			}
			/* we do not dive into */
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
				r.createLinkedFolder(container, path, file);
			} else if (file.isFile()){
				getCreationMonitor().subTask("Create link to file '" + file.getName() + "'");
				r.createLinkedFile(container, path, file);
			}
			getCreationMonitor().worked(++createdLinks);
		} catch (CoreException e) {
			EGradleUtil.log(new Status(IStatus.ERROR,Activator.PLUGIN_ID,"Was not able to create link to file:" + file, e));
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