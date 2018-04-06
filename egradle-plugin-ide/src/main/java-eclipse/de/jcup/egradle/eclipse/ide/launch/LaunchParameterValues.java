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
package de.jcup.egradle.eclipse.ide.launch;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.IParameterValues;
import org.eclipse.debug.core.ILaunch;

import de.jcup.egradle.eclipse.util.EGradlePostBuildJob;

public class LaunchParameterValues implements IParameterValues {

	private Map<Object, Object> map = new HashMap<>();
	private ILaunch launch;
	private EGradlePostBuildJob postJob;
	private String overriddenTasks;

	@Override
	public Map<Object, Object> getParameterValues() {
		return map;
	}

	/**
	 * Set launch to use in command
	 * 
	 * @param launch
	 *            launch instance to use in command
	 */
	public void setLaunch(ILaunch launch) {
		this.launch = launch;
	}

	public ILaunch getLaunch() {
		return launch;
	}

	/**
	 * Set tasks to be overridden (optional)
	 * 
	 * @param tasks
	 */
	public void setOverriddenTasks(String tasks) {
		this.overriddenTasks = tasks;
	}

	public String getOverriddenTasks() {
		return overriddenTasks;
	}

	public void setPostJob(EGradlePostBuildJob postJob) {
		this.postJob = postJob;
	}

	public EGradlePostBuildJob getPostJob() {
		return postJob;
	}

	/**
	 * Reset launch data. Does NOT change parameter values!
	 */
	public void resetLaunchData() {
		this.launch = null;
		this.postJob = null;
		this.overriddenTasks = null;
	}

}
