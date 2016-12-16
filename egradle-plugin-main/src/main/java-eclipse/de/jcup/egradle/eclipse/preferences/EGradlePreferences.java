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
package de.jcup.egradle.eclipse.preferences;

import static de.jcup.egradle.eclipse.preferences.EGradlePreferenceConstants.*;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.jcup.egradle.core.process.EGradleShellType;
import de.jcup.egradle.eclipse.Activator;

public class EGradlePreferences {
	
	public static EGradlePreferences EGRADLE_IDE_PREFERENCES = new EGradlePreferences();
	
	private IPreferenceStore store;

	EGradlePreferences() {
		store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);
	}

	public String getStringPreference(EGradlePreferenceConstants id) {
		String data = getPreferenceStore().getString(id.getId());
		if (data==null){
			data="";
		}
		return data;
	}
	
	public IPreferenceStore getPreferenceStore() {
		return store;
	}

	public boolean isOutputValidationEnabled() {
		boolean validationEnabled = getPreferenceStore().getBoolean(P_OUTPUT_VALIDATION_ENABLED.getId());
		return validationEnabled;
	}
	
	public boolean isAutomaticallyDeriveBuildFoldersEnabled() {
		boolean automaticallyDeriveBuildFoldersEnabled = getPreferenceStore().getBoolean(P_FILEHANDLING_AUTOMATICALLY_DERIVE_BUILDFOLDERS.getId());
		return automaticallyDeriveBuildFoldersEnabled;
	}
	
	public boolean isSubProjectIconDecorationEnabled() {
		boolean validationEnabled = getPreferenceStore().getBoolean(P_DECORATION_SUBPROJECTS_WITH_ICON_ENABLED.getId());
		return validationEnabled;
	}

	public String getGlobalJavaHomePath() {
		return getStringPreference(P_JAVA_HOME_PATH);
	}

	public String getGradleCallCommand() {
		return getStringPreference(P_GRADLE_CALL_COMMAND);
	}

	public String getGradleBinInstallFolder() {
		return getStringPreference(P_GRADLE_INSTALL_BIN_FOLDER);
	}

	public String getGradleShellId() {
		return getStringPreference(P_GRADLE_SHELL);
	}

	public String getGradleCallTypeID(){
		return getStringPreference(P_GRADLE_CALL_TYPE);
	}
	
	public String getRootProjectPath() {
		return getStringPreference(P_ROOTPROJECT_PATH);
	}

	public void setRootProjectPath(String rootPath) {
		getPreferenceStore().setValue(P_ROOTPROJECT_PATH.getId(), rootPath);
	}

	public void setGlobalJavaHomePath(String globalJavaHome) {
		getPreferenceStore().setValue(P_JAVA_HOME_PATH.getId(), globalJavaHome);
	}

	public void setGradleBinInstallFolder(String gradleInstallPath) {
		getPreferenceStore().setValue(P_GRADLE_INSTALL_BIN_FOLDER.getId(), gradleInstallPath);
	}

	public void setGradleCallCommand(String gradleCommand) {
		getPreferenceStore().setValue(P_GRADLE_CALL_COMMAND.getId(), gradleCommand);
	}

	public void setGradleShellType(EGradleShellType shell) {
		if (shell==null){
			shell=EGradleShellType.NONE;
		}
		getPreferenceStore().setValue(P_GRADLE_SHELL.getId(), shell.getId());
	}

	public void setGradleCallTypeID(String callTypeId) {
		getPreferenceStore().setValue(P_GRADLE_CALL_TYPE.getId(), callTypeId);
	}

	
	
}
