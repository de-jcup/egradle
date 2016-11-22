package de.jcup.egradle.eclipse.gradleeditor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;

public class OpenQuickOutlineHandler extends AbstractHandler {

	public static final String COMMAND_ID = "egradle.editor.commands.quickoutline";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		/* FIXME ATR, 22.11.2016: make more failure resistant*/
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		if (editor instanceof GradleEditor){
			GradleEditor ge = (GradleEditor) editor;
			ge.openQuickOutline();
		}
		return null;
	}


}
