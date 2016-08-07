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