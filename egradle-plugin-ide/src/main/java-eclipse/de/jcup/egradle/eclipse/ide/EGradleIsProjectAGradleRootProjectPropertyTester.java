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
import org.eclipse.core.runtime.CoreException;

import de.jcup.egradle.core.util.GradleInfoSupport;
import de.jcup.egradle.eclipse.util.EclipseResourceHelper;

/**
 * Will check if the given resource is a project and if the project is a root
 * project (means root for multi projects, or at single projects, the single
 * project itself) 
 * 
 * @author Albert Tregnaghi
 *
 */
public class EGradleIsProjectAGradleRootProjectPropertyTester extends PropertyTester {

	public static final String PROPERTY_NAMESPACE = "de.jcup.egradle";
	public static final String PROPERTY_IS_PROJECT_GRADLE_ROOT_PROJECT = "isProjectAGradleRootProject";
	private GradleInfoSupport gradleInfoSupport;
	private EclipseResourceHelper eclipseResourceHelper;

	public EGradleIsProjectAGradleRootProjectPropertyTester() {
		gradleInfoSupport = new GradleInfoSupport();
		eclipseResourceHelper = new EclipseResourceHelper();
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (!PROPERTY_IS_PROJECT_GRADLE_ROOT_PROJECT.equals(property)) {
			return false;
		}
		/* FIXME ATR, 23.06.2017: make root project field editor invisible at project setup */
		/* FIXME ATR, 23.06.2017: setup components initial with data from global root project setup */
		/* FIXME ATR, 23.06.2017: change handle launching etc. with project specific root project config */
		/* FIXME ATR, 23.06.2017: property page must have checkbox "a) use setup from global root config b) use custom config */
		/* FIXME ATR, 23.06.2017: store info at .egradleProject ?!?! */
		/* FIXME ATR, 23.06.2017: decide .gitignore for .egradleProject */
		/* FIXME ATR, 23.06.2017: extend launch configurations so projects are added on "run as"... etc. */
		/* FIXME ATR, 23.06.2017: use information from projects for launches */
		/* FIXME ATR, 23.06.2017: maybe the sub projects should also have the property page, but should configure there root project instead
		 *  */
		if (!(receiver instanceof IProject)) {
			/* not supported */
			return false;
		}
		if (!(expectedValue instanceof Boolean)) {
			return false;
		}

		IProject project = (IProject) receiver;
		if (!project.exists()) {
			return false;
		}

		boolean expectedToBeRootProject = (Boolean) expectedValue;

		File folder;
		try {
			folder = eclipseResourceHelper.toFile(project.getLocation());
		} catch (CoreException e) {
			return false;
		}
		boolean isAGradleRootProjectFolder = gradleInfoSupport.isGradleRootProjectFolder(folder);

		return evalResult(expectedToBeRootProject, isAGradleRootProjectFolder);
	}

	private boolean evalResult(boolean expected, boolean state) {
		if (state == expected) {
			return true;
		}
		return false;
	}
}
