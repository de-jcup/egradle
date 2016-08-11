package de.jcup.egradle.eclipse.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.jcup.egradle.eclipse.Activator;

public class EGradlePreferences {
	
	public static enum PreferenceConstants{
		P_ROOTPROJECT_PATH("pathGradleRootProject"),
		P_JAVA_HOME_PATH ("pathJavaHome");

		private String id;
		
		private PreferenceConstants(String id){
			this.id=id;
		}
		
		public String getId() {
			return id;
		}
		

	}

	
	public static EGradlePreferences PREFERENCES = new EGradlePreferences();
	private IPreferenceStore store; 
	
	EGradlePreferences() {
		store =new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);
	}

	
	public String getStringPreference(PreferenceConstants id) {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		String result = prefs.get(id.getId(),"");
		return result;
	}


	public IPreferenceStore getPreferenceStore() {
		return store;
	}
}
