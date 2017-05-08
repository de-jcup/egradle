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
package de.jcup.egradle.eclipse.migration;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.jcup.egradle.eclipse.MainActivator;

/**
 * This class is only a workaround to migrate old settings from main plugin to
 * IDE plugin! Should be removed when migrations no longer needed (means multiple 2.x deployments happend so its clear
 * the main plugin parts are no longer needed)
 * 
 * @author Albert Tregnaghi
 *
 */
public class EGradleOldMainPreferenceProvider {
	private enum OldConstants {
		P_ROOTPROJECT_PATH("pathGradleRootProject"),

		P_JAVA_HOME_PATH("pathJavaHome"),

		P_GRADLE_CALL_TYPE("gradleCallType"), P_GRADLE_SHELL("commandShell"), P_GRADLE_INSTALL_BIN_FOLDER(
				"pathGradleInstallation"), P_GRADLE_CALL_COMMAND("commandGradle"), P_OUTPUT_VALIDATION_ENABLED(
						"validatEnabled"), P_SHOW_CONSOLE_VIEW_ON_BUILD_FAILED_ENABLED("showConsoleViewOnBuildfailed"),

		/* file handling parts */
		P_FILEHANDLING_AUTOMATICALLY_DERIVE_BUILDFOLDERS("automaticallyDeriveBuildFolders"),

		/* import */
		P_IMPORT__EXECUTE_ASSEMBLE_TASK("onImportExecuteAssembleTask"), P_IMPORT__DO_CLEAN_PROJECTS(
				"onImportDoCleanProjects"),

		P_DECORATION_SUBPROJECTS_WITH_ICON_ENABLED("validatEnabled");

		private String id;

		private OldConstants(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}
	}

	private static EGradleOldMainPreferenceProvider INSTANCE = new EGradleOldMainPreferenceProvider();

	private IPreferenceStore store;

	public static EGradleOldMainPreferenceProvider getInstance() {
		return INSTANCE;
	}

	public EGradleOldMainPreferenceProvider() {
		store = new ScopedPreferenceStore(InstanceScope.INSTANCE, MainActivator.PLUGIN_ID);
	}

	private String getStringPreference(OldConstants id) {
		String data = getPreferenceStore().getString(id.getId());
		if (data == null) {
			data = "";
		}
		return data;
	}

	public IPreferenceStore getPreferenceStore() {
		return store;
	}

	public boolean isShowingConsoleOnBuildFailed() {
		boolean showConsoleOnBuildFailed = getPreferenceStore()
				.getBoolean(OldConstants.P_SHOW_CONSOLE_VIEW_ON_BUILD_FAILED_ENABLED.getId());
		return showConsoleOnBuildFailed;
	}

	public boolean isOutputValidationEnabled() {
		boolean validationEnabled = getPreferenceStore().getBoolean(OldConstants.P_OUTPUT_VALIDATION_ENABLED.getId());
		return validationEnabled;
	}

	public boolean isCleanProjectsOnImportEnabled() {
		boolean cleanProjectsEnabled = getPreferenceStore()
				.getBoolean(OldConstants.P_IMPORT__DO_CLEAN_PROJECTS.getId());
		return cleanProjectsEnabled;
	}

	public boolean isExecuteAssembleTaskOnImportEnabled() {
		boolean executeAssembleTaskEnabled = getPreferenceStore()
				.getBoolean(OldConstants.P_IMPORT__EXECUTE_ASSEMBLE_TASK.getId());
		return executeAssembleTaskEnabled;
	}

	public boolean isAutomaticallyDeriveBuildFoldersEnabled() {
		boolean automaticallyDeriveBuildFoldersEnabled = getPreferenceStore()
				.getBoolean(OldConstants.P_FILEHANDLING_AUTOMATICALLY_DERIVE_BUILDFOLDERS.getId());
		return automaticallyDeriveBuildFoldersEnabled;
	}

	public boolean isSubProjectIconDecorationEnabled() {
		boolean validationEnabled = getPreferenceStore()
				.getBoolean(OldConstants.P_DECORATION_SUBPROJECTS_WITH_ICON_ENABLED.getId());
		return validationEnabled;
	}

	public String getGlobalJavaHomePath() {
		return getStringPreference(OldConstants.P_JAVA_HOME_PATH);
	}

	public String getGradleCallCommand() {
		return getStringPreference(OldConstants.P_GRADLE_CALL_COMMAND);
	}

	public String getGradleBinInstallFolder() {
		return getStringPreference(OldConstants.P_GRADLE_INSTALL_BIN_FOLDER);
	}

	public String getGradleShellId() {
		return getStringPreference(OldConstants.P_GRADLE_SHELL);
	}

	public String getGradleCallTypeID() {
		return getStringPreference(OldConstants.P_GRADLE_CALL_TYPE);
	}

	public String getRootProjectPath() {
		return getStringPreference(OldConstants.P_ROOTPROJECT_PATH);
	}

}
