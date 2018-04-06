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
package de.jcup.egradle.eclipse.ide.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import de.jcup.egradle.eclipse.ide.ui.RootProjectConfigMode;
import de.jcup.egradle.eclipse.ide.wizards.EGradleRootProjectImportWizard;

public class ReimportGradleProjectHandler extends AbstractHandler {
	public static final String COMMAND_ID = "egradle.commands.reimportGradleProject";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell activeShell = HandlerUtil.getActiveShell(event);

		EGradleRootProjectImportWizard wizard = new EGradleRootProjectImportWizard();
		wizard.setImportMode(RootProjectConfigMode.REIMPORT_PROJECTS);
		wizard.setWindowTitle("Reimport project(s)");

		WizardDialog dialog = new WizardDialog(activeShell, wizard);

		dialog.open();
		return null;
	}

}
