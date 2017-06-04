package de.jcup.egradle.eclipse.ide.wizards;

import static de.jcup.egradle.eclipse.ide.IDEUtil.*;
import static de.jcup.egradle.ide.NewProjectTemplateVariables.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;

import de.jcup.egradle.eclipse.ide.EGradleMessageDialogSupport;
import de.jcup.egradle.eclipse.ide.ui.RootProjectConfigMode;
import de.jcup.egradle.eclipse.util.EclipseResourceHelper;
import de.jcup.egradle.eclipse.util.EclipseUtil;
import de.jcup.egradle.ide.NewProjectContext;
import de.jcup.egradle.ide.NewProjectTemplateVariables;
import de.jcup.egradle.ide.TemplateVariable;
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

		try {
			File locationFolder = EclipseResourceHelper.DEFAULT.toFile(mainPage.getLocationPath());
			File targetFolder = new File(locationFolder, context.getProjectName());

			getContainer().run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						createNewProjectAndImport(targetFolder, monitor);
					} catch (Exception e) {
						throw new InvocationTargetException(e);
					}

				}
			});
			return true;
		} catch (Exception e) {
			logError("EGradle new project creation failed", e);
			return false;
		}

	}

	protected void createNewProjectAndImport(File targetFolder, IProgressMonitor monitor) {
		/*
		 * FIXME ATR, 30.05.2017: multi projects needs subproject name + loop
		 * for calling!
		 */
		/*
		 * FIXME ATR, 04.06.2017: src/main/res and src/test/res is not part of
		 * classpath for java templates ?!?!?
		 */
		FileStructureTemplate template = context.getSelectedTemplate();
		FileStructureTemplate gradleWrapperTemplate = getGradleWrapperTemplate();

		showConsoleView();
		openSystemConsole(true);

		try {

			outputToSystemConsole("Start creating project by template:" + template.getName());

			if (context.isMultiProject()) {
				List<String> subProjectNames = context.getMultiProjectsAsList();
				String multiProjectsIncludeString = context.getMultiProjectsAsIncludeString();

				Map<Object, Object> additionalProperties = new HashMap<>();
				setVariable(additionalProperties, VAR__MULTIPROJECTS__INCLUDE_SUBPROJECTS, multiProjectsIncludeString);

				for (String subProjectName : subProjectNames) {
					setVariable(additionalProperties, VAR__NAME_OF_SUBPROJECT, subProjectName);
					createProject(targetFolder, template, gradleWrapperTemplate, additionalProperties);
				}
			} else {
				createProject(targetFolder, template, gradleWrapperTemplate, null);
			}
			importCreatedProject(targetFolder);

		} catch (Exception e) {
			outputToSystemConsole("FAILED]");

			logError("Was not able to apply template:" + template.getName(), e);
			EGradleMessageDialogSupport.INSTANCE.showError("Was not able to apply template. See error log for details");
		}

	}

	private void setVariable(Map<Object, Object> map, TemplateVariable variable, String value) {
		map.put(variable.getVariableName(), value);
	}

	private void createProject(File targetFolder, FileStructureTemplate template,
			FileStructureTemplate gradleWrapperTemplate, Map<Object, Object> additionalProperties) throws IOException {
		if (context.isSupportingGradleWrapper()) {
			if (gradleWrapperTemplate == null) {
				throw new IllegalStateException(
						"Not able to provide template - gradle wrapper necessary, but not available from IDE");
			}
		}
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		Properties properties = context.toProperties();
		if (additionalProperties != null) {
			properties.putAll(additionalProperties);
		}
		String projectName = null;
		if (context.isMultiProject()){
			projectName = properties.getProperty(NewProjectTemplateVariables.VAR__NAME_OF_SUBPROJECT.getVariableName());
		}else{
			projectName=context.getProjectName();
		}
		outputToSystemConsole("- creating project '" + projectName + "' at:" + targetFolder.getAbsolutePath());
		template.applyTo(targetFolder, properties);
		outputToSystemConsole("  + added project content");
		if (context.isSupportingGradleWrapper()) {
			gradleWrapperTemplate.applyTo(targetFolder, properties);
			outputToSystemConsole("  + added gradle wrapper parts");
		}

	}

	private void importCreatedProject(File targetFolder) throws CoreException {
		/* open import wizard with correct setup */

		IWizardDescriptor descriptor = PlatformUI.getWorkbench().getImportWizardRegistry()
				.findWizard(EGradleRootProjectImportWizard.ID);
		if (descriptor == null) {
			throw new IllegalStateException("Import was not possible, because of missing wizard!");
		}

		IWizard wizard = descriptor.createWizard();
		if (!(wizard instanceof EGradleRootProjectImportWizard)) {
			throw new IllegalStateException("Not a root project wizard but:" + wizard);
		}

		outputToSystemConsole("- start import wizard for generated project");

		EGradleRootProjectImportWizard piw = (EGradleRootProjectImportWizard) wizard;
		piw.setCustomRootProjectpath(targetFolder.getAbsolutePath());
		piw.setImportMode(RootProjectConfigMode.PREDEFINED_VALUES);

		WizardDialog wd = new WizardDialog(getShell(), piw);
		wd.setTitle("Import generated gradle project");

		EclipseUtil.safeAsyncExec(new Runnable() {

			@Override
			public void run() {
				int result = wd.open();
				if (MessageDialog.OK == result) {
					outputToSystemConsole("[DONE]");
				} else {
					outputToSystemConsole("[CANCELED]");
				}
			}
		});
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
