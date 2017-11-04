/*
 * Copyright 2017 Albert Tregnaghi
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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.RGB;

import de.jcup.egradle.eclipse.util.PreferenceIdentifiable;

public interface IEditorPreferences {
	public String getStringPreference(PreferenceIdentifiable id) ;

	public boolean getBooleanPreference(PreferenceIdentifiable id) ;

	public void setBooleanPreference(PreferenceIdentifiable id, boolean value) ;

	public IPreferenceStore getPreferenceStore() ;

	public boolean getDefaultBooleanPreference(PreferenceIdentifiable id) ;

	public RGB getColor(PreferenceIdentifiable identifiable) ;
	
	/**
	 * Returns color as a web color in format "#RRGGBB"
	 * @param identifiable
	 * @return web color string
	 */
	public String getWebColor(PreferenceIdentifiable identifiable) ;

	public void setDefaultColor(PreferenceIdentifiable identifiable, RGB color) ;

	public boolean isCodeAssistProposalsEnabled() ;
	
	public boolean isCodeAssistNoProposalsForGetterOrSetter() ;
	
	public boolean isCodeAssistTooltipsEnabled() ;

	public boolean isEditorAutoCreateEndBracketsEnabled();

	public boolean isLinkOutlineWithEditorEnabled();

	public PreferenceIdentifiable getP_EDITOR_MATCHING_BRACKETS_ENABLED();

	public PreferenceIdentifiable getP_EDITOR_MATCHING_BRACKETS_COLOR();

	public PreferenceIdentifiable getP_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION();

	public PreferenceIdentifiable getP_EDITOR_ENCLOSING_BRACKETS();
}
