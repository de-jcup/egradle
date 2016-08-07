package de.jcup.egradle.core.config;

public class AlwaysBashWithGradleWrapperConfiguration implements GradleConfiguration {

	@Override
	public String getShellForGradleWrapper() {
		return "bash";
	}

	@Override
	public boolean isUsingGradleWrapper() {
		return true;
	}

}
