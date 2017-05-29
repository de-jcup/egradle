package de.jcup.egradle.eclipse.ide.wizards;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.ui.SWTFactory;
import de.jcup.egradle.template.Features;
import de.jcup.egradle.template.FileStructureTemplate;

public class EGradleNewProjectWizardTemplateDetailsPage extends WizardPage {

	private NewProjectContext context;
	private Group multiProjectGroup;
	private Composite composite;
	private Text multiProjectNamesText;
	private Group javaGroup;
	private Text javaSourceCompatibility;

	public EGradleNewProjectWizardTemplateDetailsPage(NewProjectContext context) {
		super("templateDetails");
		this.context = context;
		setTitle("Gradle template details");
		setImageDescriptor(IDEUtil.createImageDescriptor("icons/egradle-banner_64x64.png"));
		setDescription("Setup details of used template");
	}

	@Override
	public void createControl(Composite parent) {
		composite = SWTFactory.createComposite(parent, 1, SWT.FILL,SWT.FILL);

        initializeDialogUnits(parent);

        /* FIXME ATR, 24.05.2017:  check implementation and ui parts  */
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
        // IIDEHelpContextIds.NEW_PROJECT_WIZARD_PAGE);

		initializeDialogUnits(parent);

		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		initMultiProjectParts(composite);
		initJavaParts(composite);
		
		multiProjectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// Show description on opening
		setErrorMessage(null);
		setMessage(null);
		setControl(composite);
		
		Dialog.applyDialogFont(composite);

		updateUI();
	}

	private void initMultiProjectParts(Composite composite) {
		multiProjectGroup = SWTFactory.createGroup(composite, "Multi project", 1, SWT.FILL, SWT.FILL);

		SWTFactory.createLabel(multiProjectGroup, "Please enter sub project name(s). Use comma to separate", SWT.FILL);
		multiProjectNamesText = SWTFactory.createSingleText(multiProjectGroup, 1);
	
		multiProjectNamesText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
	
	}
	
	private void initJavaParts(Composite composite) {
		javaGroup = SWTFactory.createGroup(composite, "Java", 1, SWT.FILL, SWT.FILL);

		SWTFactory.createLabel(javaGroup, "Please enter source compatibility level (e.g. 1.8)", SWT.FILL);
		javaSourceCompatibility = SWTFactory.createSingleText(javaGroup, 1);
		javaSourceCompatibility.setText("1.8");
	
		javaSourceCompatibility.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
	
	}

	private boolean validatePage() {
		FileStructureTemplate selectedTemplate = context.getSelectedTemplate();
		if (selectedTemplate==null){
			return false;
		}
		if (isMultiProject(selectedTemplate)){
			if (StringUtils.isBlank(multiProjectNamesText.getText())){
				return false;
			}
		}
		if (isSupportingJava(selectedTemplate)){
			if (StringUtils.isBlank(javaSourceCompatibility.getText())){
				return false;
			}
		}
			
		return true;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
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
		setDescription("Define details for " + selectedTemplate.getName());
		multiProjectGroup.setVisible(isMultiProject(selectedTemplate));
		javaGroup.setVisible(isSupportingJava(selectedTemplate));
		
		setPageComplete(validatePage());

	}

	private boolean isSupportingJava(FileStructureTemplate selectedTemplate) {
		return selectedTemplate.hasFeature(Features.NEW_PROJECT__SUPPORTS_JAVA);
	}

	private boolean isMultiProject(FileStructureTemplate selectedTemplate) {
		return selectedTemplate.hasFeature(Features.NEW_PROJECT__TYPE_MULTI_PROJECT);
	}

}
