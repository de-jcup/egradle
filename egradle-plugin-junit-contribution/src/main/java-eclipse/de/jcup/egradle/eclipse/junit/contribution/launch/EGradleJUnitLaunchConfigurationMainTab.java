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

import static de.jcup.egradle.eclipse.launch.EGradleLauncherConstants.*;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.junit.contribution.Activator;
import de.jcup.egradle.eclipse.launch.EGradleLaunchConfigurationMainTab;

public class EGradleJUnitLaunchConfigurationMainTab extends EGradleLaunchConfigurationMainTab {

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		super.setDefaults(configuration);
		configuration.setAttribute(PROPERTY_TASKS, EGradleJUnitLaunchShortCut.getTestTasks());
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		/* make tasks not editable, but focusable. So we do NOT use tasksField.setEnabled(false)! */
		tasksField.setEditable(false);
		tasksField.setBackground(parent.getShell().getBackground());
		
	}

	@Override
	public Image getImage() {
		return EGradleUtil.getImage("icons/gradle-og-junit.gif",Activator.PLUGIN_ID);
	}

	@Override
	public String getName() {
		return "Gradle JUnit";
	}

}
