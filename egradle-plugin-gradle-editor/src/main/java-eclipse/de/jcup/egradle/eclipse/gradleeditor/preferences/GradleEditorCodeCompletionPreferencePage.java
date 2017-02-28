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

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.egradle.codeassist.CodeCompletionRegistry;
import de.jcup.egradle.eclipse.gradleeditor.Activator;
import de.jcup.egradle.eclipse.ui.SWTFactory;
import de.jcup.egradle.sdk.SDK;
import de.jcup.egradle.sdk.SDKInfo;
import de.jcup.egradle.sdk.SDKManager;

public class GradleEditorCodeCompletionPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	private BooleanFieldEditor codeAssistProposalsEnabled;
	private BooleanFieldEditor codeAssistNoProposalsForGetterOrSetter;
	private BooleanFieldEditor codeAssistTooltipsEnabled;

	public GradleEditorCodeCompletionPreferencePage() {
		setPreferenceStore(EDITOR_PREFERENCES.getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();
		codeAssistProposalsEnabled = new BooleanFieldEditor(P_EDITOR_CODEASSIST_PROPOSALS_ENABLED.getId(),
				"Code completion enabled", parent);
		addField(codeAssistProposalsEnabled);

		codeAssistNoProposalsForGetterOrSetter = new BooleanFieldEditor(
				P_EDITOR_CODEASSIST_NO_PROPOSALS_FOR_GETTER_OR_SETTERS.getId(), "No proposals for getter or setter",
				parent);
		addField(codeAssistNoProposalsForGetterOrSetter);

		codeAssistTooltipsEnabled = new BooleanFieldEditor(P_EDITOR_CODEASSIST_TOOLTIPS_ENABLED.getId(),
				"Code tooltips enabled", parent);
		addField(codeAssistTooltipsEnabled);

		Button reloadButton = new Button(parent, SWT.PUSH);
		reloadButton.setText("Clean cache");
		reloadButton.setToolTipText("Clean cache of code completion ");
		reloadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Activator activator = Activator.getDefault();
				CodeCompletionRegistry registry = activator.getCodeCompletionRegistry();
				if (registry == null) {
					return;
				}
				registry.rebuild();
				;
			}
		});
		
		/* sdk info group*/
		GridData groupLayoutData = new GridData();
		groupLayoutData.horizontalAlignment = GridData.FILL;
		groupLayoutData.verticalAlignment = GridData.BEGINNING;
		groupLayoutData.grabExcessHorizontalSpace = true;
		groupLayoutData.grabExcessVerticalSpace = true;
		groupLayoutData.verticalSpan = 2;
		groupLayoutData.horizontalSpan = 3;

		Group validationGroup = SWTFactory.createGroup(parent, "SDK information", 1, 10, SWT.FILL);
		validationGroup.setLayoutData(groupLayoutData);
		
		/* SDK info*/
		SDK sdk = SDKManager.get().getCurrentSDK();
		
		SDKInfo sdkInfo = sdk.getInfo();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Version ").append(sdkInfo.getSdkVersion()).append(" contains\n");
		sb.append("- Gradle").append(sdkInfo.getGradleVersion()).append("\n");
		Date installationDate = sdkInfo.getInstallationDate();
		if (installationDate!=null){
			sb.append("- Was installed at ").append(DateFormat.getDateTimeInstance().format(installationDate));
		}
		
		Text text = new Text(validationGroup, SWT.MULTI);
		text.setBackground(parent.getBackground());
		text.setText(sb.toString());
	}

}
