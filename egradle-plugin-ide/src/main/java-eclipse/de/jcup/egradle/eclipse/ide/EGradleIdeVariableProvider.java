package de.jcup.egradle.eclipse.ide;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.jcup.egradle.eclipse.api.VariableProvider;

public class EGradleIdeVariableProvider implements VariableProvider{

	@Override
	public Map<String, String> getVariables() {
		Map<String,String> map = new HashMap<>();
		/*
		 * TODO ATR, 28.11.2016: what about check if current file is inside
		 * current root project? Otherwise the link makes not really sense
		 * !?!?
		 */
		File rootFolder = IdeUtil.getRootProjectFolderWithoutErrorHandling();
		if (rootFolder != null) {
			String rootProjectDir = rootFolder.getAbsolutePath().replace('\\', '/');
			map.put("rootProject.projectDir", rootProjectDir);
		}
		return map;
	}

	
}
