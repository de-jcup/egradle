package de.jcup.egradle.eclipse.gradleeditor;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorPreferences;

public class EditorUtil {

	public static GradleEditorPreferences getPreferences() {
		return GradleEditorPreferences.getInstance();
	}

	public static void logInfo(String info) {
		getLog().log(new Status(IStatus.INFO, EditorActivator.PLUGIN_ID, info));
	}

	public static void logWarning(String warning) {
		getLog().log(new Status(IStatus.WARNING, EditorActivator.PLUGIN_ID, warning));
	}
	
	public static void logError(String error, Throwable t) {
		getLog().log(new Status(IStatus.ERROR, EditorActivator.PLUGIN_ID, error,t));
	}

	private static ILog getLog() {
		ILog log = EditorActivator.getDefault().getLog();
		return log;
	}
}
