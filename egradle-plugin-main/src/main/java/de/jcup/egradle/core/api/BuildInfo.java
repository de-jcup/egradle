package de.jcup.egradle.core.api;

import de.jcup.egradle.core.process.ProcessExecutor;

public class BuildInfo {
	public BuildInfo(String buildDescription, int buildExitCode) {
		super();
		this.buildDescription = buildDescription;
		this.buildExitCode = buildExitCode;
	}

	private int buildExitCode;
	private String buildDescription;

	public String getBuildDescription() {
		return buildDescription;
	}

	public boolean hasBuildFailed() {
		return buildExitCode != ProcessExecutor.PROCESS_RESULT_OK;
	}
}