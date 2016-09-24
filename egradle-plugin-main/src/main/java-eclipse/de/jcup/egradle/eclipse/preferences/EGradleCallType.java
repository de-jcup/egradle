package de.jcup.egradle.eclipse.preferences;

import static org.apache.commons.lang3.Validate.*;

public enum EGradleCallType{
	/* Linux/Mac etc.*/
	LINUX_GRADLE_WRAPPER("linux.gradlewrapper", "./gradlew"),
	LINUX_GRADLE_INSTALLED("linux.installed","gradle"),
	/* Windows*/
	WINDOWS_GRADLE_WRAPPER("windows.gradlewrapper", "gradlew.bat"),
	WINDOWS_GRADLE_INSTALLED("windows.installed","gradle.bat"),
	/* Custom */
	CUSTOM("custom","");
	
	private String id;
	private String defaultGradleCommand;
	private String defaultGradleShell;
	private String defaultGradleBinFolder;
	
	EGradleCallType(String id, String defaultGradleCommand){
		this(id,defaultGradleCommand,"","");
	}
	
	EGradleCallType(String id, String defaultGradleCommand, String defaultGradleShell, String defaultGradleBinFolder){
		notNull(id, "id may not be null");
		notNull(defaultGradleCommand, "defaultGradleCommand may not be null");
		notNull(defaultGradleShell, "id may not be null");
		notNull(defaultGradleBinFolder, "id may not be null");
		
		this.id=id;
		this.defaultGradleCommand=defaultGradleCommand;
		this.defaultGradleShell=defaultGradleShell;
		this.defaultGradleBinFolder=defaultGradleBinFolder;
	}
	
	public String getId() {
		return id;
	}
	
	public String getDefaultGradleCommand() {
		return defaultGradleCommand;
	}

	public String getDefaultGradleShell() {
		return defaultGradleShell;
	}

	public String getDefaultGradleBinFolder() {
		return defaultGradleBinFolder;
	}
	
	public static EGradleCallType findById(String id){
		for (EGradleCallType c: values()){
			if (c.getId().equals(id)){
				return c;
			}
		}
		return null;
	}
}