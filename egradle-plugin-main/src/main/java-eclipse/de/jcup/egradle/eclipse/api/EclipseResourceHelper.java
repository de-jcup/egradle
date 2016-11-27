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
package de.jcup.egradle.eclipse.api;

import static org.apache.commons.lang3.Validate.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.manipulation.ContainerCreator;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;

import de.jcup.egradle.core.GradleImportScanner;
import de.jcup.egradle.core.api.FileHelper;

public class EclipseResourceHelper {
	public static EclipseResourceHelper DEFAULT = new EclipseResourceHelper();
	private static String FILE_FILTER_ID = "org.eclipse.ui.ide.patternFilterMatcher"; 

	private final int MAX_RETRY = 5;
	private final IProgressMonitor NULL_MONITOR = new NullProgressMonitor();
	
	private FileHelper fileHelper = new FileHelper();

	public void addFileFilter(IProject newProject, String pattern, IProgressMonitor monitor) throws CoreException {
		FileInfoMatcherDescription matcherDescription = new FileInfoMatcherDescription(FILE_FILTER_ID, pattern);
		/* ignore the generated files - .project and .gitignore at navigator etc. */
		newProject.createFilter(IResourceFilterDescription.EXCLUDE_ALL | IResourceFilterDescription.FILES, matcherDescription, IResource.BACKGROUND_REFRESH, monitor);
	}

	public IFile createFile(IFolder folder, String name, String contents) throws CoreException {
		return createFile(folder.getFile(name), name, contents);
	}

	
	public IFile createFile(IProject project, String name, String contents) throws CoreException {
		return createFile(project.getFile(name), name, contents);
	}
	
	public IFolder createFolder(IPath path) throws CoreException {
		return createFolder(path, null);
	}

	public IFolder createFolder(IPath path, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = NULL_MONITOR;
		}
		ContainerCreator creator = new ContainerCreator(ResourcesPlugin.getWorkspace(), path);
		IContainer container = creator.createContainer(monitor);
		if (container instanceof IFolder) {
			return (IFolder) container;
		}
		return null;
	}


	public IFolder createFolder(String portableFolderPath) throws CoreException {
		return createFolder(portableFolderPath, null);
	}

	public IFolder createFolder(String portableFolderPath, IProgressMonitor monitor) throws CoreException {
		Path fullPath = new Path(portableFolderPath);
		return createFolder(fullPath, monitor);
	}

	public IFile createLinkedFile(IContainer container, IPath linkPath, File linkedFileTarget) throws CoreException {
		IFile iFile = container.getFile(linkPath);
		iFile.createLink(new Path(linkedFileTarget.getAbsolutePath()), IResource.ALLOW_MISSING_LOCAL, NULL_MONITOR);
		return iFile;
	}

	public IFile createLinkedFile(IContainer container, IPath linkPath, Plugin plugin, IPath linkedFileTargetPath)
			throws CoreException {
		File file = getFileInPlugin(plugin, linkedFileTargetPath);
		IFile iFile = container.getFile(linkPath);
		iFile.createLink(new Path(file.getAbsolutePath()), IResource.ALLOW_MISSING_LOCAL, NULL_MONITOR);
		return iFile;
	}

	public IFolder createLinkedFolder(IContainer container, IPath linkPath, File linkedFolderTarget)
			throws CoreException {
		IFolder folder = container.getFolder(linkPath);
		folder.createLink(new Path(linkedFolderTarget.getAbsolutePath()), IResource.ALLOW_MISSING_LOCAL, NULL_MONITOR);
		return folder;
	}

	public IFolder createLinkedFolder(IContainer container, IPath linkPath, Plugin plugin, IPath linkedFolderTargetPath)
			throws CoreException {
		File file = getFileInPlugin(plugin, linkedFolderTargetPath);
		IFolder iFolder = container.getFolder(linkPath);
		iFolder.createLink(new Path(file.getAbsolutePath()), IResource.ALLOW_MISSING_LOCAL, NULL_MONITOR);
		return iFolder;
	}
	public IProject createLinkedProject(String projectName, Plugin plugin, IPath linkPath) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(projectName);

		IProjectDescription desc = workspace.newProjectDescription(projectName);
		File file = getFileInPlugin(plugin, linkPath);
		IPath projectLocation = new Path(file.getAbsolutePath());
		if (Platform.getLocation().equals(projectLocation))
			projectLocation = null;
		desc.setLocation(projectLocation);

		project.create(desc, NULL_MONITOR);
		if (!project.isOpen())
			project.open(NULL_MONITOR);

		return project;
	}

	/**
	 * Creates or refreshes virtual root project. If project exists but isn't opened it will
	 * be automatically opened
	 * 
	 * @param projectName
	 * @param monitor
	 * @param projectDescriptionCreator 
	 * @param natureIds
	 * @return project
	 * @throws CoreException
	 */
	public IProject createOrRefreshProject(String projectName, IProgressMonitor monitor, ProjectDescriptionCreator projectDescriptionCreator,
			String... natureIds) throws CoreException {
		if (monitor == null) {
			monitor = NULL_MONITOR;
		}
		IProject project = getProject(projectName);
		if (!project.exists()) {
			IProjectDescription initialDescription = projectDescriptionCreator.createNewProjectDescription(projectName);
			project.create(initialDescription, monitor);

		} else {
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}

		if (!project.isOpen()) {
			project.open(monitor);
		}
		/*
		 * the next lines are important: only when we do set description on
		 * project again the nature will be created AND configured as wished -
		 * necessary to get builder running
		 * 
		 */
		IProjectDescription descriptionCopy = project.getDescription();
		descriptionCopy.setNatureIds(natureIds);
		project.setDescription(descriptionCopy, monitor);
		return project;
	}

	private IProject getProject(String projectName) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject(projectName);
		return project;
	}

	public File createTempFileInPlugin(Plugin plugin, IPath path) {
		IPath stateLocation = plugin.getStateLocation();
		stateLocation = stateLocation.append(path);
		return stateLocation.toFile();
	}

	public void delete(IPath path) throws CoreException {
		IFileStore fileStore = FileBuffers.getFileStoreAtLocation(path);

		File file = null;
		file = fileStore.toLocalFile(EFS.NONE, NULL_MONITOR);
		try {
			getFileHelper().delete(file);
		} catch (IOException e) {
			EGradleUtil.throwCoreException("Was not able to delete path:"+path, e);
		}
	}

	public void delete(final IProject project) throws CoreException {
		delete(project, true);
	}

	public void delete(final IProject project, boolean deleteContent) throws CoreException {

		for (int i = 0; i < MAX_RETRY; i++) {
			try {
				project.delete(deleteContent, true, NULL_MONITOR);
				i = MAX_RETRY;
			} catch (CoreException x) {
				if (i == MAX_RETRY - 1) {
					EGradleUtil.log(x.getStatus());
				}
				try {
					Thread.sleep(1000); // sleep a second
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void deleteProject(String projectName) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		IPath location = project.getLocation();
		if (project.exists()) {
			delete(project);
		}
		if (location != null) {
			/*
			 * we do a hard cleanup here no matter if the project has existed in
			 * workspace before or not.
			 * 
			 */
			deleteRecursive(location.toFile());
		}
	}

	public File getFileInPlugin(Plugin plugin, IPath path) throws CoreException {
		try {
			URL installURL = plugin.getBundle().getEntry(path.toString());
			URL localURL = FileLocator.toFileURL(installURL);
			return new File(localURL.getFile());
		} catch (IOException e) {
			throw new PathException(IStatus.ERROR, path, "cannot get file in plugin", e);
		}
	}

	/**
	 * Gets simple file name without extension
	 * @param resource
	 * @return file name, no extension
	 */
	public String getFileName(IResource resource) {
		String extension = resource.getFileExtension();
		String name = resource.getName();
		if (StringUtils.isBlank(name)){
			return "";
		}
		if (StringUtils.isNotEmpty(extension)){
			int length = extension.length()+1;/* +1 because of dot*/
			String result= name.substring(0,name.length()-length);
			return result;
		}else{
			return name;
		}
	}

	/**
	 * Returns the file or <code>null</code>
	 * @param path
	 * @return file or <code>null</code>
	 * @throws CoreException
	 */
	public File toFile(IPath path) throws CoreException {
		if (path==null){
			return null;
		}
		IFileStore fileStore = FileBuffers.getFileStoreAtLocation(path);

		File file = null;
		file = fileStore.toLocalFile(EFS.NONE, NULL_MONITOR);
		return file;
	}

	public File toFile(IResource resource) throws CoreException {
		if (resource==null){
			return toFile((IPath)null);
		}
		return toFile(resource.getLocation());
	}

	/**
	 * Returns the IFile representation for given file or <code>null</code> if file not in workspace
	 * @param file
	 * @return file or null
	 * @deprecated does not work correctly. Better: IFileStore fileStore = EFS.getLocalFileSystem().getStore(localFile.toURI());
	 */
	public IFile toIFile(File file) {
		IPath path = Path.fromOSString(file.getAbsolutePath()); 
		return toIFile(path);
	}

	public IFile toIFile(IPath path) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFile fileResult = workspace.getRoot().getFile(path);
		return fileResult;
	}

	public IFile toIFile(String pathString) {
		IPath path = Path.fromOSString(pathString);
		return toIFile(path);
	}

	public IPath toPath(File tempFolder) {
		notNull(tempFolder, "'tempFolder' may not be null");
		IPath path = Path.fromOSString(tempFolder.getAbsolutePath());
		return path;
	}

	private IFile createFile(IFile file, String name, String contents) throws CoreException {
		if (contents == null)
			contents = "";
		InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
		file.create(inputStream, true, NULL_MONITOR);
		return file;
	}

	private void deleteRecursive(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				deleteRecursive(child);
			}
			file.delete();
		} else {
			file.delete();
		}
	}

	private FileHelper getFileHelper(){
		return fileHelper;
	}

	/**
	 * Imports a project by given description file (.project). If a project already exists in workspace with same name
	 * it will be automatically deleted (without content delete)
	 * @param projectFileOrFolder file (.project) or the folder containing it
	 * @param monitor
	 * @return project, never <code>null</code>
	 * @throws CoreException
	 */
	public IProject importProject(File projectFileOrFolder, IProgressMonitor monitor) throws CoreException {
		File projectFile = null;
		if (projectFileOrFolder.isDirectory()){
			projectFile=new File(projectFileOrFolder,GradleImportScanner.ECLIPSE_PROJECTFILE_NAME);
		}else{
			projectFile=projectFileOrFolder;
		}
		Path path = new Path(projectFile.getAbsolutePath());
		IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(path);
		IProject project = getProject(description.getName());
		/* always delete project if already existing - but without content delete */
		project.delete(false, true,monitor);
		project.create(description, monitor);
		return project;
	}

}