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
	 * @param editorInput
	 *            input so provider can determine for which file the parameters
	 *            shall be supported.
	 * 
	 * @return unmodifiable map or <code>null</code>
	 */
	Map<String, String> getVariables(IEditorInput editorInput);

}
