package de.jcup.egradle.eclipse.api;

import org.eclipse.core.runtime.jobs.Job;

import de.jcup.egradle.core.api.BuildInfo;

public abstract class EGradlePostBuildJob extends Job{


	private BuildInfo buildInfo;

	public EGradlePostBuildJob(String name) {
		super(name);
	}
	
	/**
	 * Set build information about former build
	 * @param info information to set
	 */
	public void setBuildInfo(BuildInfo info) {
		this.buildInfo=info;
	}
	
	/**
	 * Returns <code>true</code> when build info about former build is available
	 * @return <code>true</code> when build info about former build is available
	 */
	public boolean hasBuildInfo(){
		return buildInfo!=null;
	}
	
	public BuildInfo getBuildInfo() {
		return buildInfo;
	}

}
	