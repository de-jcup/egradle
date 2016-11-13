package de.jcup.egradle.eclipse.gradleeditor.preferences;
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


import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.jcup.egradle.eclipse.Activator;

public class EGradleEditorPreferences {
	

	public static EGradleEditorPreferences EDITOR_PREFERENCES = new EGradleEditorPreferences();
	private IPreferenceStore store;

	EGradleEditorPreferences() {
		/* TODO ATR, 12.11.2016: Activator.PLUGIN_ID comes fom main plugin. junit and editor reuse this
		 * identifier  so all preferences are stored inside same file. This has to be reconsidered in future
		 */
		store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);
	}

	public String getStringPreference(EGradleEditorPreferenceConstants id) {
		String data = getPreferenceStore().getString(id.getId());
		if (data==null){
			data="";
		}
		return data;
	}
	
	public boolean getBooleanPreference(EGradleEditorPreferenceConstants id) {
		boolean data = getPreferenceStore().getBoolean(id.getId());
		return data;
	}
	
	public void setBooleanPreference(EGradleEditorPreferenceConstants id, boolean value){
		getPreferenceStore().setValue(id.getId(),value);
	}
	
	public IPreferenceStore getPreferenceStore() {
		return store;
	}
}
