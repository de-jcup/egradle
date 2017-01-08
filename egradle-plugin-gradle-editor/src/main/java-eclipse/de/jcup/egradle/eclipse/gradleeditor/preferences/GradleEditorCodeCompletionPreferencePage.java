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

import static de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorPreferenceConstants.*;
import static de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorPreferences.*;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.egradle.core.codecompletion.UserHomeBasedXMLProposalDataModelProvider;

public class GradleEditorCodeCompletionPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	private BooleanFieldEditor codeCompletionEnabled;

	public GradleEditorCodeCompletionPreferencePage() {
		setPreferenceStore(EDITOR_PREFERENCES.getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {
		codeCompletionEnabled = new BooleanFieldEditor(P_EDITOR_CODECOMPLETION_ENABLED.getId(),
				"Code completion enabled", getFieldEditorParent());
		addField(codeCompletionEnabled);
	}
	
	@Override
	protected void performDefaults() {
		super.performDefaults();
		
		/* reload xml parts */
		UserHomeBasedXMLProposalDataModelProvider.INSTANCE.restoreDefaults();
		setMessage("XML code completion definition reloaded");
	}
	
	
}
