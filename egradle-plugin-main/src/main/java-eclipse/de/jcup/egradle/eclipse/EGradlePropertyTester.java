package de.jcup.egradle.eclipse;

import java.io.File;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import de.jcup.egradle.eclipse.api.EGradleUtil;

public class EGradlePropertyTester extends PropertyTester{

	public static final String PROPERTY_NAMESPACE = "de.jcup.egradle";
	public static final String PROPERTY_IS_PROJECT_PART_OF_ROOTPROJECT= "isProjectPartOfRootProject";
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (! (receiver instanceof IResource)) {
			/* not supported */
			return false;
		}
		File rootFolder = EGradleUtil.getRootProjectFolderWithoutErrorHandling();
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
			if (EGradleUtil.isRootProject(project)){
				/* project itself is root project - normally only for single projects */
				return evalResult(expectedToBeRootPartOfRootProject,true);
			}
			/* virtual root project */
			if (EGradleUtil.hasVirtualRootProjectNature(project)){
				return evalResult(expectedToBeRootPartOfRootProject,true);
			}
			
			/* sub project detection */
			try {
				if (EGradleUtil.isSubprojectOfCurrentRootProject(project)){
					return evalResult(expectedToBeRootPartOfRootProject,true);
				}
			} catch (CoreException e) {
				EGradleUtil.log(e);
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
