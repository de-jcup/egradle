package de.jcup.egradle.core.process;

import java.util.Map;

public interface EnvironmentProvider {

	public Map<String, String> getEnvironment();
	
}
