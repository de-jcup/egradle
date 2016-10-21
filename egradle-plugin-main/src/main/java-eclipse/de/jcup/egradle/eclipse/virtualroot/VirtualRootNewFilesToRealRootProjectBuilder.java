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

import static de.jcup.egradle.eclipse.api.EGradleUtil.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.api.FileHelper;
import de.jcup.egradle.eclipse.api.ResourceHelper;

/**
 * Special project builder to support new files added on virtual root inside
 * eclipse are transfered automatically to real root project and are replaced by
 * link (links are of course not copied...)
 * 
 * @author Albert Tregnaghi
 *
 */
public class VirtualRootNewFilesToRealRootProjectBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "de.jcup.egradle.eclipse.plugin.main.virtualRootProjectBuilder";

	protected void handleAddedInRootFolder(File rootFolder, IResource resource) throws CoreException {
		
		/* check we got no .gitignore or the .project here! This are the only two files, to complete ignore!*/
		File sourceFile = getFileHelper().toFile(resource);
		String name = sourceFile.getName();
		if (".gitignore".equals(name)){
			return;
		}
		
		if (".project".equals(name)){
			return;
		}
		
//		System.out.println("handle added in root folder:" + resource.getLocation());

		File targetFile = new File(rootFolder, sourceFile.getName());
		IPath targetPathInProject = Path.fromPortableString(sourceFile.getName());

		IProject project = resource.getProject();

		try {
			getFileHelper().copy(sourceFile, targetFile);
		} catch (IOException e) {
			EGradleUtil.throwCoreException("Cannot handle root folder add action, copy of source file failed", e);
		}
		/*
		 * after copy we delete the entry - be aware about infinite loops when
		 * handling delete events...
		 */
		resource.delete(true, null);

		/*
		 * create new linked file - infinite loop may not happen because a link.
		 * Links are filtered in pre event handling
		 */
		if (targetFile.isDirectory()){
			getResourceHelper().createLinkedFolder(project, targetPathInProject, targetFile);
			project.refreshLocal(2, null);
		}else{
			getResourceHelper().createLinkedFile(project, targetPathInProject, targetFile);
		}
	}

	/**
	 * Method is called when its clear that resource was a link before. The
	 * correct link target which shall be deleted, is already calculated
	 * 
	 * @param linkTargetFile
	 * @param resource
	 */
	protected void handleLinkedFileRemovedFromRootFolder(File linkTargetFile, IResource resource) {
		/*
		 * eclipse will before the deletion a dialog about ... will not be
		 * deleted... etc.
		 */
		/* we have to ask the user if he really wants to delete instead... */
		String message = "Do you like to delete " + resource.getName()
				+ " from root project as you have done with the link?\n" + linkTargetFile.getAbsolutePath();
		EGradleUtil.safeAsyncExec(new Runnable() {

			@Override
			public void run() {
				Shell shell = getActiveWorkbenchShell();
				boolean deleteTheFile = MessageDialog.openQuestion(shell, "EGradle", message);
				if (deleteTheFile) {
					try {
						getFileHelper().delete(linkTargetFile);
					} catch (CoreException e) {
						EGradleUtil.log(e);
						;
					}
				}
			}

		});

	}

	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		IProject project = getProject();
		if (!project.isOpen()) {
			return null;
		}
		if (!(project.hasNature(VirtualRootProjectNature.NATURE_ID))) {
			return null;
		}
		File projectOnRealFileSystem = project.getLocation().toFile();
		File rootProjectFolder = projectOnRealFileSystem.getParentFile();
		if (kind == FULL_BUILD) {
			fullBuild(rootProjectFolder, monitor);
		} else {

			IResourceDelta delta = getDelta(project);
			if (delta == null) {
				fullBuild(rootProjectFolder, monitor);
			} else {
				incrementalBuild(rootProjectFolder, delta, monitor);
			}
		}
		return null;
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
	}

	protected void fullBuild(File rootProjectFolder, final IProgressMonitor monitor) throws CoreException {
		try {
			getProject().accept(new VirtualRootFolderResourceVisitor(rootProjectFolder));
		} catch (CoreException e) {
		}
	}

	protected void incrementalBuild(File rootProjectFolder, IResourceDelta delta, IProgressMonitor monitor)
			throws CoreException {
		delta.accept(new VirtualRootFolderDeltaVisitor(rootProjectFolder));
	}

	private ResourceHelper getResourceHelper() {
		return ResourceHelper.SHARED;
	}

	private FileHelper getFileHelper() {
		return ResourceHelper.SHARED.getFileHelper();
	}

	class VirtualRootFolderDeltaVisitor implements IResourceDeltaVisitor {
		private File rootProjectFolder;

		public VirtualRootFolderDeltaVisitor(File rootProjectFolder) {
			this.rootProjectFolder = rootProjectFolder;
		}

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();

			int kind = delta.getKind();
			switch (kind) {
			case IResourceDelta.ADDED:
				if (resource.isLinked(IResource.CHECK_ANCESTORS)) {
					/* add of an link is not interesting... */
					return true;
				}
				handleAddedInRootFolder(rootProjectFolder, resource);
				break;
			case IResourceDelta.REMOVED:
				/*
				 * it seems we have not the possibility to check already deleted
				 * links like this:
				 */
				// if (!resource.isLinked(IResource.NONE/ANCESTOR...)){
				File file = resource.getLocation().toFile();
				File parentFile = file.getParentFile();
				if (!".egradle".equals(parentFile.getName())) {
					/*
					 * removed something outside the .egradle folder - so done
					 * in file system correctly
					 */
					return true;
				}
				File linkTargetFile = new File(rootProjectFolder, file.getName());
				if (linkTargetFile.exists()) {
					/* we got it inside root folder, so this was a link */
					handleLinkedFileRemovedFromRootFolder(linkTargetFile, resource);
				}
				break;
			case IResourceDelta.CHANGED:
				break;
			}
			// return true to continue visiting children.
			return true;
		}
	}

	class VirtualRootFolderResourceVisitor implements IResourceVisitor {

		private File rootProjectFolder;

		public VirtualRootFolderResourceVisitor(File rootProjectFolder) {
			this.rootProjectFolder = rootProjectFolder;
		}

		public boolean visit(IResource resource) throws CoreException {
			File file = resource.getFullPath().toFile();
			if (resource.isLinked(IResource.CHECK_ANCESTORS)) {
				return true;
			}
			// }
			boolean isLinkCandidate = EclipseVirtualProjectPartCreator.isLinkCandidate(getProject(), file);
			if (isLinkCandidate) {
				/* ups ... should not be there */
				handleAddedInRootFolder(rootProjectFolder, resource);
			}
			// return false to not continue visiting children.
			return true;
		}
	}
}
