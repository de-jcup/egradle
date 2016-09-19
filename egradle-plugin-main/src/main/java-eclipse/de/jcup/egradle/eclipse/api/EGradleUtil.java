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

import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.*;
import static de.jcup.egradle.eclipse.preferences.EGradlePreferences.PreferenceConstants.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.debug.ui.IJavaDebugUIConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.eclipse.Activator;
import de.jcup.egradle.eclipse.EGradleMessageDialog;

public class EGradleUtil {

	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		Shell shell = null;
		if (window != null) {
			shell = window.getShell();
		}
		return shell;
	}

	public static IWorkbenchWindow getWorkbenchWindowChecked(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		return window;
	}

	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window==null){
			return null;
		}
		return window.getActivePage();
	}

	public static void log(Throwable t) {
		if (t instanceof CoreException) {
			log(new Status(IStatus.ERROR, getUniqueIdentifier(), IStatus.ERROR, t.getMessage(), t.getCause()));
		} else {
			log(new Status(IStatus.ERROR, getUniqueIdentifier(), IJavaDebugUIConstants.INTERNAL_ERROR, "Internal Error",
					t));
		}

	}

	public static void log(Status status) {
		Activator.getDefault().getLog().log(status);
	}

	private static String getUniqueIdentifier() {
		return "EGradle";
	}

	static boolean isUIThread() {
		if (Display.getCurrent() == null) {
			return false;
		}
		return true;
	}

	public static File getRootProjectFolder() throws IOException {
		GradleRootProject rootProject = EGradleUtil.getRootProject();
		if (rootProject == null) {
			throw new IOException("No gradle root project available");
		}
		return rootProject.getFolder();
	}

	public static GradleRootProject getRootProject() {
		String path = PREFERENCES.getStringPreference(P_ROOTPROJECT_PATH);
		if (StringUtils.isEmpty(path)) {
			EGradleMessageDialog.INSTANCE.showError("No root project path set. Please setup in preferences!");
			return null;
		}
		GradleRootProject rootProject;
		try {
			rootProject = new GradleRootProject(new File(path));
		} catch (IOException e1) {
			EGradleMessageDialog.INSTANCE.showError(e1.getMessage());
			return null;
		}
		return rootProject;
	}
}
