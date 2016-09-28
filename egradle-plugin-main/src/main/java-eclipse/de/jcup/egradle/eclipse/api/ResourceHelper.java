package de.jcup.egradle.eclipse.api;

import static org.apache.commons.lang3.Validate.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.eclipse.core.filebuffers.manipulation.ContainerCreator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;

public class ResourceHelper {
	public static ResourceHelper SHARED= new ResourceHelper(FileHelper.SHARED);
	
	private final IProgressMonitor NULL_MONITOR = new NullProgressMonitor();
	private final int MAX_RETRY = 5;
	private FileHelper fileHelper;
	
	public ResourceHelper(FileHelper fileHelper){
		notNull(fileHelper, "'fileHelper' may not be null");
		this.fileHelper=fileHelper;
	}
	/**
	 * Creates or refreshes project. If project exists but isn't opened it will be automatically opened
	 * @param projectName
	 * @return project
	 * @throws CoreException
	 */
	public IProject createOrRefreshProject(String projectName) throws CoreException {
		return createOrRefreshProject(projectName,null);
	}

		
	/**
	 * Creates or refreshes project. If project exists but isn't opened it will be automatically opened
	 * @param projectName
	 * @param monitor
	 * @return project
	 * @throws CoreException
	 */
	public IProject createOrRefreshProject(String projectName, IProgressMonitor monitor) throws CoreException {
		if (monitor==null){
			monitor = NULL_MONITOR;
		}
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		if (!project.exists()){
			project.create(monitor);
		}
		else{
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}

		if (!project.isOpen()){
			project.open(monitor);
		}
		return project;
	}

	public void deleteProject(String projectName) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		if (project.exists()){
			delete(project);
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
	
	public FileHelper getFileHelper() {
		return fileHelper;
	}

	public IFolder createFolder(String portableFolderPath) throws CoreException {
		return createFolder(portableFolderPath,null);
	}
	public IFolder createFolder(String portableFolderPath, IProgressMonitor monitor) throws CoreException {
		Path fullPath = new Path(portableFolderPath);
		return createFolder(fullPath, monitor);
	}
	public IFolder createFolder(IPath path, IProgressMonitor monitor) throws CoreException {
		if (monitor==null){
			monitor = NULL_MONITOR;
		}
		ContainerCreator creator = new ContainerCreator(ResourcesPlugin.getWorkspace(), path);
		IContainer container = creator.createContainer(monitor);
		if (container instanceof IFolder){
			return (IFolder) container;
		}
		return null;
	}

	public IFile createFile(IFolder folder, String name, String contents) throws CoreException {
		return createFile(folder.getFile(name), name, contents);
	}

	public IFile createFile(IProject project, String name, String contents) throws CoreException {
		return createFile(project.getFile(name), name, contents);
	}

	private IFile createFile(IFile file, String name, String contents) throws CoreException {
		if (contents == null)
			contents = "";
		InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
		file.create(inputStream, true, NULL_MONITOR);
		return file;
	}

	public IFile createLinkedFile(IContainer container, IPath linkPath, File linkedFileTarget) throws CoreException {
		IFile iFile = container.getFile(linkPath);
		iFile.createLink(new Path(linkedFileTarget.getAbsolutePath()), IResource.ALLOW_MISSING_LOCAL, NULL_MONITOR);
		return iFile;
	}

	public IFile createLinkedFile(IContainer container, IPath linkPath, Plugin plugin, IPath linkedFileTargetPath)
			throws CoreException {
		File file = fileHelper.getFileInPlugin(plugin, linkedFileTargetPath);
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
		File file = fileHelper.getFileInPlugin(plugin, linkedFolderTargetPath);
		IFolder iFolder = container.getFolder(linkPath);
		iFolder.createLink(new Path(file.getAbsolutePath()), IResource.ALLOW_MISSING_LOCAL, NULL_MONITOR);
		return iFolder;
	}

	public IProject createLinkedProject(String projectName, Plugin plugin, IPath linkPath) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(projectName);

		IProjectDescription desc = workspace.newProjectDescription(projectName);
		File file = fileHelper.getFileInPlugin(plugin, linkPath);
		IPath projectLocation = new Path(file.getAbsolutePath());
		if (Platform.getLocation().equals(projectLocation))
			projectLocation = null;
		desc.setLocation(projectLocation);

		project.create(desc, NULL_MONITOR);
		if (!project.isOpen())
			project.open(NULL_MONITOR);

		return project;
	}
}