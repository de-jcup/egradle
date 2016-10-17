package de.jcup.egradle.eclipse.junit.contribution.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import static de.jcup.egradle.eclipse.junit.contribution.preferences.EGradleJUnitPreferences.*;
/**
 * Class used to initialize default preference values.
 */
public class EGradleJunitPreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = JUNIT_PREFERENCES.getPreferenceStore();
		store.setDefault(EGradleJunitPreferenceConstants.P_TEST_TASKS.getId(), EGradleJUnitTestTasksType.CLEAN_ALL.getId());
	}

}
