package de.jcup.egradle.eclipse.ide.wizards;

import static de.jcup.egradle.eclipse.ui.SWTUtil.*;

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
import de.jcup.egradle.ide.NewProjectContext;
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
		
		SWTFactory.createLabel(commonGroup, "Please enter group name - if empty project name will be used", SWT.FILL);
		gradleGroupNameText = SWTFactory.createSingleText(commonGroup, 1);
		gradleGroupNameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
	}

	private void initMultiProjectParts(Composite composite) {
		multiProjectGroup = SWTFactory.createGroup(composite, "Multi project", 1, SWT.FILL, SWT.FILL);
		multiProjectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
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
		javaGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		
		SWTFactory.createLabel(javaGroup, "Please enter source compatibility level", SWT.FILL);
		javaSourceCompatibilityText = SWTFactory.createSingleText(javaGroup, 1);
		javaSourceCompatibilityText.setText("1.8");
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
		FileStructureTemplate selectedTemplate = context.getSelectedTemplate();
		if (selectedTemplate == null) {
			return false;
		}
		/*
		 * set context with current values - no matter if valid or not, or
		 * feature is enabled.
		 */
		String groupName = gradleGroupNameText.getText();
		String multiProjects = multiProjectNamesText.getText();
		String javaSourceCompatibility = javaSourceCompatibilityText.getText();
		String javaHome = javaHomeText.getText();
		
		context.setJavaHome(javaHome);
		context.setGroupName(groupName);
		context.setMultiProjects(multiProjects);
		context.setJavaSourceCompatibility(javaSourceCompatibility);

		if (!context.validateMultiProject()) {
			return false;
		}
		if (!context.validateJavaSupport()) {
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
		setDescription("Define details for " + selectedTemplate.getName());

		showControl(multiProjectGroup, context.isMultiProject());
		showControl(javaGroup, context.isSupportingJava());
		setPageComplete(validatePage());
	}

}