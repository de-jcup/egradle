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
package de.jcup.egradle.eclipse;

import static de.jcup.egradle.eclipse.api.EGradleUtil.*;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.preferences.EGradlePreferences;
import de.jcup.egradle.eclipse.ui.BuildFailedDialog;

public class EGradleMessageDialogSupport {

	public static final EGradleMessageDialogSupport INSTANCE = new EGradleMessageDialogSupport();

	public void showWarning(String message) {
		EGradleUtil.safeAsyncExec(new Runnable() {

			@Override
			public void run() {
				Shell shell = getActiveWorkbenchShell();
				MessageDialog.openWarning(shell, "EGradle", message);
			}

		});

	}

	public void showError(String message) {
		EGradleUtil.safeAsyncExec(new Runnable() {

			@Override
			public void run() {
				Shell shell = getActiveWorkbenchShell();
				MessageDialog.openError(shell, "EGradle", message);
			}

		});

	}

	public void showBuildFailed(String detail) {
		EGradleUtil.safeAsyncExec(new Runnable() {

			@Override
			public void run() {
				
				if (EGradlePreferences.EGRADLE_IDE_PREFERENCES.isShowingConsoleOnBuildFailed()){
					EGradleUtil.showConsoleView();
				}
			
				String text = detail;
				String path = "icons/gradle-build-failed.png";
				if (EGradleUtil.existsValidationErrors()){
					text = text+"\n(Please look into problems view for details about compile/evaluation failures)";
					path="icons/gradle-script-failure.png";
				}
				Image backgroundImage = EGradleUtil.getImage(path);
				Image titleImage = EGradleUtil.getImage("icons/gradle-og.png");
				
				Shell shell = getActiveWorkbenchShell();
				BuildFailedDialog bfdialog = new BuildFailedDialog(shell, titleImage, backgroundImage, text);
				bfdialog.open();
				
			}

		});
	
	}

		

}
