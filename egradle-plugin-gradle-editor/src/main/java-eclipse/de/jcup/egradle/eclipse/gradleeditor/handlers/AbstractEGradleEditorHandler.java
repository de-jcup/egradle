package de.jcup.egradle.eclipse.gradleeditor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;

public abstract class AbstractEGradleEditorHandler extends AbstractHandler {

	public AbstractEGradleEditorHandler() {
		super();
	}

	/**
	 * Execute something by using gradle editor instance
	 * @param gradleEditor - never <code>null</code>
	 */
	protected abstract void executeOnGradleEditor(GradleEditor gradleEditor);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench==null){
			return null;
		}
		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow==null){
			return null;
		}
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		if (activePage==null){
			return null;
		}
		IEditorPart editor = activePage.getActiveEditor();
		
		if (editor instanceof GradleEditor){
			executeOnGradleEditor((GradleEditor) editor);
		}
		return null;
	}

}