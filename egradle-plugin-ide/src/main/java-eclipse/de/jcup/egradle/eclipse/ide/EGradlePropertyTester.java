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
 package de.jcup.egradle.eclipse.ide;

import java.io.File;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class EGradlePropertyTester extends PropertyTester{

	public static final String PROPERTY_NAMESPACE = "de.jcup.egradle";
	public static final String PROPERTY_IS_PROJECT_PART_OF_ROOTPROJECT= "isProjectPartOfRootProject";
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (! (receiver instanceof IResource)) {
			/* not supported */
			return false;
		}
		File rootFolder = IDEUtil.getRootProjectFolderWithoutErrorHandling();
		if (rootFolder==null){
			return false;
		}
		IResource resource = (IResource) receiver;
		
		IProject project = null;
		if (resource instanceof IProject){
			project = (IProject) resource;
		}else{
			project = resource.getProject();
		}
		if (project==null){
			return false;
		}
		if (!project.exists()){
			return false;
		}
		
		if (PROPERTY_IS_PROJECT_PART_OF_ROOTPROJECT.equals(property)){
			if (! (expectedValue instanceof Boolean)){
				return false;
			}
			boolean expectedToBeRootPartOfRootProject = (Boolean)expectedValue;
			/* root project itself */
			if (IDEUtil.isRootProject(project)){
				/* project itself is root project - normally only for single projects */
				return evalResult(expectedToBeRootPartOfRootProject,true);
			}
			/* virtual root project */
			if (IDEUtil.hasVirtualRootProjectNature(project)){
				return evalResult(expectedToBeRootPartOfRootProject,true);
			}
			
			/* sub project detection */
			try {
				if (IDEUtil.isSubprojectOfCurrentRootProject(project)){
					return evalResult(expectedToBeRootPartOfRootProject,true);
				}
			} catch (CoreException e) {
				IDEUtil.logError("Was not able to evaluate project:"+project,e);
				return evalResult(expectedToBeRootPartOfRootProject,false);
			}
			return evalResult(expectedToBeRootPartOfRootProject,false);
		}
		return false;
	}

	private boolean evalResult(boolean expected, boolean state){
		if (state==expected){
			return true;
		}
		return false;
	}
}
