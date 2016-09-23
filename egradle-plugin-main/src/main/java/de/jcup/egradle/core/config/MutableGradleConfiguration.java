package de.jcup.egradle.core.config;

public class MutableGradleConfiguration implements GradleConfiguration{

	private String shellCommand;
	private String gradleCommand;
	private String gradleInstallDirectory;

	@Override
	public String getShellCommand() {
		if (shellCommand==null){
			shellCommand="";
		}
		return shellCommand;
	}
	
	public void setShellCommand(String shellCommand) {
		this.shellCommand = shellCommand;
	}

	@Override
	public String getGradleCommand() {
		if (gradleCommand==null) {
			gradleCommand="";
		}
		return gradleCommand;
	}
	
	public void setGradleCommand(String gradleCommand) {
		this.gradleCommand = gradleCommand;
	}

	@Override
	public String getGradleInstallDirectory() {
		if (gradleInstallDirectory==null){
			gradleInstallDirectory="";
		}
		return gradleInstallDirectory;
	}
	
	public void setGradleInstallDirectory(String gradleInstallDirectory) {
		this.gradleInstallDirectory = gradleInstallDirectory;
	}

}
