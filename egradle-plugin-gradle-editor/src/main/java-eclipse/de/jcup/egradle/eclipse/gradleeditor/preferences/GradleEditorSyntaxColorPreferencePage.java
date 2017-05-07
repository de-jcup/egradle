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

import static de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorSyntaxColorPreferenceConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.egradle.eclipse.gradleeditor.EditorUtil;
import de.jcup.egradle.eclipse.gradleeditor.GradleEditorColorConstants;

public class GradleEditorSyntaxColorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public GradleEditorSyntaxColorPreferencePage() {
		setPreferenceStore(EditorUtil.getPreferences().getPreferenceStore());
	}
	
	@Override
	public void init(IWorkbench workbench) {
		
	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();
		Map<GradleEditorSyntaxColorPreferenceConstants, ColorFieldEditor> editorMap = new HashMap<GradleEditorSyntaxColorPreferenceConstants, ColorFieldEditor>();
		for (GradleEditorSyntaxColorPreferenceConstants colorIdentifier: GradleEditorSyntaxColorPreferenceConstants.values()){
			ColorFieldEditor editor = new ColorFieldEditor(colorIdentifier.getId(), colorIdentifier.getLabelText(), parent);
			editorMap.put(colorIdentifier, editor);
			addField(editor);
		}
		Button restoreDarkThemeColorsButton= new Button(parent,  SWT.PUSH);
		restoreDarkThemeColorsButton.setText("Restore Defaults for Dark Theme");
		restoreDarkThemeColorsButton.setToolTipText("Same as 'Restore Defaults' but for dark themes.\n EGradle makes just a suggestion, you still have to apply or cancel the settings.");
		restoreDarkThemeColorsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				/* editor colors */
				changeColor(editorMap, COLOR_NORMAL_TEXT, GradleEditorColorConstants.GRAY_JAVA);
				changeColor(editorMap, COLOR_JAVA_KEYWORD, GradleEditorColorConstants.MIDDLE_GREEN);
				changeColor(editorMap, COLOR_ANNOTATION, GradleEditorColorConstants.DARK_THEME_GRAY);
				
				changeColor(editorMap, COLOR_GROOVY_KEYWORD, GradleEditorColorConstants.MIDDLE_GREEN);
				changeColor(editorMap, COLOR_GROOVY_DOC, GradleEditorColorConstants.MEDIUM_CYAN);
				changeColor(editorMap, COLOR_NORMAL_STRING, GradleEditorColorConstants.MIDDLE_GRAY);
				changeColor(editorMap, COLOR_GSTRING, GradleEditorColorConstants.MIDDLE_ORANGE);
				changeColor(editorMap, COLOR_COMMENT, GradleEditorColorConstants.GREEN_JAVA);
				changeColor(editorMap, COLOR_GRADLE_APPLY_KEYWORD, GradleEditorColorConstants.MIDDLE_BROWN);
				changeColor(editorMap, COLOR_GRADLE_OTHER_KEYWORD, GradleEditorColorConstants.MIDDLE_GREEN);
				changeColor(editorMap, COLOR_GRADLE_TASK_KEYWORD, GradleEditorColorConstants.TASK_CYAN);
				changeColor(editorMap, COLOR_GRADLE_VARIABLE, GradleEditorColorConstants.DARK_THEME_GRAY);
				changeColor(editorMap, COLOR_JAVA_LITERAL, GradleEditorColorConstants.MIDDLE_GREEN);
				
			}

			private void changeColor(Map<GradleEditorSyntaxColorPreferenceConstants, ColorFieldEditor> editorMap,
					GradleEditorSyntaxColorPreferenceConstants colorId, RGB rgb) {
				editorMap.get(colorId).getColorSelector().setColorValue(rgb);
			}
			
		});
			
		
	}
	
}