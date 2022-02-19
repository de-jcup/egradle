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
package de.jcup.egradle.eclipse.ide.wizards;

import static de.jcup.egradle.eclipse.ui.SWTUtil.*;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.ui.SWTFactory;
import de.jcup.egradle.ide.NewProjectContext;
import de.jcup.egradle.ide.NewProjectTemplateVariables;
import de.jcup.egradle.template.FileStructureTemplate;

public class EGradleNewProjectWizardTemplateDetailsPage extends WizardPage {

    private NewProjectContext context;
    private Group multiProjectGroup;
    private Composite composite;
    private Text multiProjectNamesText;
    private Group javaGroup;
    private Text javaSourceCompatibilityText;
    private Text javaHomeText;
    private Group commonGroup;
    private Text gradleGroupNameText;
    private Text gradleVersionText;
    private Button gradleWrapperEnabledRadioButton;
    private Label gradleVersionLabel;

    public EGradleNewProjectWizardTemplateDetailsPage(NewProjectContext context) {
        super("templateDetails");
        this.context = context;
        setTitle("Gradle template details");
        setImageDescriptor(IDEUtil.createImageDescriptor("icons/egradle-banner_64x64.png"));
        setDescription("Setup details of used template");
    }

    @Override
    public void createControl(Composite parent) {
        composite = SWTFactory.createComposite(parent, 1, SWT.FILL, SWT.FILL);

        initializeDialogUnits(parent);

        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        initCommonParts(composite);
        initMultiProjectParts(composite);
        initJavaParts(composite);

        // Show description on opening
        setErrorMessage(null);
        setMessage(null);
        setControl(composite);

        Dialog.applyDialogFont(composite);

        updateUI();
    }

    private void initCommonParts(Composite composite) {
        commonGroup = SWTFactory.createGroup(composite, "Common", 1, SWT.FILL, SWT.FILL);
        commonGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        SWTFactory.createLabel(commonGroup, "Please enter group name", SWT.FILL);
        gradleGroupNameText = SWTFactory.createSingleText(commonGroup, 1);
        gradleGroupNameText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setPageComplete(validatePage());
            }
        });

        gradleWrapperEnabledRadioButton = SWTFactory.createCheckButton(commonGroup, "Use gradle wrapper", null, context.isSupportingGradleWrapper(), SWT.FILL);
        gradleWrapperEnabledRadioButton.setSelection(true); // per default enabled
        gradleWrapperEnabledRadioButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                context.setGradleWrapperEnabled(gradleWrapperEnabledRadioButton.getSelection());
                updateUI();
                setPageComplete(validatePage());
            }

        });

        gradleVersionLabel = SWTFactory.createLabel(commonGroup, "Gradle Version", SWT.FILL);

        gradleVersionText = SWTFactory.createSingleText(commonGroup, 1);
        gradleVersionText.setMessage(NewProjectTemplateVariables.VAR__GRADLE__VERSION.getDefaultValue());
        gradleVersionText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setPageComplete(validatePage());
            }
        });
        gradleVersionText.setEnabled(context.isSupportingGradleWrapper());
    }

    private void initMultiProjectParts(Composite composite) {
        multiProjectGroup = SWTFactory.createGroup(composite, "Multi project", 1, SWT.FILL, SWT.FILL);
        multiProjectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        SWTFactory.createLabel(multiProjectGroup, "Please enter sub project name(s). Use comma to separate", SWT.FILL);
        multiProjectNamesText = SWTFactory.createSingleText(multiProjectGroup, 1);
        multiProjectNamesText.setText(context.getUnProcessedMultiProjectNames());

        multiProjectNamesText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setPageComplete(validatePage());
            }
        });

    }

    private void initJavaParts(Composite composite) {
        javaGroup = SWTFactory.createGroup(composite, "Java", 1, SWT.FILL, SWT.FILL);
        javaGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        SWTFactory.createLabel(javaGroup, "Please enter source compatibility level", SWT.FILL);
        javaSourceCompatibilityText = SWTFactory.createSingleText(javaGroup, 1);
        javaSourceCompatibilityText.setText(NewProjectTemplateVariables.VAR__JAVA__VERSION.getDefaultValue());
        javaSourceCompatibilityText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setPageComplete(validatePage());
            }
        });

        SWTFactory.createLabel(javaGroup, "JAVA_HOME for gradle (optional)", SWT.FILL);
        javaHomeText = SWTFactory.createSingleText(javaGroup, 1);
        javaHomeText.setText(IDEUtil.getPreferences().getGlobalJavaHomePath());
        javaHomeText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setPageComplete(validatePage());
            }
        });

    }

    private boolean validatePage() {
        setMessage(null);
        
        FileStructureTemplate selectedTemplate = context.getSelectedTemplate();
        if (selectedTemplate == null) {
            return false;
        }
        /*
         * set context with current values - no matter if valid or not, or feature is
         * enabled.
         */
        String groupName = gradleGroupNameText.getText();
        String gradleVersion = gradleVersionText.getText();
        String multiProjects = multiProjectNamesText.getText();
        String javaSourceCompatibility = javaSourceCompatibilityText.getText();
        String javaHome = javaHomeText.getText();

        context.setJavaHome(javaHome);
        context.setGradleVersion(gradleVersion);
        context.setGroupName(groupName);
        context.setMultiProjects(multiProjects);
        context.setJavaSourceCompatibility(javaSourceCompatibility);

        if (!context.validateMultiProject()) {
            setMessage("Multiproject data not valid!", ERROR);
            return false;
        }
        if (!context.validateJavaSupport()) {
            setMessage("Java version not valid!", ERROR);
            return false;
        }
        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            /* only when become visible again do the ui update */
            updateUI();
        }
    }

    private void updateUI() {
        FileStructureTemplate selectedTemplate = context.getSelectedTemplate();
        if (selectedTemplate == null) {
            composite.setVisible(false);
            setDescription("No template selected");
            return;
        }
        composite.setVisible(true);
        setDescription("Define details for template '" + selectedTemplate.getName() + "'");

        showControl(gradleWrapperEnabledRadioButton, context.isSupportingGradleWrapper());
        showControl(gradleVersionLabel, context.isGradleWrapperSupportedAndEnabled());
        showControl(gradleVersionText, context.isGradleWrapperSupportedAndEnabled());

        showControl(multiProjectGroup, context.isMultiProject());
        showControl(javaGroup, context.isSupportingJava());
        setPageComplete(validatePage());
        gradleGroupNameText.setText(context.getGroupName());
        gradleGroupNameText.setMessage(context.getGroupName());// fallback... view when empty by user (still valid, because in this case
                                                               // autogenerated)
        
        /* make editable or not */
        gradleWrapperEnabledRadioButton.setEnabled(context.isSupportingGradleWrapper());
        gradleVersionLabel.setEnabled(context.isSupportingGradleWrapper());
        gradleVersionText.setEnabled(gradleWrapperEnabledRadioButton.isEnabled());
        
    }

}
