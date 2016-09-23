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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class EGradleMessageDialog {

	public static final EGradleMessageDialog INSTANCE = new EGradleMessageDialog();

	public void showWarning(String message) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				Shell shell = getActiveWorkbenchShell();
				MessageDialog.openWarning(shell, "Egradle", message);
			}

		});

	}

	public void showError(String message) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				Shell shell = getActiveWorkbenchShell();
				MessageDialog.openError(shell, "Egradle", message);
			}

		});

	}

}