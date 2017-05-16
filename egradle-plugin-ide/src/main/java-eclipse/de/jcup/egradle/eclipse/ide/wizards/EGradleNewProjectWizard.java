package de.jcup.egradle.eclipse.ide.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class EGradleNewProjectWizard extends Wizard implements INewWizard{

	private EGradleNewProjectWizardMainPage mainPage;
	private EGradleNewProjectWizardProjectTypePage projectTypePage;

	public EGradleNewProjectWizard() {
		setWindowTitle("New Gradle Project");
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
	
	@Override
	public void addPages() {
		mainPage = new EGradleNewProjectWizardMainPage();
		projectTypePage = new EGradleNewProjectWizardProjectTypePage();
		addPage(mainPage);
		addPage(projectTypePage);
	}

	@Override
	public boolean performFinish() {
		/* FIXME ATR, 18.05.2017: implement wizard complete */
		/* FIXME ATR, 18.05.2017: remove project type page and replace by template approach page*/
		return false;
	}

}
