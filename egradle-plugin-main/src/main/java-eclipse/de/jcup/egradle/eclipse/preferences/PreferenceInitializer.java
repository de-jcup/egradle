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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		/* currently we got no defaults */
		IPreferenceStore store = EGradlePreferences.PREFERENCES.getPreferenceStore();
		store.setDefault(EGradlePreferences.PreferenceConstants.P_GRADLE_CALL_TYPE.getId(), EGradlePreferences.CALL_TYPE_STANDARD);
		store.setDefault(EGradlePreferences.PreferenceConstants.P_GRADLE_SHELL.getId(), EGradlePreferences.DEFAULT_GRADLE_SHELL);
		store.setDefault(EGradlePreferences.PreferenceConstants.P_GRADLE_INSTALL_PATH.getId(), EGradlePreferences.DEFAULT_GRADLE_HOME_PATH);
		store.setDefault(EGradlePreferences.PreferenceConstants.P_GRADLE_CALL_COMMAND.getId(), EGradlePreferences.DEFAULT_GRADLE_CALL_COMMAND);
	}

}
