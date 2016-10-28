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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.jcup.egradle.eclipse.api.EGradleUtil;

public class EGradleRootProjectImportWizardPage extends WizardPage {

	protected DirectoryFieldEditor editor;

	public EGradleRootProjectImportWizardPage(String pageName) {
		super(pageName);
		setTitle("Import gradle projects"); // NON-NLS-1
		setDescription("Import a gradle root project with all subprojects from given root folder"); // NON-NLS-1
	}

	private IPath selectedPath;

	private void setSelectedPath(String selectedPath) {
		boolean empty = StringUtils.isEmpty(selectedPath);
		setPageComplete(!empty);
		if (empty){
			this.selectedPath=null;
		}else{
			this.selectedPath = new Path(selectedPath);
		}
	}

	IPath getSelectedPath() {
		return selectedPath;
	}

	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite folderSelectionArea = new Composite(parent, SWT.NONE);
		GridData fileSelectionData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		folderSelectionArea.setLayoutData(fileSelectionData);

		GridLayout fileSelectionLayout = new GridLayout();
		fileSelectionLayout.numColumns = 3;
		fileSelectionLayout.makeColumnsEqualWidth = false;
		fileSelectionLayout.marginWidth = 0;
		fileSelectionLayout.marginHeight = 0;
		folderSelectionArea.setLayout(fileSelectionLayout);

		editor = new DirectoryFieldEditor("Gradle root folder", "Select Folder: ", folderSelectionArea); // NON-NLS-1
		String path = EGradleUtil.getPreferences().getRootProjectPath();
		editor.setStringValue(path);
		setSelectedPath(path);
		/* we use current root project path for select - so a simple full re-import is always possible*/
		// //NON-NLS-2
		editor.getTextControl(folderSelectionArea).addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				Object source = e.getSource();
				if (source instanceof Text){
					Text text = (Text) source;
					setSelectedPath(text.getText());
				}
			}
		});

		setControl(folderSelectionArea);
	}
	
	@Override
	public boolean isPageComplete() {
		return super.isPageComplete();
	}
}
