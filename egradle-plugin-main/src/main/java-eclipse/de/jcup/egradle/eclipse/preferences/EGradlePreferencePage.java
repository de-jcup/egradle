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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.progress.UIJob;

import de.jcup.egradle.core.api.GradleConfigurationValidator;
import de.jcup.egradle.core.api.ValidationException;
import de.jcup.egradle.core.config.MutableGradleConfiguration;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.ui.SWTFactory;

public class EGradlePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private ComboFieldEditor gradleCallTypeRadioButton;
	private StringFieldEditor shellFieldEditor;
	private StringFieldEditor gradleCommandFieldEditor;
	private DirectoryFieldEditor gradleHomeDirectoryFieldEditor;
	private Group gradleCallGroup;
	private Text validationOutputField;
	private DirectoryFieldEditor rootPathDirectoryEditor;
	private DirectoryFieldEditor defaultJavaHomeDirectoryEditor;
	private Button validationButton;

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
		groupLayoutData.horizontalSpan = 3;

		Group defaultGroup = SWTFactory.createGroup(getFieldEditorParent(), "Defaults", 1, 10, SWT.FILL);
		defaultGroup.setLayoutData(groupLayoutData);

		rootPathDirectoryEditor = new DirectoryFieldEditor(P_ROOTPROJECT_PATH.getId(), "&Gradle root project path:",
				defaultGroup);
		addField(rootPathDirectoryEditor);
		String rootPathTooltipText = "Default root path. Can be overriden in launch configurations";
		rootPathDirectoryEditor.getLabelControl(defaultGroup).setToolTipText(rootPathTooltipText);
		rootPathDirectoryEditor.getTextControl(defaultGroup).setToolTipText(rootPathTooltipText);
		rootPathDirectoryEditor.setEmptyStringAllowed(false);

		/* java home default */
		defaultJavaHomeDirectoryEditor = new DirectoryFieldEditor(P_JAVA_HOME_PATH.getId(),
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
		groupLayoutData.grabExcessVerticalSpace = false;
		groupLayoutData.verticalSpan = 3;
		groupLayoutData.horizontalSpan = 3;
		gradleCallGroup = SWTFactory.createGroup(getFieldEditorParent(), "Gradle call", 1, 10, SWT.FILL);
		gradleCallGroup.setLayoutData(groupLayoutData);
		String[][] entryNamesAndValues = new String[][] { new String[] { "Standard", CALL_TYPE_STANDARD },
				new String[] { "Windows", CALL_TYPE_WINDOWS }, new String[] { "Linux", CALL_TYPE_LINUX },
				new String[] { "Custom", CALL_TYPE_CUSTOM } };
		gradleCallTypeRadioButton = new ComboFieldEditor(P_GRADLE_CALL_TYPE.getId(), "Call type", entryNamesAndValues,
				gradleCallGroup);

		addField(gradleCallTypeRadioButton);
		shellFieldEditor = new StringFieldEditor(P_GRADLE_SHELL.getId(), "Shell", gradleCallGroup);
		addField(shellFieldEditor);
		gradleCommandFieldEditor = new StringFieldEditor(P_GRADLE_CALL_COMMAND.getId(), "Gradle call", gradleCallGroup);
		addField(gradleCommandFieldEditor);
		gradleHomeDirectoryFieldEditor = new DirectoryFieldEditor(P_GRADLE_INSTALL_PATH.getId(), "Gradle bin folder:",
				gradleCallGroup);
		addField(gradleHomeDirectoryFieldEditor);

		/* ------------------------------------ */
		/* - Check output - */
		/* ------------------------------------ */
		groupLayoutData = new GridData();
		groupLayoutData.horizontalAlignment = GridData.FILL;
		groupLayoutData.verticalAlignment = GridData.BEGINNING;
		groupLayoutData.grabExcessHorizontalSpace = true;
		groupLayoutData.grabExcessVerticalSpace = true;
		groupLayoutData.verticalSpan = 2;
		groupLayoutData.horizontalSpan = 3;

		Group validationGroup = SWTFactory.createGroup(getFieldEditorParent(),
				"Manual validation if gradle is executable", 1, 10, SWT.FILL);
		validationGroup.setLayoutData(groupLayoutData);

		GridData labelGridData = new GridData();
		labelGridData.horizontalAlignment = GridData.FILL;
		labelGridData.verticalAlignment = GridData.BEGINNING;
		labelGridData.grabExcessHorizontalSpace = false;
		labelGridData.grabExcessVerticalSpace = false;

		GridData gridDataLastColumn = new GridData();
		gridDataLastColumn.horizontalAlignment = GridData.FILL;
		gridDataLastColumn.verticalAlignment = GridData.FILL;
		gridDataLastColumn.grabExcessHorizontalSpace = true;
		gridDataLastColumn.grabExcessVerticalSpace = false;
		gridDataLastColumn.verticalSpan = 2;
		gridDataLastColumn.horizontalSpan = 2;
		gridDataLastColumn.minimumHeight = 50;
		gridDataLastColumn.heightHint = 100;

		// Label validationLabel = new Label(validationGroup, SWT.NULL);
		// validationLabel.setText("Gradle execution validation output:");
		// validationLabel.setLayoutData(labelGridData);

		validationOutputField = new Text(validationGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.READ_ONLY);
		validationOutputField.setLayoutData(gridDataLastColumn);

		validationButton = new Button(validationGroup, SWT.NONE);
		validationButton.setText("Validate gradle execution");
		validationButton.setImage(EGradleUtil.getImage("icons/gradle-og.gif"));

		validationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (validationRunning == true) {
					return;
				}
				setValid(false);
				validationRunning = true;
				validationOutputField.setText("Start validation...\n");
				new ValidationJob().schedule(500);
			}
		});
	}

	@Override
	protected void updateApplyButton() {
		super.updateApplyButton();
	}


	private boolean validationRunning = false;

	private class ValidationJob extends UIJob {
		private GradleConfigurationValidator validator;
		private MutableGradleConfiguration configuration;
		private OutputHandler outputHandler;

		public ValidationJob() {
			super("Preferene validation");

			outputHandler = new OutputHandler() {

				@Override
				public void output(String line) {
					Display.getCurrent().asyncExec(new Runnable() {

						@Override
						public void run() {
							validationOutputField.append(line + "\n");
						}
					});
				}

			};

			validator = new GradleConfigurationValidator(new SimpleProcessExecutor(outputHandler, true));
			validator.setOutputHandler(outputHandler);
			configuration = new MutableGradleConfiguration();
			configuration.setGradleCommand(gradleCommandFieldEditor.getStringValue());
			configuration.setGradleBinDirectory(gradleHomeDirectoryFieldEditor.getStringValue());
			configuration.setShellCommand(shellFieldEditor.getStringValue());
			configuration.setWorkingDirectory(rootPathDirectoryEditor.getStringValue());
			configuration.setJavaHome(defaultJavaHomeDirectoryEditor.getStringValue());
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			/* check more... */
			try {
				validator.validate(configuration);
				checkState(); // we say its valid - but the others...
				outputHandler.output("\n\n\n\nOK - your gradle settings are correct and working.");
				return Status.OK_STATUS;
			} catch (ValidationException e) {
				setValid(false);
				StringBuilder sb = new StringBuilder();
				sb.append(e.getMessage());
				String details = e.getDetails();
				if (details != null) {
					sb.append("\n");
					sb.append(details);
				}
				outputHandler.output("\n\n\n\nFAILED - " + sb.toString());
				return Status.CANCEL_STATUS;
			} finally {
				Display.getCurrent().asyncExec(new Runnable() {

					@Override
					public void run() {
						validationRunning = false;
						updateApplyButton();
					}
				});
			}
		}

	}

	@Override
	protected void initialize() {
		super.initialize();
		updateCallGroupPartsEnabledState(
				gradleCallTypeRadioButton.getPreferenceStore().getString(P_GRADLE_CALL_TYPE.getId()));
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		if (gradleCallTypeRadioButton == event.getSource()) {
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
			gradleCommandFieldEditor.setStringValue("gradle");
			break;
		case CALL_TYPE_WINDOWS:
			shellFieldEditor.setStringValue("");
			gradleHomeDirectoryFieldEditor.setStringValue(EGradlePreferences.DEFAULT_GRADLE_HOME_PATH);
			gradleCommandFieldEditor.setStringValue("gradle.bat");
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