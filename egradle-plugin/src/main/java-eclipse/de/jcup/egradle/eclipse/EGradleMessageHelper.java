package de.jcup.egradle.eclipse;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class EGradleMessageHelper {
	
	public static final EGradleMessageHelper INSTANCE = new EGradleMessageHelper();

	public void showWarning(String message) {
		Shell shell = getShell();
		MessageDialog.openWarning(shell, "Egradle",	message);
		
	}
	
	public void showError(String message) {
		Shell shell = getShell();
		MessageDialog.openError(shell, "Egradle",	message);
		
	}

	private Shell getShell() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		Shell shell = window.getShell();
		return shell;
	}

}
