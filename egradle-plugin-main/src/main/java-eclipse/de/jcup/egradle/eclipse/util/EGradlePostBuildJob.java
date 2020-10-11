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
package de.jcup.egradle.eclipse.util;

import org.eclipse.core.runtime.jobs.Job;

import de.jcup.egradle.core.util.BuildInfo;

public abstract class EGradlePostBuildJob extends Job {

    private BuildInfo buildInfo;

    public EGradlePostBuildJob(String name) {
        super(name);
    }

    /**
     * Set build information about former build
     * 
     * @param info information to set
     */
    public void setBuildInfo(BuildInfo info) {
        this.buildInfo = info;
    }

    /**
     * Returns <code>true</code> when build info about former build is available
     * 
     * @return <code>true</code> when build info about former build is available
     */
    public boolean hasBuildInfo() {
        return buildInfo != null;
    }

    public BuildInfo getBuildInfo() {
        return buildInfo;
    }

}
