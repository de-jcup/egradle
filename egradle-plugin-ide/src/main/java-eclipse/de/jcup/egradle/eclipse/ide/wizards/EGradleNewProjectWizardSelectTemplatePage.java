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

import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.ui.SWTFactory;
import de.jcup.egradle.eclipse.ui.SWTUtil;
import de.jcup.egradle.ide.NewProjectContext;
import de.jcup.egradle.ide.NewProjectTemplateVariables;
import de.jcup.egradle.template.Features;
import de.jcup.egradle.template.FileStructureTemplate;

public class EGradleNewProjectWizardSelectTemplatePage extends WizardPage {

    private Text descriptionText;
    private NewProjectContext context;
    private List<FileStructureTemplate> templates;
    private org.eclipse.swt.widgets.List templateList;
    private Text infoText;

    public EGradleNewProjectWizardSelectTemplatePage(NewProjectContext context) {
        super("template");
        this.context = context;
        setTitle("Gradle project template");
        setImageDescriptor(IDEUtil.createImageDescriptor("icons/egradle-banner_64x64.png"));
        setDescription("Select new project template use");
        templates = IDEUtil.getNewProjectTemplates();
    }

    @Override
    public void createControl(Composite p) {
        Composite parent = SWTFactory.createComposite(p, 1, SWT.BEGINNING, SWT.FILL);

        GridData layoutData = new GridData();
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.verticalAlignment = SWT.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.minimumHeight = 50;
        layoutData.heightHint = 200;

        templateList = new org.eclipse.swt.widgets.List(parent, SWT.SINGLE | SWT.SCROLL_PAGE);

        templateList.setLayoutData(layoutData);

        descriptionText = SWTFactory.createText(parent, SWT.V_SCROLL | SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.READ_ONLY, SWT.FILL);

        layoutData = new GridData();
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.verticalAlignment = SWT.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = false;
        layoutData.minimumHeight = 50;
        layoutData.heightHint = 80;
        descriptionText.setLayoutData(layoutData);

        infoText = SWTFactory.createText(parent, SWT.WRAP | SWT.MULTI | SWT.READ_ONLY, SWT.FILL);
        SWTUtil.setFontDataStyle(infoText, SWT.ITALIC);

        layoutData = new GridData();
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.verticalAlignment = SWT.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = false;
        layoutData.minimumHeight = 10;
        layoutData.heightHint = 30;
        infoText.setLayoutData(layoutData);

        initTemplateComponent(parent);

        // required to avoid an error in the system
        setControl(parent);

    }

    @Override
    public boolean isPageComplete() {
        return super.isPageComplete();
    }

    void initTemplateComponent(Composite parent) {
        if (templates.size() == 0) {
            return;
        }
        FileStructureTemplate contextTemplate = context.getSelectedTemplate();
        int initselection = -1;
        String[] items = new String[templates.size()];
        for (int i = 0; i < items.length; i++) {
            FileStructureTemplate fileStructureTemplate = templates.get(i);
            items[i] = fileStructureTemplate.getName();
            if (fileStructureTemplate == contextTemplate) {
                initselection = i;
            }
        }

        templateList.setFont(parent.getFont());
        templateList.setItems(items);

        SelectionListener listener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = templateList.getSelectionIndex();
                handleTemplateSelection(index);
            }

        };
        templateList.addSelectionListener(listener);

        if (initselection != -1) {
            templateList.select(initselection);
            handleTemplateSelection(initselection);
        }
    }

    private void handleTemplateSelection(int index) {
        FileStructureTemplate selectedTemplate = templates.get(index);
        context.setSelectedTemplate(selectedTemplate);
        descriptionText.setText(selectedTemplate.getDescription());
        StringBuilder info = new StringBuilder();
        if (selectedTemplate.hasFeature(Features.NEW_PROJECT__SUPPORTS_GRADLEWRAPPER)) {
            info.append("A gradle wrapper will be created. You can use the wrapper for import");
        } else {
            info.append("Comes without gradle wrapper - so you must import with a local gradle installation!");
        }
        if (selectedTemplate.hasFeature(Features.NEW_PROJECT__SUPPORTS_JAVA)) {
            if (context.getJavaSourceCompatibility() == null || context.getJavaSourceCompatibility().isEmpty()) {
                context.setJavaSourceCompatibility(NewProjectTemplateVariables.VAR__JAVA__VERSION.getDefaultValue());
            }
        }
        boolean detailsNecessary = selectedTemplate.hasFeature(Features.NEW_PROJECT__TYPE_MULTI_PROJECT);
        IWizardPage nextPage = getNextPage();
        if (nextPage instanceof EGradleNewProjectWizardTemplateDetailsPage) {
            EGradleNewProjectWizardTemplateDetailsPage detailsPage = (EGradleNewProjectWizardTemplateDetailsPage) nextPage;
            detailsPage.setPageComplete(!detailsNecessary);
        }
        infoText.setText(info.toString());
        setPageComplete(true);
    }

}
