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

import static de.jcup.egradle.eclipse.preferences.EGradlePreferenceConstants.*;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.execution.validation.ExecutionConfigDelegate;
import de.jcup.egradle.eclipse.ui.ExecutionConfigComposite;

public class EGradlePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage, ExecutionConfigDelegate {

	private String originRootProject;
	private ExecutionConfigComposite configComposite;

	public EGradlePreferencePage() {
		super(GRID);
		setPreferenceStore(EGradleUtil.getPreferences().getPreferenceStore());
		setDescription("Preferences for EGradle");
		configComposite = new ExecutionConfigComposite(this);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		Composite fieldEditorParent = getFieldEditorParent();
		configComposite.createFieldEditors(fieldEditorParent);
	}

	@Override
	public boolean performOk() {
		boolean done =  super.performOk();
		if (done){
			String newRootProject=configComposite.getRootPathDirectory();
			if (! StringUtils.equals(newRootProject, originRootProject)){
				/* root project has changed - refresh decoration of all projects */ 
				EGradleUtil.refreshAllProjectDecorations();
			}
		}
		return done;
	}
	
	
	@Override
	protected void performDefaults() {
		super.performDefaults(); // set defaults and store them, so can now be
									// loaded:
		String storedCallTypeId = getPreferenceStore().getDefaultString(EGradlePreferenceConstants.P_GRADLE_CALL_TYPE.getId());
		configComposite.updateCallTypeFields(storedCallTypeId);
	}

	@Override
	public void handleValidationResult(boolean valid) {
		if (valid){
			checkState(); // we say its valid - but the others...
		}else{
			setValid(false);
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
		String callTypeId = EGradleUtil.getPreferences().getStringPreference(P_GRADLE_CALL_TYPE);
		configComposite.updateCallTypeGroupEnabledState(callTypeId);
		return callTypeId;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		if (configComposite.isGradleCallTypeButton(event)) {
			configComposite.updateCallTypeFields(event.getNewValue());
		}
	}

	public void init(IWorkbench workbench) {

	}
	
	@Override
	public void setValid(boolean valid) {
		super.setValid(valid);
	}

	@Override
	public void handleValidationRunning(boolean running) {
		updateApplyButton();
	}

	@Override
	public void handleValidationStateChanges(boolean valid) {
		setValid(valid);
	}

	@Override
	public void handleFieldEditorAdded(FieldEditor editor) {
		addField(editor);
		
	}

	@Override
	public void handleCheckState() {
		checkState();
		
	}


	@Override
	public void handleOriginRootProject(String originRootProject) {
		this.originRootProject=originRootProject;
		
	}

	@Override
	public boolean isHandlingPropertyChanges() {
		/* the field editor keeps care of the fields,
		 * so we must handle property events here standalone
		 */
		return true;
	}


}