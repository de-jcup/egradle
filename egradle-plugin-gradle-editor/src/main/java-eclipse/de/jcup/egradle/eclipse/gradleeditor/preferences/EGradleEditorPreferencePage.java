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

import static de.jcup.egradle.eclipse.gradleeditor.preferences.EGradleEditorPreferenceConstants.*;
import static de.jcup.egradle.eclipse.gradleeditor.preferences.EGradleEditorPreferences.*;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class EGradleEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private BooleanFieldEditor linkEditorWithOutline;

	public EGradleEditorPreferencePage() {
		super(GRID);
		setPreferenceStore(EDITOR_PREFERENCES.getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {

		linkEditorWithOutline = new BooleanFieldEditor(P_LINK_OUTLINE_WITH_EDITOR.getId(), "Link editor with outline (default)",
				getFieldEditorParent());

		addField(linkEditorWithOutline);
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void initialize() {
		super.initialize();
	}

}
