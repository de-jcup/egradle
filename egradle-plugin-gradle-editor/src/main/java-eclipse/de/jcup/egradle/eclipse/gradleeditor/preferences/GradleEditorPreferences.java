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

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.jcup.egradle.eclipse.api.ColorUtil;
import de.jcup.egradle.eclipse.api.EclipseUtil;
import de.jcup.egradle.eclipse.api.PreferenceIdentifiable;
import de.jcup.egradle.eclipse.gradleeditor.EditorActivator;
import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;

public class GradleEditorPreferences {

	public static GradleEditorPreferences EDITOR_PREFERENCES = new GradleEditorPreferences();
	private IPreferenceStore store;

	GradleEditorPreferences() {
		store = new ScopedPreferenceStore(InstanceScope.INSTANCE, EditorActivator.PLUGIN_ID);
		store.addPropertyChangeListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event==null){
					return;
				}
				String property = event.getProperty();
				if (property==null){
					return;
				}
				boolean colorChanged = false;
				for (GradleEditorSyntaxColorPreferenceConstants c: GradleEditorSyntaxColorPreferenceConstants.values()){
					if (property.equals(c.getId())){
						colorChanged=true;
						break;
					}
				}
				if (colorChanged){
					updateColorsInEGradleEditors();
				}
				
				
			}

			private void updateColorsInEGradleEditors() {
				/* inform all EGradle editors about color changes*/
				IWorkbenchPage activePage = EclipseUtil.getActivePage();
				if (activePage==null){
					return;
				}
				IEditorReference[] references = activePage.getEditorReferences();
				for (IEditorReference ref: references){
					IEditorPart editor = ref.getEditor(false);
					if (editor==null){
						continue;
					}
					if (! (editor instanceof GradleEditor)){
						continue;
					}
					GradleEditor geditor = (GradleEditor) editor;
					geditor.handleColorSettingsChanged();
				}
			}
		});
			
	}

	public String getStringPreference(GradleEditorPreferenceConstants id) {
		String data = getPreferenceStore().getString(id.getId());
		if (data == null) {
			data = "";
		}
		return data;
	}

	public boolean getBooleanPreference(GradleEditorPreferenceConstants id) {
		boolean data = getPreferenceStore().getBoolean(id.getId());
		return data;
	}

	public void setBooleanPreference(GradleEditorPreferenceConstants id, boolean value) {
		getPreferenceStore().setValue(id.getId(), value);
	}

	public IPreferenceStore getPreferenceStore() {
		return store;
	}

	public boolean getDefaultBooleanPreference(GradleEditorPreferenceConstants id) {
		boolean data = getPreferenceStore().getDefaultBoolean(id.getId());
		return data;
	}

	public RGB getColor(PreferenceIdentifiable identifiable) {
		RGB color = PreferenceConverter.getColor(getPreferenceStore(), identifiable.getId());
		return color;
	}
	
	/**
	 * Returns color as a web color in format "#RRGGBB"
	 * @param identifiable
	 * @return web color string
	 */
	public String getWebColor(PreferenceIdentifiable identifiable) {
		RGB color = getColor(identifiable);
		if (color==null){
			return null;
		}
		String webColor= ColorUtil.convertToHexColor(color);
		return webColor;
	}

	public void setDefaultColor(PreferenceIdentifiable identifiable, RGB color) {
		PreferenceConverter.setDefault(getPreferenceStore(), identifiable.getId(), color);
	}

	public boolean isCodeAssistProposalsEnabled() {
		return getBooleanPreference(GradleEditorPreferenceConstants.P_EDITOR_CODEASSIST_PROPOSALS_ENABLED);
	}
	
	public boolean isCodeAssistNoProposalsForGetterOrSetter() {
		return getBooleanPreference(GradleEditorPreferenceConstants.P_EDITOR_CODEASSIST_NO_PROPOSALS_FOR_GETTER_OR_SETTERS);
	}
	
	public boolean isCodeAssistTooltipsEnabled() {
		return getBooleanPreference(GradleEditorPreferenceConstants.P_EDITOR_CODEASSIST_TOOLTIPS_ENABLED);
	}

	
}
