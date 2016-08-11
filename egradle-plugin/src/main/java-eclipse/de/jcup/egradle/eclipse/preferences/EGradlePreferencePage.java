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

import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.PREFERENCES;
/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */
import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.PreferenceConstants.P_JAVA_HOME_PATH;
import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.PreferenceConstants.P_ROOTPROJECT_PATH;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
public class EGradlePreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public EGradlePreferencePage() {
		super(GRID);
		setPreferenceStore(PREFERENCES.getPreferenceStore());
		setDescription("EGradle setup");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new DirectoryFieldEditor(P_ROOTPROJECT_PATH.getId(), 
				"&Gradle root project path:", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(P_JAVA_HOME_PATH.getId(), 
				"&JAVA HOME set for gradle:", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}
	
}