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

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.jcup.egradle.core.migration.MigrationState;
import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.preferences.EGradleCallType;

/**
 * Class used to initialize default preference values.
 */
public class EGradleIdePreferenceInitializer extends AbstractPreferenceInitializer {

    public void initializeDefaultPreferences() {
        IPreferenceStore store = IDEUtil.getPreferences().getPreferenceStore();
        EGradleCallType defaultCallType = calculateDefaultCallType();
        store.setDefault(P_OUTPUT_VALIDATION_ENABLED.getId(), true);

        store.setDefault(P_FILEHANDLING_AUTOMATICALLY_DERIVE_BUILDFOLDERS.getId(), false);

        store.setDefault(P_IMPORT__EXECUTE_ASSEMBLE_TASK.getId(), true);
        store.setDefault(P_IMPORT__DO_CLEAN_PROJECTS.getId(), true);

        store.setDefault(P_SHOW_CONSOLE_VIEW_ON_BUILD_FAILED_ENABLED.getId(), true);
        store.setDefault(P_DECORATION_SUBPROJECTS_WITH_ICON_ENABLED.getId(), true);

        store.setDefault(P_GRADLE_CALL_TYPE.getId(), defaultCallType.getId());
        store.setDefault(P_GRADLE_SHELL.getId(), defaultCallType.getDefaultShell().getId());
        store.setDefault(P_GRADLE_INSTALL_BIN_FOLDER.getId(), defaultCallType.getDefaultGradleBinFolder());
        store.setDefault(P_GRADLE_CALL_COMMAND.getId(), defaultCallType.getDefaultGradleCommand());

        store.setDefault(P_MIGRATE_IDE_STATE.getId(), MigrationState.NOT_MIGRATED.name());

        store.setDefault(P_ALWAYS_ADD_VIRTUAL_ROOT_TO_ALL_WORKINGSETS.getId(), true);
    }

    /**
     * Calculate default GRADLE call type depending on OS
     * 
     * @return default type
     */
    public static EGradleCallType calculateDefaultCallType() {
        EGradleCallType defaultCallType = null;
        if (SystemUtils.IS_OS_WINDOWS) {
            defaultCallType = EGradleCallType.WINDOWS_GRADLE_WRAPPER;
        } else {
            defaultCallType = EGradleCallType.LINUX_GRADLE_WRAPPER;
        }
        return defaultCallType;
    }

    /**
     * Calculate GRADLE call type (installed variant) depending on OS
     * 
     * @return type
     */
    public static EGradleCallType calculateOSInstalledType() {
        EGradleCallType installedType = null;
        if (SystemUtils.IS_OS_WINDOWS) {
            installedType = EGradleCallType.WINDOWS_GRADLE_INSTALLED;
        } else {
            installedType = EGradleCallType.LINUX_GRADLE_INSTALLED;
        }
        return installedType;
    }

}
