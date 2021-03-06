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

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import de.jcup.egradle.eclipse.MainActivator;
import de.jcup.egradle.eclipse.util.EclipseUtil;

public class EGradlePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public EGradlePreferencePage() {
        super(GRID);
//        setDescription(createDescription());
        setImageDescriptor(EclipseUtil.createImageDescriptor("icons/gradle-og.png", MainActivator.PLUGIN_ID));
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI
     * blocks needed to manipulate various types of preferences. Each field editor
     * knows how to save and restore itself.
     */
    public void createFieldEditors() {

        Composite composite = getFieldEditorParent();

        String message = "Configure EGradle IDE at sub pages - if you installed only editor, there is only one page\n\nYou can visit the project site at <a href=\"https://github.com/de-jcup/egradle/wiki\">GitHub</a>.";

        Link link = new Link(composite, SWT.NONE);
        link.setText(message);
        link.setSize(400, 100);
        link.setLayoutData(GridDataFactory.fillDefaults());
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    // Open default external browser
                    PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(e.text));
                } catch (Exception ex) {
                    MainActivator.getDefault().getLog().log(new Status(IStatus.ERROR, MainActivator.PLUGIN_ID, "Was not able to open url in external browser", ex));
                }
            }
        });

    }

    public void init(IWorkbench workbench) {
    }

    @Override
    public void setValid(boolean valid) {
        super.setValid(valid);
    }

}