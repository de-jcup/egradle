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

import static de.jcup.egradle.eclipse.api.EGradleUtil.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import de.jcup.egradle.core.Constants;
import de.jcup.egradle.core.GradleExecutor.Result;
import de.jcup.egradle.core.GradleImportScanner;
import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.process.EGradleShellType;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.ProcessExecutor;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.execution.GradleExecutionDelegate;
import de.jcup.egradle.eclipse.execution.GradleExecutionException;

public class EGradleRootProjectImportWizard extends Wizard implements IImportWizard {

	private static ImageDescriptor desc = EGradleUtil
			.createImageDescriptor("icons/egradle-import-rootproject-wizard-banner.png");//$NON-NLS-1$
	EGradleRootProjectImportWizardPage mainPage;
	private String globalJavaHome;
	private String gradleCommand;
	private String gradleInstallPath;
	private EGradleShellType shell;
	private String callTypeId;

	public EGradleRootProjectImportWizard() {
		
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
		globalJavaHome=mainPage.getGlobalJavaHomePath();
		
		gradleInstallPath=mainPage.getGradleBinDirectory();
		shell=mainPage.getShellCommand();
		gradleCommand=mainPage.getGradleCommand();
		callTypeId=mainPage.getCallTypeId();
		try {
			getContainer().run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						doImport(path, monitor);
					} catch (Exception e) {
						throw new InvocationTargetException(e);
					}

				}
			});
			return true;
		} catch (Exception e) {
			EGradleUtil.log(e);
			return false;
		}

	}

	private void doImport(IPath path, IProgressMonitor monitor) throws Exception {
		int worked = 0;
		try {
			File newRootFolder = getResourceHelper().toFile(path);
			if (newRootFolder == null) {
				return;
			}
			List<IProject> projectsToClose = fetchEclipseProjectsAlreadyInNewRootProject(newRootFolder);

			GradleRootProject rootProject = new GradleRootProject(newRootFolder);
			GradleImportScanner importer = new GradleImportScanner();
			List<File> foldersToImport = importer.scanEclipseProjectFolders(newRootFolder);

			/* check if virtual root should be created */
			boolean createVirtualRoot = false;
			createVirtualRoot = createVirtualRoot || foldersToImport.isEmpty();
			createVirtualRoot = createVirtualRoot || !foldersToImport.contains(newRootFolder);

			int importSize = foldersToImport.size();
			int closeSize = projectsToClose.size();
			int workToDo = 0;

			workToDo += closeSize;// close projects (virtual root is contained)
			workToDo += closeSize;// delete projects
			workToDo += importSize;// import projects
			workToDo++; // recreate virtual root project

			String message = "Importing gradle project(s) from:" + newRootFolder.getAbsolutePath();
			monitor.beginTask(message, workToDo);
			getSystemConsoleOutputHandler().output(message);

			importProgressMessage(monitor, "collect infos about existing eclipse projects");
			monitor.worked(++worked);

			/* close the projects which will be deleted/reimported */
			for (IProject projectToClose : projectsToClose) {
				importProgressMessage(monitor, "close already existing project:" + projectToClose.getName());
				projectToClose.close(monitor);
				monitor.worked(++worked);
			}

			Result result = executeGradleEclipse(rootProject, monitor);
			if (!result.isOkay()) {
				getDialogSupport()
						.showError("Was not able to execute 'gradle eclipse' - will now undo former actions!\n\n"
								+ "Please check your settings are correct in egradle preferences.\n"
								+ "Be aware importing with gradle wrapper needs a wrapper inside your imported root project!\n"
								+ "Also your projects have to apply eclipse plugin inside build.gradle...");
				importProgressMessage(monitor, "import failed - undo former project closing");
				/*
				 * reopen former project parts because import was not successful
				 */
				for (IProject projectToClose : projectsToClose) {
					importProgressMessage(monitor, "reopen already existing project:" + projectToClose.getName());
					projectToClose.open(monitor);
					monitor.worked(++worked); // also count to show progress
				}
				return;
			}
			/* result is okay, so use this setup in preferences now */
			getPreferences().setRootProjectPath(newRootFolder.getAbsolutePath());
			getPreferences().setGlobalJavaHomePath(globalJavaHome);
			getPreferences().setGradleBinInstallFolder(gradleInstallPath);
			getPreferences().setGradleCallCommand(gradleCommand);
			getPreferences().setGradleShellType(shell);
			getPreferences().setGradleCallTypeID(callTypeId);
			
			/* delete the projects */
			for (IProject projectToClose : projectsToClose) {
				importProgressMessage(monitor, "delete already existing project:" + projectToClose.getName());
				if (hasVirtualRootProjectNature(projectToClose)) {
					/*
					 * to check delete with content happens only on virtual root
					 * projects we do check on another way too - this is only to
					 * prevent coding failures and should never happen!
					 */
					String name = projectToClose.getName();
					if (!Constants.VIRTUAL_ROOTPROJECT_NAME.equals(name)) {
						throw new IllegalArgumentException(
								"Trying to delete full, with content, but this seems not to be a virtual root project?!?!?");
					}
					projectToClose.delete(true, true, monitor);
				} else {
					projectToClose.delete(false, true, monitor);
				}
				monitor.worked(++worked);
			}

			/* start import of all eclipse projects inside multiproject */
			for (File folder : foldersToImport) {
				importProgressMessage(monitor, "importing: " + folder.getAbsolutePath());
				IProject project = getResourceHelper().importProject(folder, monitor);
				project.open(monitor);
				monitor.worked(++worked);
			}

			/* recreate virtual root project */
			if (createVirtualRoot) {
				createOrRecreateVirtualRootProject();
			}
		} finally {
			monitor.done();
		}

	}

	private List<IProject> fetchEclipseProjectsAlreadyInNewRootProject(File newRootFolder) throws CoreException {
		List<IProject> projectsToClose = new ArrayList<>();
		for (IProject project : EGradleUtil.getAllProjects()) {
			IPath projectPath = project.getLocation();
			if (projectPath == null) {
				/* project no more valid */
				projectsToClose.add(project);
			} else {
				File projectAsFile = getResourceHelper().toFile(projectPath);
				if (newRootFolder.equals(projectAsFile)) {
					/*
					 * when new root folder itself is the project, remove it too
					 */
					projectsToClose.add(project);
				} else if (getFileHelper().isDirectSubFolder(projectAsFile, newRootFolder)) {
					/* direct sub folder so has to be removed */
					projectsToClose.add(project);
				}
			}
		}
		return projectsToClose;
	}

	private void importProgressMessage(IProgressMonitor monitor, String message) {
		monitor.subTask(message);
		getSystemConsoleOutputHandler().output(">>" + message);
	}

	private Result executeGradleEclipse(GradleRootProject rootProject, IProgressMonitor progressMonitor)
			throws GradleExecutionException, Exception {
		OutputHandler outputHandler = EGradleUtil.getSystemConsoleOutputHandler();
		ProcessExecutor processExecutor = new SimpleProcessExecutor(outputHandler, true, 30);

		GradleExecutionDelegate delegate = new GradleExecutionDelegate(outputHandler, processExecutor, context -> context.setCommands(GradleCommand.build("cleanEclipse eclipse")), rootProject){
			@Override
			protected GradleContext createContext(GradleRootProject rootProject)
					throws GradleExecutionException {
				String shellId=null;
				if(shell!=null){
					shellId=shell.getId();
				}
				return createContext(rootProject, globalJavaHome, gradleCommand, gradleInstallPath, shellId);
			}
		};
		delegate.execute(progressMonitor);

		Result result = delegate.getResult();
		return result;
	}

	public void addPages() {
		mainPage = new EGradleRootProjectImportWizardPage("egradleRootProjectWizardPage1");
		addPage(mainPage);
	}

}
