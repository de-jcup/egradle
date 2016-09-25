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
import static de.jcup.egradle.eclipse.preferences.PreferenceConstants.*;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import de.jcup.egradle.core.api.GradleConfigurationValidator;
import de.jcup.egradle.core.api.ValidationException;
import de.jcup.egradle.core.config.MutableGradleConfiguration;
import de.jcup.egradle.core.process.EGradleShellType;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.ui.SWTFactory;

public class EGradlePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private ComboFieldEditor gradleCallTypeRadioButton;
	private ChangeableComboFieldEditor shellFieldEditor;
	private StringFieldEditor gradleCommandFieldEditor;
	private DirectoryFieldEditor gradleInstallBinDirectoryFieldEditor;
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

		rootPathDirectoryEditor = new DirectoryFieldEditor(P_ROOTPROJECT_PATH.getId(), "&Gradle root project path",
				defaultGroup);
		addField(rootPathDirectoryEditor);
		String rootPathTooltipText = "Default root path. Can be overriden in launch configurations";
		rootPathDirectoryEditor.getLabelControl(defaultGroup).setToolTipText(rootPathTooltipText);
		rootPathDirectoryEditor.getTextControl(defaultGroup).setToolTipText(rootPathTooltipText);
		rootPathDirectoryEditor.setEmptyStringAllowed(false);

		/* java home default */
		defaultJavaHomeDirectoryEditor = new DirectoryFieldEditor(P_JAVA_HOME_PATH.getId(),
				"&JAVA HOME (optional)", defaultGroup);
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
		/* @formatter:off */
		String[][] entryNamesAndValues = new String[][] {
				new String[] { "Windows - Gradle wrapper in root project",
						EGradleCallType.WINDOWS_GRADLE_WRAPPER.getId() },
				new String[] { "Windows - Use gradle installation", EGradleCallType.WINDOWS_GRADLE_INSTALLED.getId() },
				new String[] { "Linux/Mac - Gradle wrapper in root project",
						EGradleCallType.LINUX_GRADLE_WRAPPER.getId() },
				new String[] { "Linux/Mac - Use gradle installation", EGradleCallType.LINUX_GRADLE_INSTALLED.getId() },
				new String[] { "Custom", EGradleCallType.CUSTOM.getId() } };
		/* @formatter:on */
		gradleCallTypeRadioButton = new ComboFieldEditor(P_GRADLE_CALL_TYPE.getId(), "Call type", entryNamesAndValues,
				gradleCallGroup);

		addField(gradleCallTypeRadioButton);
		/* @formatter:off */
		String[][] shellTypeComboValues = new String[][] { new String[] { "bash", EGradleShellType.BASH.getId() },
				new String[] { "sh", EGradleShellType.SH.getId() },
				new String[] { "cmd", EGradleShellType.CMD.getId() } };
		/* @formatter:on */
		shellFieldEditor = new ChangeableComboFieldEditor(P_GRADLE_SHELL.getId(), "Shell", shellTypeComboValues,
				gradleCallGroup);
		addField(shellFieldEditor);
		gradleCommandFieldEditor = new StringFieldEditor(P_GRADLE_CALL_COMMAND.getId(), "Gradle call", gradleCallGroup);
		addField(gradleCommandFieldEditor);
		gradleInstallBinDirectoryFieldEditor = new DirectoryFieldEditor(P_GRADLE_INSTALL_BIN_FOLDER.getId(),
				"Gradle bin folder:", gradleCallGroup);
		addField(gradleInstallBinDirectoryFieldEditor);

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

		Group validationGroup = SWTFactory.createGroup(getFieldEditorParent(), "Validate preferences correct", 1, 10,
				SWT.FILL);
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

		validationOutputField = new Text(validationGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.READ_ONLY);
		validationOutputField.setLayoutData(gridDataLastColumn);

		validationButton = new Button(validationGroup, SWT.NONE);
		validationButton.setText("Start validation");
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

				IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
				try {
					progressService.busyCursorWhile(new ValidationProgressRunnable());
				} catch (InvocationTargetException | InterruptedException e1) {
					EGradleUtil.log(e1);
				}
			}
		});
	}

	@Override
	protected void updateApplyButton() {
		super.updateApplyButton();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults(); // set defaults and store them, so can now be
									// loaded:
		String storedCallTypeId = getPreferenceStore().getDefaultString(PreferenceConstants.P_GRADLE_CALL_TYPE.getId());
		updateCallTypeFields(storedCallTypeId);
	}

	private boolean validationRunning = false;

	private class ValidationProgressRunnable /* extends Job */ implements IRunnableWithProgress {
		private GradleConfigurationValidator validator;
		private MutableGradleConfiguration configuration;
		private OutputHandler outputHandler;

		public ValidationProgressRunnable() {
			outputHandler = new OutputHandler() {

				@Override
				public void output(String line) {
					EGradleUtil.safeAsyncExec(new Runnable() {

						@Override
						public void run() {
							validationOutputField.append(line + "\n");
						}
					});
				}

			};

			validator = new GradleConfigurationValidator(new SimpleProcessExecutor(outputHandler, true, 10));
			validator.setOutputHandler(outputHandler);
			configuration = new MutableGradleConfiguration();
			configuration.setGradleCommand(gradleCommandFieldEditor.getStringValue());
			configuration.setGradleBinDirectory(gradleInstallBinDirectoryFieldEditor.getStringValue());
			configuration.setShellCommand(EGradleShellType.findById(shellFieldEditor.getStringValue()));
			configuration.setWorkingDirectory(rootPathDirectoryEditor.getStringValue());
			configuration.setJavaHome(defaultJavaHomeDirectoryEditor.getStringValue());
		}

		@Override
		public void run(IProgressMonitor monitor) {
			/* check more... */
			try {
				validator.validate(configuration);
				EGradleUtil.safeAsyncExec(new Runnable() {

					@Override
					public void run() {
						checkState(); // we say its valid - but the others...
						outputHandler.output("\n\n\n\nOK - your gradle settings are correct and working.");

					}
				});
			} catch (ValidationException e) {
				EGradleUtil.safeAsyncExec(new Runnable() {

					@Override
					public void run() {
						setValid(false);
						StringBuilder sb = new StringBuilder();
						sb.append(e.getMessage());
						String details = e.getDetails();
						if (details != null) {
							sb.append("\n");
							sb.append(details);
						}
						outputHandler.output("\n\n\n\nFAILED - " + sb.toString());

					}
				});
				
			} finally {
				EGradleUtil.safeAsyncExec(new Runnable() {

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
		updateCallGroupEnabledStateByStoredCallTypeId();
	}

	/**
	 * Updates group enabled state and returns stored call type id
	 * 
	 * @return stored call type id
	 */
	private String updateCallGroupEnabledStateByStoredCallTypeId() {
		String callTypeId = gradleCallTypeRadioButton.getPreferenceStore().getString(P_GRADLE_CALL_TYPE.getId());
		updateCallGroupEnabledState(callTypeId);
		return callTypeId;
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
		String callTypeId = (String) value;
		updateCallGroupEnabledState(callTypeId);

		EGradleCallType callType = EGradleCallType.findById(callTypeId);
		if (callType == null) {
			throw new IllegalStateException("Wrong implemented - not a call type ID:" + callTypeId);
		}
		if (EGradleCallType.CUSTOM.equals(callType)) {
			/*
			 * do nothing, just keep the editor fields from former default
			 * settings - user can change...
			 */
		} else {
			setCallGroupPartsEnabled(false);
			/* set defaults */
			shellFieldEditor.setStringValue(callType.getDefaultShell().getId());
			gradleInstallBinDirectoryFieldEditor.setStringValue(callType.getDefaultGradleBinFolder());
			gradleCommandFieldEditor.setStringValue(callType.getDefaultGradleCommand());
		}

	}

	private void updateCallGroupEnabledState(String callTypeId) {
		if (EGradleCallType.CUSTOM.getId().equals(callTypeId)) {
			/* set all editable */
			setCallGroupPartsEnabled(true);
		} else {
			setCallGroupPartsEnabled(false);
			/* set defaults */
		}
	}

	private void setCallGroupPartsEnabled(boolean enabled) {
		shellFieldEditor.setEnabled(enabled, gradleCallGroup);
		gradleCommandFieldEditor.setEnabled(enabled, gradleCallGroup);
		gradleInstallBinDirectoryFieldEditor.setEnabled(enabled, gradleCallGroup);
	}

	public void init(IWorkbench workbench) {

	}

}