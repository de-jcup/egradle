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
package de.jcup.egradle.eclipse.importWizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class EGradleRootProjectImportWizardPage extends WizardPage {

	protected DirectoryFieldEditor editor;

	public EGradleRootProjectImportWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName); // NON-NLS-1
		setDescription("Import a gradle root project with all subprojects from given root folder"); // NON-NLS-1
	}

	private IPath selectedPath;

	private void setSelectedPath(IPath selectedPath) {
		this.selectedPath = selectedPath;
	}

	IPath getSelectedPath() {
		return selectedPath;
	}

	@Override
	public void createControl(Composite parent) {
		Composite fileSelectionArea = new Composite(parent, SWT.NONE);
		GridData fileSelectionData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		fileSelectionArea.setLayoutData(fileSelectionData);

		GridLayout fileSelectionLayout = new GridLayout();
		fileSelectionLayout.numColumns = 3;
		fileSelectionLayout.makeColumnsEqualWidth = false;
		fileSelectionLayout.marginWidth = 0;
		fileSelectionLayout.marginHeight = 0;
		fileSelectionArea.setLayout(fileSelectionLayout);

		editor = new DirectoryFieldEditor("Gradle root folder", "Select Folder: ", fileSelectionArea); // NON-NLS-1
		// //NON-NLS-2
		editor.getTextControl(fileSelectionArea).addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				IPath path = new Path(EGradleRootProjectImportWizardPage.this.editor.getStringValue());
				setSelectedPath(path);
			}
		});
		// String[] extensions = new String[] { "*.*" }; // NON-NLS-1
		// editor.setf(extensions);
		fileSelectionArea.moveAbove(null);
	}
}
