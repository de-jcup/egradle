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

import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.CALL_TYPE_CUSTOM;
import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.CALL_TYPE_LINUX;
import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.CALL_TYPE_STANDARD;
import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.CALL_TYPE_WINDOWS;
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
import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.PreferenceConstants.P_GRADLE_CALL_COMMAND;
import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.PreferenceConstants.P_GRADLE_CALL_TYPE;
import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.PreferenceConstants.P_GRADLE_INSTALL_PATH;
import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.PreferenceConstants.P_GRADLE_SHELL;
import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.PreferenceConstants.P_JAVA_HOME_PATH;
import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.PreferenceConstants.P_ROOTPROJECT_PATH;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.egradle.eclipse.ui.SWTFactory;

public class EGradlePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private ComboFieldEditor gradleCallTypeRadioButton;
	private StringFieldEditor shellFieldEditor;
	private StringFieldEditor gradleCommandFieldEditor;
	private DirectoryFieldEditor gradleHomeDirectoryFieldEditor;
	private Group gradleCallGroup;

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
		GridData groupLayoutData = new GridData();
		groupLayoutData.horizontalAlignment = GridData.FILL;
		groupLayoutData.verticalAlignment = GridData.BEGINNING;
		groupLayoutData.grabExcessHorizontalSpace = true;
		groupLayoutData.grabExcessVerticalSpace = false;
		groupLayoutData.verticalSpan = 2;
		groupLayoutData.horizontalSpan = 2;

		Group defaultGroup = SWTFactory.createGroup(getFieldEditorParent(), "Defaults", 1, 10, SWT.FILL);
		defaultGroup.setLayoutData(groupLayoutData);

		DirectoryFieldEditor rootPathDirectoryEditor = new DirectoryFieldEditor(P_ROOTPROJECT_PATH.getId(),
				"&Gradle root project path:", defaultGroup);
		addField(rootPathDirectoryEditor);
		String rootPathTooltipText = "Default root path. Can be overriden in launch configurations";
		rootPathDirectoryEditor.getLabelControl(defaultGroup).setToolTipText(rootPathTooltipText);
		rootPathDirectoryEditor.getTextControl(defaultGroup).setToolTipText(rootPathTooltipText);
		rootPathDirectoryEditor.setEmptyStringAllowed(false);

		/* java home default */
		DirectoryFieldEditor defaultJavaHomeDirectoryEditor = new DirectoryFieldEditor(P_JAVA_HOME_PATH.getId(),
				"Default &JAVA HOME set for gradle (optional):", defaultGroup);
		defaultJavaHomeDirectoryEditor.setEmptyStringAllowed(true);
		String defaultJavaHomeDirectoryTooltipText = "A default global JAVA_HOME path. Can be overriden in launch configurations";
		defaultJavaHomeDirectoryEditor.getLabelControl(defaultGroup)
				.setToolTipText(defaultJavaHomeDirectoryTooltipText);
		defaultJavaHomeDirectoryEditor.getTextControl(defaultGroup).setToolTipText(defaultJavaHomeDirectoryTooltipText);
		addField(defaultJavaHomeDirectoryEditor);

		/* gradle call group */
		groupLayoutData = new GridData();
		groupLayoutData.horizontalAlignment = GridData.FILL;
		groupLayoutData.verticalAlignment = GridData.BEGINNING;
		groupLayoutData.grabExcessHorizontalSpace = true;
		groupLayoutData.grabExcessVerticalSpace = true;
		groupLayoutData.verticalSpan = 2;
		groupLayoutData.horizontalSpan = 2;
		gradleCallGroup = SWTFactory.createGroup(getFieldEditorParent(), "Gradle call", 1, 10, SWT.FILL);
		gradleCallGroup.setLayoutData(groupLayoutData);
		String[][] entryNamesAndValues = new String[][] { new String[] { "Standard", CALL_TYPE_STANDARD },
				new String[] { "Windows", CALL_TYPE_WINDOWS }, new String[] { "Linux", CALL_TYPE_LINUX },
				new String[] { "Custom", CALL_TYPE_CUSTOM } };
		gradleCallTypeRadioButton = new ComboFieldEditor(P_GRADLE_CALL_TYPE.getId(), "Call type", entryNamesAndValues,
				gradleCallGroup) {
			// setPropertyChangeListener does not work on combo field editor
			@Override
			protected void fireValueChanged(String property, Object oldValue, Object newValue) {

				super.fireValueChanged(property, oldValue, newValue);
				// updateCallTypeFields(newValue);
			}
		};

		addField(gradleCallTypeRadioButton);
		shellFieldEditor = new StringFieldEditor(P_GRADLE_SHELL.getId(), "Shell", gradleCallGroup);
		addField(shellFieldEditor);
		gradleCommandFieldEditor = new StringFieldEditor(P_GRADLE_CALL_COMMAND.getId(), "Gradle call", gradleCallGroup);
		addField(gradleCommandFieldEditor);
		gradleHomeDirectoryFieldEditor = new DirectoryFieldEditor(P_GRADLE_INSTALL_PATH.getId(), "Gradle home path:",
				gradleCallGroup);
		addField(gradleHomeDirectoryFieldEditor);

	}

	@Override
	protected void initialize() {
		super.initialize();
		updateCallGroupPartsEnabledState(gradleCallTypeRadioButton.getPreferenceStore().getString(P_GRADLE_CALL_TYPE.getId()));
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		if (gradleCallTypeRadioButton==event.getSource()){
			updateCallTypeFields(event.getNewValue());
		}
	}

	private void updateCallTypeFields(Object value) {
		if (!(value instanceof String)) {
			return;
		}
		String strValue = updateCallGroupPartsEnabledState(value);
		switch (strValue) {
		case CALL_TYPE_STANDARD:
			shellFieldEditor.setStringValue(EGradlePreferences.DEFAULT_GRADLE_SHELL);
			gradleHomeDirectoryFieldEditor.setStringValue(EGradlePreferences.DEFAULT_GRADLE_HOME_PATH);
			gradleCommandFieldEditor.setStringValue(EGradlePreferences.DEFAULT_GRADLE_CALL_COMMAND);
			break;
		case CALL_TYPE_LINUX:
			shellFieldEditor.setStringValue("");
			gradleHomeDirectoryFieldEditor.setStringValue(EGradlePreferences.DEFAULT_GRADLE_HOME_PATH);
			gradleCommandFieldEditor.setStringValue(EGradlePreferences.DEFAULT_GRADLE_CALL_COMMAND);
			break;
		case CALL_TYPE_WINDOWS:
			shellFieldEditor.setStringValue("");
			gradleHomeDirectoryFieldEditor.setStringValue(EGradlePreferences.DEFAULT_GRADLE_HOME_PATH);
			gradleCommandFieldEditor.setStringValue(EGradlePreferences.DEFAULT_GRADLE_CALL_COMMAND + ".bat");
			break;
		}
	}

	private String updateCallGroupPartsEnabledState(Object value) {
		String strValue = (String) value;
		if (CALL_TYPE_CUSTOM.equals(strValue)) {
			/* set all editable */
			setCallGroupPartsEnabled(true);
		} else {
			setCallGroupPartsEnabled(false);
		}
		return strValue;
	}

	private void setCallGroupPartsEnabled(boolean enabled) {
		shellFieldEditor.setEnabled(enabled, gradleCallGroup);
		gradleCommandFieldEditor.setEnabled(enabled, gradleCallGroup);
		gradleHomeDirectoryFieldEditor.setEnabled(enabled, gradleCallGroup);
	}

	public void init(IWorkbench workbench) {

	}

}