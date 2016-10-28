/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
 package de.jcup.egradle.core.config;

import de.jcup.egradle.core.api.FileHelper;
import de.jcup.egradle.core.process.EGradleShellType;

public class MutableGradleConfiguration implements GradleConfiguration{
	
	private FileHelper fileHelper = FileHelper.DEFAULT;
	
	@Override
	public String getGradleCommandFullPath() {
		return fileHelper.createCorrectFilePath(gradleBinDirectory, gradleCommand);
	}
	
	@Override
	public String toString() {
		return "MutableGradleConfiguration [shellCommand=" + shellCommand + ", gradleCommand=" + gradleCommand
				+ ", gradleBinDirectory=" + gradleBinDirectory + ", workingDirectory=" + workingDirectory
				+ ", javaHome=" + javaHome + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gradleCommand == null) ? 0 : gradleCommand.hashCode());
		result = prime * result + ((gradleBinDirectory == null) ? 0 : gradleBinDirectory.hashCode());
		result = prime * result + ((javaHome == null) ? 0 : javaHome.hashCode());
		result = prime * result + ((shellCommand == null) ? 0 : shellCommand.hashCode());
		result = prime * result + ((workingDirectory == null) ? 0 : workingDirectory.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MutableGradleConfiguration other = (MutableGradleConfiguration) obj;
		if (gradleCommand == null) {
			if (other.gradleCommand != null)
				return false;
		} else if (!gradleCommand.equals(other.gradleCommand))
			return false;
		if (gradleBinDirectory == null) {
			if (other.gradleBinDirectory != null)
				return false;
		} else if (!gradleBinDirectory.equals(other.gradleBinDirectory))
			return false;
		if (javaHome == null) {
			if (other.javaHome != null)
				return false;
		} else if (!javaHome.equals(other.javaHome))
			return false;
		if (shellCommand == null) {
			if (other.shellCommand != null)
				return false;
		} else if (!shellCommand.equals(other.shellCommand))
			return false;
		if (workingDirectory == null) {
			if (other.workingDirectory != null)
				return false;
		} else if (!workingDirectory.equals(other.workingDirectory))
			return false;
		return true;
	}

	private EGradleShellType shellCommand;
	private String gradleCommand;
	private String gradleBinDirectory;
	private String workingDirectory;
	private String javaHome;

	@Override
	public EGradleShellType getShellType() {
		return shellCommand;
	}
	
	public void setShellCommand(EGradleShellType shell) {
		this.shellCommand = shell;
	}

	public void setGradleCommand(String gradleCommand) {
		this.gradleCommand = gradleCommand;
	}

	@Override
	public String getGradleBinDirectory() {
		if (gradleBinDirectory==null){
			gradleBinDirectory="";
		}
		return gradleBinDirectory;
	}
	
	public void setGradleBinDirectory(String gradleInstallDirectory) {
		this.gradleBinDirectory = gradleInstallDirectory;
	}

	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory=workingDirectory;
	}
	
	public String getWorkingDirectory() {
		return workingDirectory;
	}
	
	public void setJavaHome(String javaHome) {
		this.javaHome = javaHome;
	}
	
	@Override
	public String getJavaHome() {
		return javaHome;
	}

}
