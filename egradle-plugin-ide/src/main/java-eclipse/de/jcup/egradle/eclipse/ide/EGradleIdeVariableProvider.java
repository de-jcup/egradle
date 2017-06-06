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
				IDEUtil.logError("Cannot convert to norm file:"+file, e);
			}

		}
		
		if (isPartOfCurrentRootProject) {
			String rootProjectDir = rootFolder.getAbsolutePath().replace('\\', '/');
			map.put("rootProject.projectDir", rootProjectDir);
		}
		return map;
	}

}
