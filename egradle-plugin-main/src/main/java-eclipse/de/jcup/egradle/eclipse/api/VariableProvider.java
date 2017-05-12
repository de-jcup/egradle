package de.jcup.egradle.eclipse.api;

import java.util.Map;

/**
 * The provider simply proves a map containing data for current variables.
 * 
 * @author Albert Tregnaghi
 *
 */
public interface VariableProvider {

	/**
	 * Returns a map containing variable keys as key element, values as value.
	 * E.g. "rootProject.projectDir" is the key and
	 * "/usr/home/dev/projects/myGradleProject" is the value
	 * 
	 * @return unmodifiable map or <code>null</code>
	 */
	Map<String, String> getVariables();

}
