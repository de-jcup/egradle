package de.jcup.egradle.eclipse.ide.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import de.jcup.egradle.template.FileStructureTemplate;

public class EGradleNewProjectWizard extends Wizard implements INewWizard, NewProjectContext {

	private EGradleNewProjectWizardMainPage mainPage;
	private EGradleNewProjectWizardSelectTemplatePage templateSelectionPage;
	private EGradleNewProjectWizardTemplateDetailsPage templateDetailsPage;
	private FileStructureTemplate selectedTemplate;

	public EGradleNewProjectWizard() {
		setWindowTitle("New Gradle Project");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		mainPage = new EGradleNewProjectWizardMainPage(this);
		templateSelectionPage = new EGradleNewProjectWizardSelectTemplatePage(this);
		templateDetailsPage = new EGradleNewProjectWizardTemplateDetailsPage(this);

		addPage(mainPage);
		addPage(templateSelectionPage);
		addPage(templateDetailsPage);
	}

	@Override
	public boolean performFinish() {
		/* FIXME ATR, 18.05.2017: implement wizard complete */
		/*
		 * FIXME ATR, 18.05.2017: remove project type page and replace by
		 * template approach page
		 */
		/*
		 * FIXME ATR, 24.05.2017: SDK none installed. Fix this and also the
		 * failure message is not correct
		 */
		return false;
	}

	@Override
	public FileStructureTemplate getSelectedTemplate() {
		return selectedTemplate;
	}

	@Override
	public void setSelectedTemplate(FileStructureTemplate selectedTemplate) {
		this.selectedTemplate=selectedTemplate;
	}
}
