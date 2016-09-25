package de.jcup.egradle.eclipse.preferences;

import static org.apache.commons.lang3.Validate.*;

import de.jcup.egradle.core.process.EGradleShellType;

public enum EGradleCallType{
	/* Linux/Mac etc.*/
	LINUX_GRADLE_WRAPPER("linux.gradlewrapper", "./gradlew"),
	LINUX_GRADLE_INSTALLED("linux.installed","gradle"),
	/* Windows*/
	WINDOWS_GRADLE_WRAPPER("windows.gradlewrapper", "gradlew.bat",EGradleShellType.CMD,""),
	WINDOWS_GRADLE_INSTALLED("windows.installed","gradle.bat"),
	/* Custom */
	CUSTOM("custom","");
	
	private String id;
	private String defaultGradleCommand;
	private EGradleShellType defaultGradleShell;
	private String defaultGradleBinFolder;
	
	EGradleCallType(String id, String defaultGradleCommand){
		this(id,defaultGradleCommand,EGradleShellType.NONE,"");
	}
	
	EGradleCallType(String id, String defaultGradleCommand, EGradleShellType shell, String defaultGradleBinFolder){
		notNull(id, "id may not be null");
		notNull(defaultGradleCommand, "defaultGradleCommand may not be null");
		notNull(defaultGradleBinFolder, "id may not be null");
		
		this.id=id;
		this.defaultGradleCommand=defaultGradleCommand;
		this.defaultGradleShell=shell;
		this.defaultGradleBinFolder=defaultGradleBinFolder;
	}
	
	public String getId() {
		return id;
	}
	
	public String getDefaultGradleCommand() {
		return defaultGradleCommand;
	}

	/**
	 * Returns {@link EGradleShellType} or <code>null</code>
	 * @return type or <code>null</code>
	 */
	public EGradleShellType getDefaultShell() {
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