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

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.jcup.egradle.eclipse.Activator;

public class EGradlePreferences {

	public static final String CALL_TYPE_STANDARD = "standard";
	public static final String CALL_TYPE_LINUX = "linux";
	public static final String CALL_TYPE_WINDOWS = "windows";
	public static final String CALL_TYPE_CUSTOM = "custom";
	
	public static final String DEFAULT_GRADLE_SHELL = "bash"; // bash does work on windows/linux and mac... (git bash..)
	public static final String DEFAULT_GRADLE_HOME_PATH = "";
	public static final String DEFAULT_GRADLE_CALL_COMMAND = "gradlew"; // wrapper usage is the default
	
	public static enum PreferenceConstants {
		P_ROOTPROJECT_PATH("pathGradleRootProject"), 
		
		P_JAVA_HOME_PATH("pathJavaHome"),
		
		P_GRADLE_CALL_TYPE("gradleCallType"),
		P_GRADLE_SHELL("commandShell"),
		P_GRADLE_INSTALL_PATH("pathGradleInstallation"),
		P_GRADLE_CALL_COMMAND("commandGradle");

		private String id;

		private PreferenceConstants(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

	}

	public static EGradlePreferences PREFERENCES = new EGradlePreferences();
	private IPreferenceStore store;

	EGradlePreferences() {
		store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);
	}

	public String getStringPreference(PreferenceConstants id) {
		String data = getPreferenceStore().getString(id.getId());
		if (data==null){
			data="";
		}
		return data;
	}

	public IPreferenceStore getPreferenceStore() {
		return store;
	}
}
