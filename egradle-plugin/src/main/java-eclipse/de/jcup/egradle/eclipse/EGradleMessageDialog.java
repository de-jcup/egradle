package de.jcup.egradle.eclipse;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import static de.jcup.egradle.eclipse.EGradleUtil.*;

public class EGradleMessageDialog {
	
	public static final EGradleMessageDialog INSTANCE = new EGradleMessageDialog();

	public void showWarning(String message) {
		Display.getDefault().asyncExec(new Runnable(){

			@Override
			public void run() {
				Shell shell = getActiveWorkbenchShell();
				MessageDialog.openWarning(shell, "Egradle",	message);
			}
			
		});
		
	}
	
	public void showError(String message) {
		Display.getDefault().asyncExec(new Runnable(){

			@Override
			public void run() {
				Shell shell = getActiveWorkbenchShell();
				MessageDialog.openError(shell, "Egradle",	message);
			}
			
		});
	
		
	}

	
	

}
