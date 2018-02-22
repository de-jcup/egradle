package de.jcup.egradle.eclipse.ide.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.jcup.egradle.core.domain.GradleProjectException;
import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.util.StringUtilsAccess;
import de.jcup.egradle.eclipse.ide.EGradleMessageDialogSupport;
import de.jcup.egradle.eclipse.ide.IDEUtil;

public class NewGradleSubProjectHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		EGradleMessageDialogSupport dialogSupport = EGradleMessageDialogSupport.INSTANCE;
		GradleRootProject rootProject = IDEUtil.getRootProject(true);
		if (! rootProject.isMultiProject()){
			dialogSupport.showError("Cannot add a subproject to a single project!");
			return null;
		}
		String nameOfNewSubProject = dialogSupport.showInputDialog("Enter name of new sub project inside '"+rootProject.getName()+"'", "New Gradle sub project");
		if (StringUtilsAccess.isBlank(nameOfNewSubProject)){
			return null;
		}
		try{
			rootProject.createNewSubProject(nameOfNewSubProject);
		}catch(GradleProjectException e){
			throw new ExecutionException("Was not able to create sub project:"+nameOfNewSubProject, e);
		}
		
		/* trigger reimport*/
		ReimportGradleProjectHandler handler = new ReimportGradleProjectHandler();
		handler.execute(event);
		return null;
	}


}
