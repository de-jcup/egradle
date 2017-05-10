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
 package de.jcup.egradle.core.util;

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