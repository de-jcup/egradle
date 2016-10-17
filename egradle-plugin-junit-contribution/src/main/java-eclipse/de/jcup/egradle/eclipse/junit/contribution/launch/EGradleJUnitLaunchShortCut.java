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

import static de.jcup.egradle.eclipse.junit.contribution.preferences.EGradleJUnitPreferences.*;
import static de.jcup.egradle.eclipse.launch.EGradleLauncherConstants.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import de.jcup.egradle.eclipse.JavaHelper;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.api.FileHelper;
import de.jcup.egradle.eclipse.junit.contribution.JunitIntegrationConstants;
import de.jcup.egradle.eclipse.junit.contribution.preferences.EGradleJUnitTestTasksType;
import de.jcup.egradle.eclipse.junit.contribution.preferences.EGradleJunitPreferenceConstants;
import de.jcup.egradle.eclipse.launch.EGradleLaunchShortCut;

public class EGradleJUnitLaunchShortCut extends EGradleLaunchShortCut {

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
	protected void createCustomConfiguration(IResource resource, Object additionalScope,
			ILaunchConfigurationWorkingCopy wc, String projectName) {
		super.createCustomConfiguration(resource, additionalScope, wc, projectName);

		if (!(resource instanceof IFile)) {
			wc.setAttribute(PROPERTY_TASKS, getTestTasks());
			return;
		}
		String fullClassName = null;
		/* create package name for resource */
		IFile file = (IFile) resource;
		IJavaElement javaElement = JavaCore.create(file);

		if (javaElement instanceof ICompilationUnit) {
			ICompilationUnit cu = (ICompilationUnit) javaElement;
			try {
				IType[] types = cu.getTypes();
				if (types != null) {
					for (IType type : types) {
						fullClassName = type.getFullyQualifiedName();
						if (fullClassName != null) {
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
		 * Fallback when having not a java element with correct full classname
		 * set. Can happen when calls are done from a virtual root project path
		 */
		if (fullClassName.indexOf('.') == -1) {
			/* not a real classpath... */
			fullClassName = "*" + fullClassName + "*";

		}
		// for information about the gradle call
		// see https://docs.gradle.org/1.10/release-notes
		if (additionalScope instanceof IMethod) {
			IMethod method = (IMethod) additionalScope;
			String methodName = method.getElementName();
			fullClassName += "." + methodName;
			wc.setAttribute(JunitIntegrationConstants.TEST_METHOD_NAME, methodName);
		}
		wc.setAttribute(PROPERTY_TASKS, getTestTasks() + " --tests " + fullClassName);

	}

	/**
	 * Returns test tasks configured in preferences
	 * 
	 * @return test tasks
	 */
	public static String getTestTasks() {
		String configuredTestTaskTypeId = JUNIT_PREFERENCES
				.getStringPreference(EGradleJunitPreferenceConstants.P_TEST_TASKS);
		EGradleJUnitTestTasksType testTasksType = EGradleJUnitTestTasksType.findById(configuredTestTaskTypeId);
		/* fall back */
		if (testTasksType == null) {
			testTasksType = EGradleJUnitTestTasksType.CLEAN_ALL;
		}
		String testTasks = testTasksType.getTestTasks();
		return testTasks;
	}

	@Override
	protected String createLaunchConfigurationNameProposal(String projectName, IResource resource,
			Object additionalScope) {
		String name = super.createLaunchConfigurationNameProposal(projectName, resource, additionalScope);
		if (resource instanceof IFile) {
			String fileName = FileHelper.SHARED.getFileName(resource);
			name = name + "#" + fileName;
		}
		if (additionalScope instanceof IMethod) {
			IMethod method = (IMethod) additionalScope;
			name = name + "." + method.getElementName();
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
	protected Object resolveEditorAdditonalScope(IEditorPart editor) {
		if (editor instanceof ITextEditor) {
			ITextEditor textEditor = (ITextEditor) editor;
			IMethod method = JavaHelper.SHARED.getCurrentSelectedJavaMethod(textEditor);
			return method;
		}
		return null;
	}

	@Override
	protected boolean isResourceToMapFilesAllowed() {
		/*
		 * when launch configuration is used for a specific file this is okay
		 * and the test will use the selected class file
		 */
		return true;
	}

	@Override
	protected boolean isConfigACandidate(IResource resource, Object additionalScope, ILaunchConfiguration config)
			throws CoreException {

		boolean candidate = super.isConfigACandidate(resource, additionalScope, config);
		if (candidate) {

			IResource[] resources = config.getMappedResources();
			if (resources == null || resources.length == 0) {
				return false;
			}
			IResource resourceToCheck = resources[0];
			if (!resourceToCheck.exists()) {
				return false;
			}
			if (resourceToCheck.getLocation().equals(resource.getLocation())) {
				if (additionalScope instanceof IMethod) {
					IMethod method = (IMethod) additionalScope;
					String launchMethodName = config.getAttribute(JunitIntegrationConstants.TEST_METHOD_NAME,
							(String) null);
					if (launchMethodName == null) {
						/* method is wished... so not a candidate */
						return false;
					}
					String selectedMethodName = method.getElementName();
					if (launchMethodName.equals(selectedMethodName)) {
						return true;
					}
					return false;
				}
				/*
				 * without method selection - means all tests of class. So
				 * filter launch configs with set method name...
				 */
				String launchMethodName = config.getAttribute(JunitIntegrationConstants.TEST_METHOD_NAME,
						(String) null);
				if (launchMethodName == null) {
					/* method is Not wished... so its a candidate */
					return true;
				}
				return false;
			}
		}
		return false;
	}
}
