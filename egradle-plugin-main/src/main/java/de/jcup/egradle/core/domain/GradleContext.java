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
package de.jcup.egradle.core.domain;

import static org.apache.commons.lang3.Validate.*;

import java.util.Map;
import java.util.TreeMap;

import de.jcup.egradle.core.config.GradleConfiguration;
import de.jcup.egradle.core.process.EnvironmentProvider;
import de.jcup.egradle.core.process.ProcessContext;

/**
 * Contains information about gradle parts. E.g. root project, system properties
 * etc.
 * 
 * @author Albert Tregnaghi
 *
 */
public class GradleContext implements EnvironmentProvider, ProcessContext{

	private GradleRootProject rootProject;

	/*
	 * we use tree map to have keys always automatically sorted - easier to
	 * debug and read
	 */
	private Map<String, String> environment = new TreeMap<>();
	private Map<String, String> systemProperties = new TreeMap<>();
	private Map<String, String> gradleProperties = new TreeMap<>();

	private GradleCommand[] commands;
	private GradleConfiguration configuration;

	public int amountOfWorkToDo = 1;

	private String[] options;

	private CancelStateProvider cancelStateProvider;

	public GradleContext(GradleRootProject rootProject, GradleConfiguration configuration) {
		notNull(rootProject, "root project may not be null!");
		notNull(configuration, "'configuration' may not be null");
		this.rootProject = rootProject;
		this.configuration = configuration;
	}

	public int getAmountOfWorkToDo() {
		return amountOfWorkToDo;
	}

	public GradleCommand[] getCommands() {
		if (commands == null) {
			commands = new GradleCommand[] {};
		}
		return commands;
	}

	public String getCommandString() {
		StringBuilder sb = new StringBuilder();
		GradleCommand[] x = getCommands();
		for (GradleCommand c: x){
			if (c==null){
				continue;
			}
			sb.append(c.toString());
			sb.append(" ");
		}
		return sb.toString();
	}

	public GradleConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Returns environment - e.g for setting a JAVA_HOME...
	 * 
	 * @return environment environment or <code>null</code> if not set
	 */
	public Map<String, String> getEnvironment() {
		return environment;
	}

	public GradleRootProject getRootProject() {
		return rootProject;
	}

	public void setAmountOfWorkToDo(int amountOfWorkToDo) {
		this.amountOfWorkToDo = amountOfWorkToDo;
	}

	public void setCommands(GradleCommand[] commands) {
		this.commands = commands;
	}

	public void setEnvironment(String key, String value) {
		environment.put(key, value);
	}

	/**
	 * Returns gradle parameters - will be used with -P option
	 * 
	 * @return gradle parameter map
	 */
	public Map<String, String> getGradleProperties() {
		return gradleProperties;
	}

	/**
	 * Returns system parameters - will be used with -D option
	 * 
	 * @return system parameter map
	 */
	public Map<String, String> getSystemProperties() {
		return systemProperties;
	}

	public void setOptions(String... options) {
		if (options == null) {
			options = new String[] {};
		}
		this.options = options;
	}

	/**
	 * Returns options string - never <code>null</code>
	 * 
	 * @return options
	 */
	public String[] getOptions() {
		if (options==null){
			options=new String[]{};
		}
		return options;
	}

	/**
	 * Registers new  cancel state provider - old one will be replaced
	 * @param provider
	 */
	public void register(CancelStateProvider provider) {
		this.cancelStateProvider = provider;
	}
	
	public CancelStateProvider getCancelStateProvider() {
		if (cancelStateProvider==null){
			cancelStateProvider=CancelStateProvider.NEVER_CANCELED;
		}
		return cancelStateProvider;
	}
}
