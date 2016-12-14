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
import static de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorSyntaxColorPreferenceConstants.*;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.jcup.egradle.eclipse.gradleeditor.GradleEditorColorConstants;

/**
 * Class used to initialize default preference values.
 */
public class GradleEditorPreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = EDITOR_PREFERENCES.getPreferenceStore();
		store.setDefault(P_LINK_OUTLINE_WITH_EDITOR.getId(), true);
		
		/* bracket rendering configuration */
		store.setDefault(P_EDITOR_MATCHING_BRACKETS_ENABLED.getId(), true); // per default matching is enabled, but without the two other special parts
		store.setDefault(P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION.getId(), false);
		store.setDefault(P_EDITOR_ENCLOSING_BRACKETS.getId(), false);
		/* bracket color */
		EDITOR_PREFERENCES.setDefaultColor(P_EDITOR_MATCHING_BRACKETS_COLOR, GradleEditorColorConstants.DEFAULT_CARET_HIGHLIGHTING);
		
		/* editor colors */
		EDITOR_PREFERENCES.setDefaultColor(COLOR_NORMAL_TEXT, GradleEditorColorConstants.BLACK);

		EDITOR_PREFERENCES.setDefaultColor(COLOR_JAVA_KEYWORD, GradleEditorColorConstants.KEYWORD_DEFAULT_PURPLE);
		EDITOR_PREFERENCES.setDefaultColor(COLOR_GROOVY_KEYWORD, GradleEditorColorConstants.KEYWORD_DEFAULT_PURPLE);
		EDITOR_PREFERENCES.setDefaultColor(COLOR_NORMAL_STRING, GradleEditorColorConstants.STRING_DEFAULT_BLUE);
		EDITOR_PREFERENCES.setDefaultColor(COLOR_GSTRING, GradleEditorColorConstants.ROYALBLUE);
		EDITOR_PREFERENCES.setDefaultColor(COLOR_COMMENT, GradleEditorColorConstants.COMMENT_DEFAULT_GREEN);
		
		EDITOR_PREFERENCES.setDefaultColor(COLOR_GRADLE_APPLY_KEYWORD, GradleEditorColorConstants.LINK_DEFAULT_BLUE);
		EDITOR_PREFERENCES.setDefaultColor(COLOR_GRADLE_KEYWORD, GradleEditorColorConstants.KEYWORD_DEFAULT_PURPLE);
		
		EDITOR_PREFERENCES.setDefaultColor(COLOR_GRADLE_TASK_KEYWORD, GradleEditorColorConstants.TASK_DEFAULT_RED);
		EDITOR_PREFERENCES.setDefaultColor(COLOR_GRADLE_VARIABLE, GradleEditorColorConstants.DARK_GRAY);
		EDITOR_PREFERENCES.setDefaultColor(COLOR_JAVA_LITERAL, GradleEditorColorConstants.KEYWORD_DEFAULT_PURPLE);
		
	}
	
	
}
