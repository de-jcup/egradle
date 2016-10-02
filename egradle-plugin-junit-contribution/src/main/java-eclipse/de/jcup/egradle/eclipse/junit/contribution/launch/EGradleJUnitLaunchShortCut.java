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
package de.jcup.egradle.eclipse.junit.contribution.launch;

import static de.jcup.egradle.eclipse.launch.EGradleLauncherConstants.PROPERTY_TASKS;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.api.FileHelper;
import de.jcup.egradle.eclipse.launch.EGradleLaunchShortCut;

public class EGradleJUnitLaunchShortCut extends EGradleLaunchShortCut {

	public static final String DEFAULT_TASKS = "clean test";

	/**
	 * Returns the type of configuration this shortcut is applicable to.
	 * 
	 * @return the type of configuration this shortcut is applicable to
	 */
	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager()
				.getLaunchConfigurationType("de.jcup.egradle.junit.contribution.launchConfigurationType");
	}

	@Override
	protected void createCustomConfiguration(IResource resource, ILaunchConfigurationWorkingCopy wc,
			String projectName) {
		super.createCustomConfiguration(resource, wc, projectName);

		if (!(resource instanceof IFile)) {
			wc.setAttribute(PROPERTY_TASKS, DEFAULT_TASKS);
			return;
		}
		String fullClassName = null;
		/* create package name for resource */
		IFile file = (IFile) resource;
		IJavaElement javaElement = JavaCore.create(file);
		if (javaElement instanceof CompilationUnit) {
			CompilationUnit cu = (CompilationUnit) javaElement;
			javaElement=cu.getJavaElement();
		} 
		if (javaElement instanceof ICompilationUnit) {
			ICompilationUnit cu = (ICompilationUnit) javaElement;
			try {
				IType[] types = cu.getTypes();
				if (types!=null){
					for (IType type: types){
						fullClassName=type.getFullyQualifiedName();
						if (fullClassName!=null){
							break;
						}
					}
				}
			} catch (JavaModelException e) {
				EGradleUtil.log(e);
				return;
			}
		} else if (javaElement instanceof IType) {
			IType type = (IType) javaElement;
			fullClassName = type.getFullyQualifiedName();
		}
		/*
		 * Fallback when having not a java element with correct full classname set. Can happen
		 * when calls are done from a virtual root project path
		 */
		if (fullClassName.indexOf('.')==-1){
			/* not a real classpath...*/
			fullClassName="*"+fullClassName+"*";
					
		}
		// for information about the gradle call
		// see https://docs.gradle.org/1.10/release-notes
		wc.setAttribute(PROPERTY_TASKS, DEFAULT_TASKS+" --tests " + fullClassName);
	}

	@Override
	protected String createLaunchConfigurationNameProposal(String projectName, IResource resource) {
		String name = super.createLaunchConfigurationNameProposal(projectName, resource);
		if (resource instanceof IFile) {
			String fileName = FileHelper.SHARED.getFileName(resource);
			name = name + "#" + fileName;
		}
		return name;
	}

	protected void createTaskConfiguration(ILaunchConfigurationWorkingCopy wc) {
		/* done ins custom confguration! */
	}

	protected String getChooseConfigurationTitle() {
		return "Choose EGradle Junit Test config";
	}

	@Override
	protected boolean isConfigACandidate(IResource resource, ILaunchConfiguration config) throws CoreException {
		boolean candidate = super.isConfigACandidate(resource, config);
		if (candidate) {
//			if (resource instanceof IFile) {
				IResource[] resources = config.getMappedResources();
				if (resources == null || resources.length == 0) {
					return false;
				}
				IResource resourceToCheck = resources[0];
				if (!resourceToCheck.exists()) {
					return false;
				}
				if (resourceToCheck.getLocation().equals(resource.getLocation())) {
					return true;
				}
//			}else if (resource instanceof IFolder){
//				
//			}
		}
		return false;
	}
}
