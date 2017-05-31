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
package de.jcup.egradle.eclipse.ide.wizards;

import static de.jcup.egradle.eclipse.ide.IDEUtil.*;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import de.jcup.egradle.eclipse.ide.IDEActivator;
import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.ide.ui.RootProjectConfigMode;
import de.jcup.egradle.eclipse.util.EclipseUtil;
public class EGradleRootProjectImportWizard extends Wizard implements IImportWizard {
	public static final String ID="de.jcup.egradle.eclipse.importWizards.EGradleRootProjectImportWizard";
	
	private static ImageDescriptor desc = EclipseUtil
			.createImageDescriptor("icons/egradle-import-rootproject-wizard-banner.png",IDEActivator.PLUGIN_ID);//$NON-NLS-1$
	EGradleRootProjectImportWizardPage mainPage;
	RootProjectImportSupport importSupport;

	private String customRootProjectpath;

	private RootProjectConfigMode importMode;
	
	public EGradleRootProjectImportWizard() {
		importSupport = new RootProjectImportSupport();
	}

	public void setCustomRootProjectpath(String customRootProjectpath) {
		this.customRootProjectpath = customRootProjectpath;
	}

	public void setImportMode(RootProjectConfigMode importMode) {
		this.importMode = importMode;
	}
	/**
	 * The <code>BasicNewResourceWizard</code> implementation of this
	 * <code>IWorkbenchWizard</code> method records the given workbench and
	 * selection, and initializes the default banner image for the pages by
	 * calling <code>initializeDefaultPageImageDescriptor</code>. Subclasses may
	 * extend.
	 */
	@Override
	public void init(IWorkbench theWorkbench, IStructuredSelection currentSelection) {
		// this.workbench = theWorkbench;
		// this.selection = currentSelection;
		setWindowTitle("EGradle Import Wizard"); // NON-NLS-1
		setNeedsProgressMonitor(true);
		setDefaultPageImageDescriptor(desc);
	}

	public boolean performFinish() {
		IPath path = mainPage.getSelectedPath();
		if (path == null) {

			getDialogSupport().showWarning("Was not able to finish, because path is empty!");

			return false;
		}
		/* fetch data inside SWT thread */
		importSupport.globalJavaHome = mainPage.getGlobalJavaHomePath();

		importSupport.gradleInstallPath = mainPage.getGradleBinDirectory();
		importSupport.shell = mainPage.getShellCommand();
		importSupport.gradleCommand = mainPage.getGradleCommand();
		importSupport.callTypeId = mainPage.getCallTypeId();

		openSystemConsole(true);

		try {
			getContainer().run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						importSupport.doImport(path, monitor);
					} catch (Exception e) {
						throw new InvocationTargetException(e);
					}

				}
			});
			return true;
		} catch (Exception e) {
			IDEUtil.logError("EGradle Import execution failed", e);
			return false;
		}

	}

	


	public void addPages() {
		mainPage = new EGradleRootProjectImportWizardPage("egradleRootProjectWizardPage1",customRootProjectpath,importMode);
		addPage(mainPage);
	}

}
