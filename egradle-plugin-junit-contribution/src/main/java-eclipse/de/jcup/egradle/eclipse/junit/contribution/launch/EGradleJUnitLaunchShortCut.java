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

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import de.jcup.egradle.eclipse.launch.EGradleLaunchShortCut;

public class EGradleJUnitLaunchShortCut extends EGradleLaunchShortCut{

	public static final String DEFAULT_TASKS = "clean test";

	/**
	 * Returns the type of configuration this shortcut is applicable to.
	 * 
	 * @return the type of configuration this shortcut is applicable to
	 */
	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType("de.jcup.egradle.junit.contribution.launchConfigurationType");
	}
	
	protected void createTaskConfiguration(ILaunchConfigurationWorkingCopy wc) {
		wc.setAttribute(PROPERTY_TASKS, DEFAULT_TASKS);
	}
	
	protected String getChooseConfigurationTitle() {
		return "Choose EGradle Junit Test config";
	}
}
