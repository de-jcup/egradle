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
package de.jcup.egradle.eclipse.ide;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import de.jcup.egradle.core.util.FileSupport;
import de.jcup.egradle.eclipse.api.VariableProvider;
import de.jcup.egradle.eclipse.util.EclipseResourceHelper;
import de.jcup.egradle.eclipse.util.EclipseUtil;

public class EGradleIdeVariableProvider implements VariableProvider {

	@Override
	public Map<String, String> getVariables(IEditorInput editorInput) {
		Map<String, String> map = new HashMap<>();
		File rootFolder = IDEUtil.getRootProjectFolderWithoutErrorHandling();
		if (rootFolder == null) {
			return map;
		}

		/*
		 * Determine, if this edited file is inside current egradle root project
		 * or not
		 */
		boolean isPartOfCurrentRootProject = false;
		if (editorInput instanceof IFileEditorInput) {
			IFileEditorInput feditorInput = (IFileEditorInput) editorInput;
			IFile file = feditorInput.getFile();

			EclipseResourceHelper resHelper = EclipseUtil.getResourceHelper();
			FileSupport fileHelper = EclipseUtil.getFileHelper();

			try {
				File editorFile = resHelper.toFile(file);
				isPartOfCurrentRootProject = fileHelper.isInside(editorFile, rootFolder);
			} catch (CoreException e) {
				IDEUtil.logError("Cannot convert to norm file:" + file, e);
			}

		}

		if (isPartOfCurrentRootProject) {
			String rootProjectDir = rootFolder.getAbsolutePath().replace('\\', '/');
			map.put("rootProject.projectDir", rootProjectDir);
		}
		return map;
	}

}
