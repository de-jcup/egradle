package de.jcup.egradle.eclipse.ide.wizards;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.ide.NewProjectContext;

public class EGradleNewProjectWizardMainPage extends WizardNewProjectCreationPage {

	private NewProjectContext context;

	public EGradleNewProjectWizardMainPage(NewProjectContext context) {
		super("main");
		this.context=context;
		setTitle("Create a Gradle Project");
		setImageDescriptor(IDEUtil.createImageDescriptor("icons/egradle-banner_64x64.png"));
		setDescription("Enter project name and location.");

	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
	}
	
	@Override
	protected boolean validatePage() {
		context.setProjectName(getProjectName());
		return super.validatePage();
	}

}
