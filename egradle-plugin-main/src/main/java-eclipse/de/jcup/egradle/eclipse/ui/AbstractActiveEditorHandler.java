package de.jcup.egradle.eclipse.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public abstract class AbstractActiveEditorHandler extends AbstractHandler {

	public AbstractActiveEditorHandler() {
		super();
	}

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
		
		executeOnActiveEditor(editor);
		return null;
	}

	/**
	 * Executes on a editor part. Execution implementation should inspect by instanceof... before doing execution 
	 * @param editor
	 */
	protected abstract void executeOnActiveEditor(IEditorPart editor);

}