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
package de.jcup.egradle.eclipse.ide.preferences;

import static de.jcup.egradle.eclipse.ide.preferences.EGradleIdePreferenceConstants.*;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.ide.execution.validation.RootProjectValidationHandler;
import de.jcup.egradle.eclipse.ide.ui.RootProjectConfigUIDelegate;

public class EGradleSetupGradlePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage, RootProjectValidationHandler {

    private String originRootProject;
    private RootProjectConfigUIDelegate uiDelegate;

    public EGradleSetupGradlePreferencePage() {
        super(GRID);
        setPreferenceStore(IDEUtil.getPreferences().getPreferenceStore());
        setDescription("Define how EGradle calls gradle.");
        uiDelegate = new RootProjectConfigUIDelegate(this);
        uiDelegate.setRootPathMayBeEmpty(true);// was annoying user so on
                                               // preference empty is okay, if
                                               // not set the dialog will
                                               // appear on actions
        originRootProject = getPreferenceStore().getString(P_ROOTPROJECT_PATH.getId());
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI
     * blocks needed to manipulate various types of preferences. Each field editor
     * knows how to save and restore itself.
     */
    public void createFieldEditors() {
        Composite fieldEditorParent = getFieldEditorParent();
        uiDelegate.createConfigUI(fieldEditorParent);
    }

    @Override
    public boolean performOk() {
        boolean done = super.performOk();
        if (done) {
            String newRootProject = uiDelegate.getRootPathDirectory();
            if (StringUtils.isEmpty(newRootProject)) {
                try {
                    IDEUtil.setNoRootProjectFolder();
                } catch (CoreException e) {
                    IDEUtil.logError("Was not able to set no root project folder", e);
                    /* not done... */
                    return false;

                }
            } else if (!StringUtils.equals(newRootProject, originRootProject)) {
                /*
                 * root project has changed - refresh decoration of all projects
                 */
                File newRootProjectFolder = new File(newRootProject);
                try {
                    IDEUtil.setNewRootProjectFolder(newRootProjectFolder);
                } catch (CoreException e) {
                    IDEUtil.logError("Was not able to set new root project folder:" + newRootProject, e);
                    /* not done... */
                    return false;
                }
            }
        }
        return done;
    }

    @Override
    protected void performDefaults() {
        super.performDefaults(); // set defaults and store them, so can now be
                                 // loaded:
        String storedCallTypeId = getPreferenceStore().getDefaultString(P_GRADLE_CALL_TYPE.getId());
        uiDelegate.updateCallTypeFields(storedCallTypeId);
    }

    @Override
    public void handleValidationResult(boolean valid) {
        if (valid) {
            checkState(); // we say its valid - but the others...
        } else {
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
        String callTypeId = IDEUtil.getPreferences().getStringPreference(P_GRADLE_CALL_TYPE);
        uiDelegate.updateCallTypeGroupEnabledState(callTypeId);
        return callTypeId;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        super.propertyChange(event);
        if (uiDelegate.isGradleCallTypeButton(event)) {
            uiDelegate.updateCallTypeFields(event.getNewValue());
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
    public void onValidationStateChanged(boolean valid) {
        setValid(valid);
    }

    @Override
    public void addFieldEditor(FieldEditor editor) {
        addField(editor);
    }

    @Override
    public boolean isHandlingPropertyChanges() {
        /*
         * the field editor keeps care of the fields, so we must handle property events
         * here standalone
         */
        return true;
    }

}