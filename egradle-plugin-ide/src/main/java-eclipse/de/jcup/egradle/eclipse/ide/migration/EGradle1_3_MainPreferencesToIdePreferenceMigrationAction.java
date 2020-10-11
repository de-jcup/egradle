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
package de.jcup.egradle.eclipse.ide.migration;

import static de.jcup.egradle.eclipse.ide.preferences.EGradleIdePreferenceConstants.*;
import static org.apache.commons.lang3.StringUtils.*;

import org.eclipse.jface.preference.IPreferenceStore;

import de.jcup.egradle.core.migration.MigrationAction;
import de.jcup.egradle.core.process.EGradleShellType;
import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.ide.preferences.EGradleIdePreferences;
import de.jcup.egradle.eclipse.migration.EGradleOldMainPreferenceProvider;

public class EGradle1_3_MainPreferencesToIdePreferenceMigrationAction implements MigrationAction {

    private EGradleOldMainPreferenceProvider provider;

    public EGradle1_3_MainPreferencesToIdePreferenceMigrationAction(EGradleOldMainPreferenceProvider provider) {
        this.provider = provider;
    }

    @Override
    public void executeMigration() {
        EGradleIdePreferences idePreferences = IDEUtil.getPreferences();
        String oldRootProjectPath = provider.getRootProjectPath();
        String globalJavaHomePath = provider.getGlobalJavaHomePath();
        String gradleBinInstallFolder = provider.getGradleBinInstallFolder();
        String gradleShellId = provider.getGradleShellId();
        String gradleCallCommand = provider.getGradleCallCommand();
        String gradleCallTypeID = provider.getGradleCallTypeID();

        if (isNotBlank(gradleCallTypeID)) {
            idePreferences.setGradleCallTypeID(gradleCallTypeID);
        }
        if (isNotBlank(oldRootProjectPath)) {
            idePreferences.setRootProjectPath(oldRootProjectPath);
        }
        if (isNotBlank(globalJavaHomePath)) {
            idePreferences.setGlobalJavaHomePath(globalJavaHomePath);
        }
        if (isNotBlank(gradleBinInstallFolder)) {
            idePreferences.setGradleBinInstallFolder(gradleBinInstallFolder);
        }
        if (isNotBlank(gradleCallCommand)) {
            idePreferences.setGradleCallCommand(gradleCallCommand);
        }
        if (isNotBlank(gradleShellId)) {
            idePreferences.setGradleShellType(EGradleShellType.findById(gradleShellId));
        }

        IPreferenceStore preferenceStore = idePreferences.getPreferenceStore();
        if (provider.hasValueForExecuteAssembleTaskOnImportEnabled()) {
            preferenceStore.setValue(P_IMPORT__EXECUTE_ASSEMBLE_TASK.getId(), provider.isExecuteAssembleTaskOnImportEnabled());
        }
        if (provider.hasValueForCleanProjectsOnImportEnabled()) {
            preferenceStore.setValue(P_IMPORT__DO_CLEAN_PROJECTS.getId(), provider.isCleanProjectsOnImportEnabled());
        }
        if (provider.hasValueForAutomaticallyDeriveBuildFoldersEnabled()) {
            preferenceStore.setValue(P_FILEHANDLING_AUTOMATICALLY_DERIVE_BUILDFOLDERS.getId(), provider.isAutomaticallyDeriveBuildFoldersEnabled());
        }
        if (provider.hasValueForOutputValidationEnabled()) {
            preferenceStore.setValue(P_OUTPUT_VALIDATION_ENABLED.getId(), true);// there
                                                                                // was
                                                                                // a
                                                                                // bug
                                                                                // -
                                                                                // do
                                                                                // always
                                                                                // set
                                                                                // it!
        }
        if (provider.hasValueForShowingConsoleOnBuildFailed()) {
            preferenceStore.setValue(P_SHOW_CONSOLE_VIEW_ON_BUILD_FAILED_ENABLED.getId(), provider.isShowingConsoleOnBuildFailed());
        }
        if (provider.hasValueForSubProjectIconDecorationEnabled()) {
            preferenceStore.setValue(P_DECORATION_SUBPROJECTS_WITH_ICON_ENABLED.getId(), true); // there
                                                                                                // was
                                                                                                // a
                                                                                                // bug
                                                                                                // -
                                                                                                // do
                                                                                                // always
                                                                                                // set
                                                                                                // it!
        }

    }

}
