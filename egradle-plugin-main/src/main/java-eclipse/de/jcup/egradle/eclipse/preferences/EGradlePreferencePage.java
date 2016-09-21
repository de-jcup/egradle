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

import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.*;
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
import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.PreferenceConstants.*;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class EGradlePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public EGradlePreferencePage() {
		super(GRID);
		setPreferenceStore(PREFERENCES.getPreferenceStore());
		setDescription("Preferences for EGradle");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		DirectoryFieldEditor rootPathDirectoryEditor = new DirectoryFieldEditor(P_ROOTPROJECT_PATH.getId(), "&Gradle root project path:",
				getFieldEditorParent());
		addField(rootPathDirectoryEditor);
		rootPathDirectoryEditor.getLabelControl(getFieldEditorParent()).setToolTipText("Default root path. Can be overriden in launch configurations");
		rootPathDirectoryEditor.setEmptyStringAllowed(false);
		/* separator:*/
		Label label = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
		
		/* java home default */
		DirectoryFieldEditor defaultJavaHomeDirectoryEditor = new DirectoryFieldEditor(P_JAVA_HOME_PATH.getId(), "Default &JAVA HOME set for gradle (optional):",
				getFieldEditorParent());
		defaultJavaHomeDirectoryEditor.setEmptyStringAllowed(true);
		defaultJavaHomeDirectoryEditor.getLabelControl(getFieldEditorParent()).setToolTipText("A default global JAVA_HOME path. Can be overriden in launch configurations");
		
		addField(defaultJavaHomeDirectoryEditor);
	}

	public void init(IWorkbench workbench) {
	}

}