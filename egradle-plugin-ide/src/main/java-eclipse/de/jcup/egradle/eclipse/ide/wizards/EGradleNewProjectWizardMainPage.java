package de.jcup.egradle.eclipse.ide.wizards;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.util.EclipseResourceHelper;
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
		if (!super.validatePage()){
			return false;
		}
		String projectName = getProjectName();
		try{
			File parentFolder = EclipseResourceHelper.DEFAULT.toFile(getLocationPath());
			File target= new File(parentFolder,projectName);
			if (target.exists()){
				if (! target.isDirectory()){
					setErrorMessage("The wanted project folder exists already as a file at this location! So project cannot be created");
					return false;
				}
				if (target.listFiles().length>1){
					setErrorMessage("There exists already a folder containing files at this location! So project cannot be created");
					return false;
				}
			}
		}catch(CoreException e){
			IDEUtil.logError("Was not able to get location path for create project", e);
			setErrorMessage("Location path problem:"+e.getMessage());
			return false;
		}
		context.setProjectName(projectName);
		
		return true;
	}

}
