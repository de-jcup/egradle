package de.jcup.egradle.eclipse.ide.wizards;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import de.jcup.egradle.eclipse.ide.IDEUtil;

public class EGradleNewProjectWizardMainPage extends WizardNewProjectCreationPage {

	public EGradleNewProjectWizardMainPage() {
		super("main");

		setTitle("Create a Gradle Project");
		setImageDescriptor(IDEUtil.createImageDescriptor("icons/egradle-banner_64x64.png"));
		setDescription("Enter project name and location.");

	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
	}

}
