package de.jcup.egradle.eclipse.ide.wizards;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import de.jcup.egradle.eclipse.ide.EGradleMessageDialogSupport;
import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.util.EclipseResourceHelper;
import de.jcup.egradle.ide.NewProjectContext;
import de.jcup.egradle.template.FileStructureTemplate;

public class EGradleNewProjectWizard extends Wizard implements INewWizard {

	private EGradleNewProjectWizardMainPage mainPage;
	private EGradleNewProjectWizardSelectTemplatePage templateSelectionPage;
	private EGradleNewProjectWizardTemplateDetailsPage templateDetailsPage;
	private NewProjectContext context;

	public EGradleNewProjectWizard() {
		context = new NewProjectContext();

		setWindowTitle("New Gradle Project");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {

		mainPage = new EGradleNewProjectWizardMainPage(context);
		templateSelectionPage = new EGradleNewProjectWizardSelectTemplatePage(context);
		templateDetailsPage = new EGradleNewProjectWizardTemplateDetailsPage(context);

		addPage(mainPage);
		addPage(templateSelectionPage);
		addPage(templateDetailsPage);
	}

	@Override
	public boolean performFinish() {
		showProblem(null);
		boolean valid = context.validate();
		String errorMessage = context.getLastValidationProblem();

		if (!valid) {
			showProblem(errorMessage);
			return false;
		}
		FileStructureTemplate template = context.getSelectedTemplate();

		try {
			File locationFolder = EclipseResourceHelper.DEFAULT.toFile(mainPage.getLocationPath());
			File targetFolder = new File(locationFolder,context.getProjectName()); 
			if (! targetFolder.exists()){
				targetFolder.mkdirs();
			}
			Properties properties = context.toProperties();

			template.applyTo(targetFolder, properties);
			/* FIXME ATR, 30.05.2017: import is missing of the generated project! */
			/* FIXME ATR, 30.05.2017: multi projects needs subproject name + loop for calling! */
			/* FIXME ATR, 30.05.2017: the detail page makes currently problems - shows no java group when multiprojects also visible */
			return true;
		} catch (Exception e) {
			IDEUtil.logError("Was not able to apply template:" + template.getName(), e);
			EGradleMessageDialogSupport.INSTANCE.showError("Was not able to apply template. See error log for details");
			return false;
		}

	}

	private void showProblem(String errorMessage) {
		for (IWizardPage page : getPages()) {
			if (page instanceof DialogPage) {
				DialogPage dpage = (DialogPage) page;
				dpage.setErrorMessage(errorMessage);
			}
		}
	}

}
