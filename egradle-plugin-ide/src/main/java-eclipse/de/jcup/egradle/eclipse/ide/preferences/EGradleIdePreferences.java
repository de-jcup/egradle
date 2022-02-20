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
package de.jcup.egradle.eclipse.ide.preferences;

import static de.jcup.egradle.eclipse.ide.preferences.EGradleIdePreferenceConstants.*;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.jcup.egradle.core.migration.MigrationState;
import de.jcup.egradle.core.process.EGradleShellType;
import de.jcup.egradle.eclipse.ide.IDEActivator;

public class EGradleIdePreferences {

    private static EGradleIdePreferences INSTANCE = new EGradleIdePreferences();

    private IPreferenceStore store;

    public static EGradleIdePreferences getInstance() {
        return INSTANCE;
    }

    EGradleIdePreferences() {
        store = new ScopedPreferenceStore(InstanceScope.INSTANCE, IDEActivator.PLUGIN_ID);
    }

    public String getStringPreference(EGradleIdePreferenceConstants id) {
        String data = getPreferenceStore().getString(id.getId());
        if (data == null) {
            data = "";
        }
        return data;
    }

    public IPreferenceStore getPreferenceStore() {
        return store;
    }

    public boolean isEGradleRootAlwaysAddedToWorkingSets() {
        boolean showConsoleOnBuildFailed = getPreferenceStore().getBoolean(P_ALWAYS_ADD_VIRTUAL_ROOT_TO_ALL_WORKINGSETS.getId());
        return showConsoleOnBuildFailed;
    }

    public boolean isShowingConsoleOnBuildFailed() {
        boolean showConsoleOnBuildFailed = getPreferenceStore().getBoolean(P_SHOW_CONSOLE_VIEW_ON_BUILD_FAILED_ENABLED.getId());
        return showConsoleOnBuildFailed;
    }

    public boolean isOutputValidationEnabled() {
        boolean validationEnabled = getPreferenceStore().getBoolean(P_OUTPUT_VALIDATION_ENABLED.getId());
        return validationEnabled;
    }

    public boolean isCleanProjectsOnImportEnabled() {
        boolean cleanProjectsEnabled = getPreferenceStore().getBoolean(P_IMPORT__DO_CLEAN_PROJECTS.getId());
        return cleanProjectsEnabled;
    }

    public boolean isExecuteAssembleTaskOnImportEnabled() {
        boolean executeAssembleTaskEnabled = getPreferenceStore().getBoolean(P_IMPORT__EXECUTE_ASSEMBLE_TASK.getId());
        return executeAssembleTaskEnabled;
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

    public String getGradleUserHome() {
        return getStringPreference(P_GRADLE_USER_HOME);
    }

    public MigrationState getMigrationState() {
        String migrationStateAsString = getStringPreference(P_MIGRATE_IDE_STATE);
        MigrationState migrationState = MigrationState.fromName(migrationStateAsString);
        return migrationState;
    }

    public void setMigrationState(MigrationState migrationState) {
        if (migrationState == null) {
            migrationState = MigrationState.NOT_MIGRATED;
        }
        getPreferenceStore().setValue(P_MIGRATE_IDE_STATE.getId(), migrationState.name());
    }

    public String getGradleCallTypeID() {
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
        if (shell == null) {
            shell = EGradleShellType.NONE;
        }
        getPreferenceStore().setValue(P_GRADLE_SHELL.getId(), shell.getId());
    }

    public void setGradleCallTypeID(String callTypeId) {
        getPreferenceStore().setValue(P_GRADLE_CALL_TYPE.getId(), callTypeId);
    }

}
