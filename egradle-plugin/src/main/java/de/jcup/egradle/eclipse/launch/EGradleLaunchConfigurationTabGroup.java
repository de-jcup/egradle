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
 package de.jcup.egradle.eclipse.launch;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTabGroup;

public class EGradleLaunchConfigurationTabGroup implements ILaunchConfigurationTabGroup {
	private ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[1];

	public EGradleLaunchConfigurationTabGroup() {
		int index=0;
//		tabs[0] = new org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab();
		tabs[index++] = new EGradleLaunchConfigurationMainTab();
//		tabs[index++] = new org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab();
//		tabs[index++] = new org.eclipse.debug.ui.CommonTab();
	}

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		dialog.setActiveTab(tabs[0]);
	}

	@Override
	public void dispose() {
	}

	@Override
	public ILaunchConfigurationTab[] getTabs() {
		return tabs;
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		for (int i=0;i<tabs.length;i++){
			tabs[i].initializeFrom(configuration);
		}
	}

	@Override
	public void launched(ILaunch launch) {
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		for (int i=0;i<tabs.length;i++){
			tabs[i].performApply(configuration);
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		for (int i=0;i<tabs.length;i++){
			tabs[i].setDefaults(configuration);
		}
	}

}