package de.jcup.egradle.core.domain;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.jcup.egradle.core.config.GradleConfiguration;


/**
 * Contains information about gradle parts. E.g. root project, system properties etc.
 *  @author Albert Tregnaghi
 *
 */
public class GradleContext {

	private GradleRootProject rootProject;
	
	private Map<String,String> properties = new HashMap<>();
	private Map<String,String> environment= new HashMap<>();
	private GradleCommand[] commands;
	private GradleConfiguration configuration;

	public int amountOfWorkToDo=1;
	
	public GradleContext(GradleRootProject rootProject, GradleConfiguration configuration){
		notNull(rootProject,"root project may not be null!");
		notNull(configuration, "'configuration' may not be null");
		this.rootProject=rootProject;
		this.configuration=configuration;
	}
	
	
	public int getAmountOfWorkToDo() {
		return amountOfWorkToDo;
	}
	
	public GradleCommand[] getCommands() {
		if (commands==null){
			commands= new GradleCommand[]{};
		}
		return commands;
	}
	
	public String getCommandString() {
		return Arrays.asList(getCommands()).toString();
	}
	
	public GradleConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Returns environment - e.g for setting a JAVA_HOME...
	 * @return environment environment or <code>null</code> if not set
	 */
	public Map<String,String> getEnvironment() {
		if (environment==null){
			return null;
		}
		return Collections.unmodifiableMap(environment);
	}
	
	public String getProperty(String key){
		return properties.get(key);
	}

	public GradleRootProject getRootProject() {
		return rootProject;
	}

	public void setAmountOfWorkToDo(int amountOfWorkToDo) {
		this.amountOfWorkToDo = amountOfWorkToDo;
	}
	
	public void setCommands(GradleCommand[] commands) {
		this.commands=commands;
	}

	public void setEnvironment(String key, String value){
		environment.put(key, value);
	}
	
	public void setProperty(String key, String value){
		properties.put(key, value);
	}
}
