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
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.virtualroot.RootProjectVisitor;
import de.jcup.egradle.core.virtualroot.VirtualRootProjectException;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.api.ResourceHelper;

public class RootProjectEclipseVisitor implements RootProjectVisitor {
	ResourceHelper r = ResourceHelper.SHARED;
	private IProject newProject;

	private List<File> foldersToIgnore;
	private File rootFolder;
	
	public RootProjectEclipseVisitor(GradleRootProject rootProject){
		notNull(rootProject, "'rootProject' may not be null");
		this.rootFolder = rootProject.getFolder();
	}
	
	@Override
	public Object createOrRecreateProject(String projectName) throws VirtualRootProjectException {
		try {
			/* setup folder creation */
			foldersToIgnore=new ArrayList<>();
			/* check if already an eclipse project*/
			IProject[] projects = EGradleUtil.getAllProjects();
			for (IProject p: projects){
				try {
					File projectPath = r.getFileHelper().toFile(p.getLocation());
					if (projectPath.getParentFile().equals(rootFolder)){
						/* already an eclipse project!*/
						foldersToIgnore.add(projectPath);
					}
					
				} catch (CoreException e) {
					throw new VirtualRootProjectException("Was not able to check project:"+p,e);
				}
			}
			
			r.deleteProject(projectName);
			newProject = r.createOrRefreshProject(projectName);
			return newProject;
			
		} catch (CoreException e) {
			throw new VirtualRootProjectException("Cannot delete newProject:" + projectName, e);
		}
	}

	@Override
	public boolean needsFolderToBeCreated(Object targetFolder, File file) throws VirtualRootProjectException{
		if (targetFolder==newProject){
			if (foldersToIgnore.contains(file)){
				return false;
			}
			return true;
		}
		/* we do not dive into*/
		return false;
		
	}

	@Override
	public void createLink(Object targetParentFolder, File file) throws VirtualRootProjectException {
		notNull(targetParentFolder, "'targetParentFolder' may not be null");
		
		if (! (targetParentFolder instanceof IContainer)){
			throw new VirtualRootProjectException("Target folder clazz not supported:"+targetParentFolder);
		}
		IContainer container = (IContainer) targetParentFolder;
		IPath path = Path.fromPortableString(file.getName());
		try {
				if (file.isDirectory()){
					r.createLinkedFolder(container,path,file);
				}else{
					r.createLinkedFile(container, path, file);
					
				}
		} catch (CoreException e) {
			throw new VirtualRootProjectException("Was not able to create link to file:"+file,e);
		}
		
	}

//	public Object createFolder(Object targetParentFolder, File file) throws VirtualRootProjectException {
//		if (targetParentFolder instanceof IContainer){
//			IContainer c = (IContainer) targetParentFolder;
//			try {
//				Object folder = r.createFolder(c.getLocation().append(file.getName()));
//				return folder;
//			} catch (CoreException e) {
//				throw new VirtualRootProjectException("Was not able to create folder:"+file,e);
//			}
//		}
//		throw new VirtualRootProjectException("Was not able to create folder:"+file);
//	}



}