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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.egradle.eclipse.ide.IdeUtil;

public class EGradleValidationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private BooleanFieldEditor outputValidationEnabled;
	private BooleanFieldEditor showConsoleViewOnBuildFailed;


	public EGradleValidationPreferencePage() {
		super(GRID);
		setPreferenceStore(IdeUtil.getPreferences().getPreferenceStore());
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

		outputValidationEnabled=		new BooleanFieldEditor(
					EGradlePreferenceConstants.P_OUTPUT_VALIDATION_ENABLED.getId(),
					"Output validation enabled",
					getFieldEditorParent());
		addField(outputValidationEnabled);
		
		showConsoleViewOnBuildFailed=		new BooleanFieldEditor(
				EGradlePreferenceConstants.P_SHOW_CONSOLE_VIEW_ON_BUILD_FAILED_ENABLED.getId(),
				"Show console view on build failure",
				getFieldEditorParent());
		addField(showConsoleViewOnBuildFailed);
	}

	public boolean performOk() {
		boolean done =  super.performOk();
		if (done){
			if (!outputValidationEnabled.getBooleanValue()){
				IdeUtil.removeAllValidationErrorsOfConsoleOutput();
			}
		}
		return done;
	}
	
	
	public void init(IWorkbench workbench) {

	}

}