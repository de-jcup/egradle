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

import static de.jcup.egradle.eclipse.junit.contribution.preferences.EGradleJUnitPreferences.*;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.jcup.egradle.junit.EGradleJUnitTestTasksType;
/**
 * Class used to initialize default preference values.
 */
public class EGradleJunitPreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = JUNIT_PREFERENCES.getPreferenceStore();
		store.setDefault(EGradleJunitPreferenceConstants.P_TEST_TASKS.getId(), EGradleJUnitTestTasksType.CLEAN_ONLY_TESTS.getId());
	}

}
