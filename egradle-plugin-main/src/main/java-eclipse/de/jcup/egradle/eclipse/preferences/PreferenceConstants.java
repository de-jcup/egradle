package de.jcup.egradle.eclipse.preferences;

public enum PreferenceConstants {
	P_ROOTPROJECT_PATH("pathGradleRootProject"), 
	
	P_JAVA_HOME_PATH("pathJavaHome"),
	
	P_GRADLE_CALL_TYPE("gradleCallType"),
	P_GRADLE_SHELL("commandShell"),
	P_GRADLE_INSTALL_BIN_FOLDER("pathGradleInstallation"),
	P_GRADLE_CALL_COMMAND("commandGradle");

	private String id;

	private PreferenceConstants(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

}