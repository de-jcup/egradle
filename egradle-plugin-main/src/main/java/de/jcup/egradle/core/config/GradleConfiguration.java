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
package de.jcup.egradle.core.config;

import de.jcup.egradle.core.process.EGradleShellType;
import de.jcup.egradle.core.process.ProcessConfiguration;

public interface GradleConfiguration extends ProcessConfiguration {

	/**
	 * Returns the shell to use - if wanted
	 * @return shell or <code>null</code>
	 */
	public EGradleShellType getShellType();

	/**
	 * Returns gradle installation bin directory - if available
	 * @return gradle installation bin directory or <code>null</code>
	 */
	public String getGradleBinDirectory();
	
	
	/**
	 * Return special java home - if available
	 * @return special jave home to set into enviro
	 */
	public String getJavaHome();
	
	/**
	 * Returns the gradle command - with full path if necesary
	 * @return full path featured gradle command
	 */
	public String getGradleCommandFullPath();
	

}
