package de.jcup.egradle.eclipse.junit.contribution;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class JunitUtil {

	public static void logError(String error, Throwable t) {
		getLog().log(new Status(IStatus.ERROR, JUnitActivator.PLUGIN_ID, error, t));
	}

	private static ILog getLog() {
		ILog log = JUnitActivator.getDefault().getLog();
		return log;
	}
}
