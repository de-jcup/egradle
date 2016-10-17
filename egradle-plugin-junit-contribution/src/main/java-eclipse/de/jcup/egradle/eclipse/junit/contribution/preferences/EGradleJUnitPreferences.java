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
package de.jcup.egradle.eclipse.junit.contribution.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.jcup.egradle.eclipse.Activator;
import de.jcup.egradle.junit.EGradleJUnitTestTasksType;

public class EGradleJUnitPreferences {
	

	public static EGradleJUnitPreferences JUNIT_PREFERENCES = new EGradleJUnitPreferences();
	private IPreferenceStore store;

	EGradleJUnitPreferences() {
		store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);
	}

	public String getStringPreference(EGradleJunitPreferenceConstants id) {
		String data = getPreferenceStore().getString(id.getId());
		if (data==null){
			data="";
		}
		return data;
	}
	
	/**
	 * Returns default test task type configured in preferences
	 * 
	 * @return default test task type
	 */
	public EGradleJUnitTestTasksType getDefaultTestTaskType() {
		String configuredTestTaskTypeId = getStringPreference(EGradleJunitPreferenceConstants.P_TEST_TASKS);
		EGradleJUnitTestTasksType testTasksType = EGradleJUnitTestTasksType.findById(configuredTestTaskTypeId);
		if (testTasksType == null) {
			/* fall back */
			testTasksType = EGradleJUnitTestTasksType.CLEAN_ALL;
		}
		return testTasksType;
	}

	public IPreferenceStore getPreferenceStore() {
		return store;
	}
}
