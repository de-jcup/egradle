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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.ui.SWTFactory;

public class EGradleFileHandlingPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private BooleanFieldEditor automaticalDeriveBuildFolders;
    private BooleanFieldEditor addVirtualRootToAllWorkingSets;

    public EGradleFileHandlingPreferencePage() {
        super(GRID);
        setPreferenceStore(IDEUtil.getPreferences().getPreferenceStore());
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI
     * blocks needed to manipulate various types of preferences. Each field editor
     * knows how to save and restore itself.
     */
    public void createFieldEditors() {
        GridData groupLayoutData = new GridData();
        groupLayoutData.horizontalAlignment = GridData.FILL;
        groupLayoutData.verticalAlignment = GridData.BEGINNING;
        groupLayoutData.grabExcessHorizontalSpace = true;
        groupLayoutData.grabExcessVerticalSpace = false;
        groupLayoutData.verticalSpan = 2;
        groupLayoutData.horizontalSpan = 3;

        automaticalDeriveBuildFolders = new BooleanFieldEditor(EGradleIdePreferenceConstants.P_FILEHANDLING_AUTOMATICALLY_DERIVE_BUILDFOLDERS.getId(), "Automatically derive build folders",
                getFieldEditorParent());
        String info = "(Importer and 'refresh eclipse' action will automatically derive build folders)";

        addField(automaticalDeriveBuildFolders);
        SWTFactory.createLabel(getFieldEditorParent(), info, 2);

        addVirtualRootToAllWorkingSets = new BooleanFieldEditor(EGradleIdePreferenceConstants.P_ALWAYS_ADD_VIRTUAL_ROOT_TO_ALL_WORKINGSETS.getId(),
                "Virtual root project is automatically added to all working sets", getFieldEditorParent());
        String info2 = "(A new virtual root project is always added to all current working sets, so always visible)";

        addField(addVirtualRootToAllWorkingSets);
        SWTFactory.createLabel(getFieldEditorParent(), info2, 2);

    }

    public boolean performOk() {
        boolean done = super.performOk();
        return done;
    }

    public void init(IWorkbench workbench) {

    }

}