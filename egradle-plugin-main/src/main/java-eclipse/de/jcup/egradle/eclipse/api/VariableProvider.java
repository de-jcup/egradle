package de.jcup.egradle.eclipse.api;

import java.util.Map;

import org.eclipse.ui.IEditorInput;

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
	 * @param editorInput input so provider can determine for which file the 
	 * parameters shall be supported.
	 * 
	 * @return unmodifiable map or <code>null</code>
	 */
	Map<String, String> getVariables(IEditorInput editorInput);

}
