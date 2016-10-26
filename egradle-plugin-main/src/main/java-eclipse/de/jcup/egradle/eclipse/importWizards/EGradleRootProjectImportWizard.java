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
package de.jcup.egradle.eclipse.importWizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import de.jcup.egradle.core.GradleExecutor.Result;
import de.jcup.egradle.core.GradleImportScanner;
import de.jcup.egradle.core.api.FileUtil;
import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.ProcessExecutor;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.api.FileHelper;
import de.jcup.egradle.eclipse.api.ResourceHelper;
import de.jcup.egradle.eclipse.execution.GradleExecutionDelegate;
import de.jcup.egradle.eclipse.execution.GradleExecutionException;

public class EGradleRootProjectImportWizard extends Wizard implements IImportWizard {
	private static final IProgressMonitor NULL_PROGESS = new NullProgressMonitor();
	EGradleRootProjectImportWizardPage mainPage;

	public EGradleRootProjectImportWizard() {
		super();
	}

	public boolean performFinish() {
		IPath path = mainPage.getSelectedPath();
		if (path == null) {
			return false;
		}
		try {
			return doImport(path);
		} catch (Exception e) {
			EGradleUtil.log(e);
			return false;
		}

	}

	private ResourceHelper getResourceHelper() {
		return ResourceHelper.SHARED;
	}

	private FileHelper getFileHelper() {
		return getResourceHelper().getFileHelper();
	}

	private boolean doImport(IPath path) throws Exception {
		File newRootFolder = FileHelper.SHARED.toFile(path);
		if (newRootFolder == null) {
			return false;
		}
		GradleRootProject rootProject = new GradleRootProject(newRootFolder);
		IProgressMonitor progressMonitor = NULL_PROGESS;

		GradleImportScanner importer = new GradleImportScanner();

		List<IProject> projectsToClose = new ArrayList<>();
		/* project already exists */

		/*
		 * project already exists in eclipse - so we close it, necessary for clean reimportt
		 */

		for (IProject project : EGradleUtil.getAllProjects()) {
			IPath projectPath = project.getLocation();
			if (projectPath == null) {
				/* project no more valid */
				projectsToClose.add(project);
			} else {
				File projectAsFile = getFileHelper().toFile(projectPath);
				/* direct sub folder so has to be removed*/
				if (FileUtil.isDirectSubFolder(projectAsFile, newRootFolder)) {
					projectsToClose.add(project);
				}
			}
		}
		/* close the projects which will be deleted/reimported */
		for (IProject projectToClose : projectsToClose) {
			projectToClose.close(progressMonitor);
		}
		Result result = executeGradleEclipse(rootProject, progressMonitor);
		if (!result.isOkay()) {
			/* reopen former project parts because import was not successful */
			for (IProject projectToClose : projectsToClose) {
				projectToClose.open(progressMonitor);
			}
			return false;
		}
		/* delete the projects */
		for (IProject projectToClose : projectsToClose) {
			projectToClose.delete(false, true, progressMonitor);
		}
		/* remove the current virtual root project */
		IProject virtualRootProject = EGradleUtil.getVirtualRootProject();
		if (virtualRootProject != null) {
			virtualRootProject.delete(true, true, progressMonitor);
		}

		/* start import of all eclipse projects inside multiproject*/
		List<File> list = importer.scanEclipseProjectFolders(newRootFolder);
		/* FIXME ATR: keep on implementing.... */
		for (File file: list){
//			getResourceHelper().createOrRefreshProject(file.getName(), monitor, projectDescriptionCreator, natureIds)
		}
		return true;

	}

	private Result executeGradleEclipse(GradleRootProject rootProject, IProgressMonitor progressMonitor)
			throws GradleExecutionException, Exception {
		OutputHandler outputHandler = EGradleUtil.getSystemConsoleOutputHandler();
		ProcessExecutor processExecutor = new SimpleProcessExecutor(outputHandler, true, 30);

		GradleExecutionDelegate delegate = new GradleExecutionDelegate(outputHandler, processExecutor,
				context -> context.setCommands(GradleCommand.build("cleanEclipse eclipse")), rootProject);
		delegate.execute(progressMonitor);

		Result result = delegate.getResult();
		return result;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("EGradle Import Wizard"); // NON-NLS-1
		setNeedsProgressMonitor(true);
		mainPage = new EGradleRootProjectImportWizardPage("Import Gradle Multiproject"); // NON-NLS-1
	}

	public void addPages() {
		super.addPages();
		addPage(mainPage);
	}

}
