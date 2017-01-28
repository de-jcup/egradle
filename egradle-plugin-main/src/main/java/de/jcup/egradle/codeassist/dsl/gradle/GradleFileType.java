package de.jcup.egradle.codeassist.dsl.gradle;

/* FIXME ATR, 20.01.2017: implement all types (INTIAL, BUILDSCRIPT, SETTINGS) and setup in editor on startup time and use it inside code completion! */
/**
 * See types of gradle scripts at <a href="https://docs.gradle.org/current/dsl/">Gradle Build Language Reference</a>
 * @author Albert Tregnaghi
 *
 */
public enum GradleFileType{
	GRADLE_BUILD_SCRIPT("org.gradle.api.Project"), 
	GRADLE_INIT_SCRIPT("org.gradle.api.invocation.Gradle"),
	GRADLE_SETTINGS_SCRIPT("org.gradle.api.initialization.Settings"),
	
	UNKNOWN("unknown");

	private String rootType;

	GradleFileType(String rootType){
		this.rootType=rootType;
	}
	
	public String getRootType() {
		return rootType;
	}
}