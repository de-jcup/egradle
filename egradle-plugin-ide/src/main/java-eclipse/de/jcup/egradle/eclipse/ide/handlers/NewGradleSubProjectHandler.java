/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.egradle.eclipse.ide.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.jcup.egradle.core.domain.GradleProjectException;
import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.util.StringUtilsAccess;
import de.jcup.egradle.eclipse.ide.EGradleMessageDialogSupport;
import de.jcup.egradle.eclipse.ide.IDEUtil;

public class NewGradleSubProjectHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		EGradleMessageDialogSupport dialogSupport = EGradleMessageDialogSupport.INSTANCE;
		GradleRootProject rootProject = IDEUtil.getRootProject(true);
		if (!rootProject.isMultiProject()) {
			dialogSupport.showError("Cannot add a subproject to a single project!");
			return null;
		}
		String nameOfNewSubProject = dialogSupport.showInputDialog(
				"Enter name of new sub project inside '" + rootProject.getName() + "'", "New Gradle sub project");
		if (StringUtilsAccess.isBlank(nameOfNewSubProject)) {
			return null;
		}
		try {
			rootProject.createNewSubProject(nameOfNewSubProject);
		} catch (GradleProjectException e) {
			throw new ExecutionException("Was not able to create sub project:" + nameOfNewSubProject, e);
		}

		/* trigger reimport */
		ReimportGradleProjectHandler handler = new ReimportGradleProjectHandler();
		handler.execute(event);
		return null;
	}

}
