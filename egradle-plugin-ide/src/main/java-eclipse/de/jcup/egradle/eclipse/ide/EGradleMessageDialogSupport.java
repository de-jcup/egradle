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

import static de.jcup.egradle.eclipse.util.EclipseUtil.*;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.eclipse.ui.BuildFailedDialog;
import de.jcup.egradle.eclipse.util.EclipseUtil;

public class EGradleMessageDialogSupport {

	public static final EGradleMessageDialogSupport INSTANCE = new EGradleMessageDialogSupport();

	/**
	 * Shows an input dialog
	 * 
	 * @param message
	 * @param title
	 * @return given input string or <code>null</code> when canceled
	 */
	public final String showInputDialog(String message, String title) {
		Shell shell = getActiveWorkbenchShell();
		InputDialog dialog = new InputDialog(shell, title, message, null, null);
		int result = dialog.open();
		if (result == InputDialog.CANCEL) {
			return null;
		}

		return dialog.getValue();
	}

	public void showWarning(String message) {
		EclipseUtil.safeAsyncExec(new Runnable() {

			@Override
			public void run() {
				Shell shell = getActiveWorkbenchShell();
				MessageDialog.openWarning(shell, "EGradle", message);
			}

		});

	}

	public void showError(String message) {
		EclipseUtil.safeAsyncExec(new Runnable() {

			@Override
			public void run() {
				Shell shell = getActiveWorkbenchShell();
				MessageDialog.openError(shell, "EGradle", message);
			}

		});

	}

	public void showBuildFailed(String detail) {
		EclipseUtil.safeAsyncExec(new Runnable() {

			@Override
			public void run() {

				if (IDEUtil.getPreferences().isShowingConsoleOnBuildFailed()) {
					IDEUtil.showConsoleView();
				}

				String text = detail;
				String path = "icons/gradle-build-failed.png";
				if (IDEUtil.existsValidationErrors()) {
					text = text + "\n(Please look into problems view for details about compile/evaluation failures)";
					path = "icons/gradle-script-failure.png";
				}
				Image backgroundImage = IDEUtil.getImage(path);
				Image titleImage = IDEUtil.getImage("icons/gradle-og.png");

				Shell shell = getActiveWorkbenchShell();
				BuildFailedDialog bfdialog = new BuildFailedDialog(shell, titleImage, backgroundImage, text);
				bfdialog.open();

			}

		});

	}

	public void showMissingRootProjectDialog(String detailMessage) {

		EclipseUtil.safeAsyncExec(new Runnable() {

			@Override
			public void run() {

				MissingRootProjectDialog missingRootProjectDialog = new MissingRootProjectDialog(
						getActiveWorkbenchShell(), detailMessage);
				missingRootProjectDialog.open();

			}

		});

	}

}
