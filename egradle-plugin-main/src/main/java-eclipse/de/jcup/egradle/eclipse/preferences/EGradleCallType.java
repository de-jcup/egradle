/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
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