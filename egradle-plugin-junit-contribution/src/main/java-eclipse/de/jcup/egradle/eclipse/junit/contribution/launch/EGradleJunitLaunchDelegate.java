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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import de.jcup.egradle.eclipse.api.EclipseUtil;
import de.jcup.egradle.eclipse.junit.contribution.ImportGradleJunitResultsJob;
import de.jcup.egradle.eclipse.launch.EGradleLaunchDelegate;
import de.jcup.egradle.eclipse.launch.LaunchParameterValues;
import de.jcup.egradle.junit.EGradleJUnitTaskVariableReplacement;

public class EGradleJunitLaunchDelegate extends EGradleLaunchDelegate{

	private EGradleJUnitTaskVariableReplacement variableReplacement = new EGradleJUnitTaskVariableReplacement();
	
	@Override
	protected void appendAdditionalLaunchParameters(LaunchParameterValues launchParameterValues) {
		ILaunch launch = launchParameterValues.getLaunch();
		if (launch==null){
			throw new IllegalStateException("launch missing");
		}
		String projectName;
		try {
			ILaunchConfiguration configuration = launch.getLaunchConfiguration();
			
			projectName = configuration.getAttribute(PROPERTY_PROJECTNAME, (String)null);
			
			String configuredTasksValue= configuration.getAttribute(PROPERTY_TASKS, "");
			String tasksToExecute = variableReplacement.replace(configuredTasksValue, JUNIT_PREFERENCES.getDefaultTestTaskType().getTestTasks());
			
			launchParameterValues.setPostJob(new ImportGradleJunitResultsJob("Import gradle junit results",projectName,true));
			launchParameterValues.setOverriddenTasks(tasksToExecute);
		} catch (CoreException e) {
			EclipseUtil.log(e);
		}
	}
	
}
