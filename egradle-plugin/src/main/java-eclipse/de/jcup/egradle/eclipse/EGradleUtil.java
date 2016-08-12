package de.jcup.egradle.eclipse;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.debug.ui.IJavaDebugUIConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class EGradleUtil {

	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		Shell shell = null;
		if (window!=null){
			shell = window.getShell();
		}
		return shell;
	}

	public static void log(Throwable t) {
		if (t instanceof CoreException) {
			log(new Status(IStatus.ERROR, getUniqueIdentifier(), IStatus.ERROR, t.getMessage(), t.getCause()));
		} else {
			log(new Status(IStatus.ERROR, getUniqueIdentifier(), IJavaDebugUIConstants.INTERNAL_ERROR, "Internal Error", t));   
		}
		
	}

	public static void log(Status status) {
		Activator.getDefault().getLog().log(status);
	}
	private static String getUniqueIdentifier() {
		return "EGradle";
	}
	
	static boolean isUIThread()
	{
	    if (Display.getCurrent()==null){
	    	return false;
	    }
	    return true;
	}
}
