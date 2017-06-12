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
import static de.jcup.egradle.eclipse.util.EclipseUtil.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbenchWindow;

import de.jcup.egradle.core.GradleImportScanner;
import de.jcup.egradle.core.ProcessExecutionResult;
import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.process.EGradleShellType;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.ProcessExecutor;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.core.virtualroot.VirtualRootProjectException;
import de.jcup.egradle.eclipse.ide.EGradleMessageDialogSupport;
import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.ide.execution.GradleExecutionException;
import de.jcup.egradle.eclipse.ide.execution.UIGradleExecutionDelegate;
import de.jcup.egradle.eclipse.ide.filehandling.AutomaticalDeriveBuildFoldersHandler;
import de.jcup.egradle.eclipse.ide.preferences.EGradleIdePreferences;
import de.jcup.egradle.eclipse.ide.virtualroot.EclipseVirtualProjectPartCreator;

public class RootProjectImportSupport {
	String globalJavaHome;
	String gradleCommand;
	String gradleInstallPath;
	EGradleShellType shell;
	String callTypeId;

	private AutomaticalDeriveBuildFoldersHandler automaticalDeriveBuildFoldersHandler;

	public RootProjectImportSupport() {
		automaticalDeriveBuildFoldersHandler = new AutomaticalDeriveBuildFoldersHandler();
	}

	void doImport(IPath path, IProgressMonitor monitor) throws Exception {
		UpdateRunnable op = new UpdateRunnable();
		op.path = path;

		Job job = new Job("Finalize egradle import") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				getSafeDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						try {
							new ProgressMonitorDialog(getActiveWorkbenchShell()).run(true, true, op);

						} catch (InvocationTargetException e) {

							IDEUtil.logError("Assemble task failed", e);
							EGradleMessageDialogSupport.INSTANCE.showBuildFailed("Assemble task failed");

						} catch (InterruptedException e) {
							/* ignore - user interrupted */
						}
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	private class UpdateRunnable implements IRunnableWithProgress {
		private boolean autoBuildEnabled;
		private IPath path;
		private int worked;

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			boolean cleanProjects = IDEUtil.getPreferences().isCleanProjectsOnImportEnabled();
			boolean executeAssemble = IDEUtil.getPreferences().isExecuteAssembleTaskOnImportEnabled();

			try {

				autoBuildEnabled = isWorkspaceAutoBuildEnabled();
				if (autoBuildEnabled) {
					/* we disable auto build while import running */
					setWorkspaceAutoBuild(false);
				}
				File newRootFolder = getResourceHelper().toFile(path);
				if (newRootFolder == null) {
					return;
				}
				boolean virtualRootExistedBefore = EclipseVirtualProjectPartCreator
						.deleteVirtualRootProjectFull(monitor);

				List<IProject> projectsToClose = fetchEclipseProjectsAlreadyInNewRootProject(newRootFolder);

				GradleRootProject rootProject = new GradleRootProject(newRootFolder);

				int closeSize = projectsToClose.size();
				int workToDo = 0;

				workToDo += closeSize;// close projects (virtual root is
										// contained)
				workToDo += closeSize;// delete projects
				workToDo += 1;// import projects
				workToDo++; // recreate virtual root project

				String message = "Importing gradle project(s) from:" + newRootFolder.getAbsolutePath();
				monitor.beginTask(message, workToDo);
				getSystemConsoleOutputHandler().output(message);

				importProgressMessage(monitor, "collect infos about existing eclipse projects");
				monitor.worked(++worked);

				worked = closeProjectsWhichWillBeDeletedOrReimported(monitor, worked, projectsToClose);
				if (monitor.isCanceled()) {
					return;
				}

				ProcessExecutionResult processExecutionResult = executeGradleEclipse(rootProject, monitor);

				if (processExecutionResult.isNotOkay()) {
					worked = undoFormerClosedProjects(monitor, worked, virtualRootExistedBefore, projectsToClose,
							processExecutionResult);
					return;
				}
				worked = deleteProjects(monitor, worked, projectsToClose);
				importProgressMessage(monitor, "Deleted closed projects. Start eclipse refresh operations");

				/* -------------------------- */
				/* - Rescan eclipse parts   - */
				/* -------------------------- */
				GradleImportScanner importScanner = new GradleImportScanner();
				List<File> existingEclipseFoldersAfterImport = importScanner.scanEclipseProjectFolders(newRootFolder);

				/* check if virtual root should be created */
				boolean hasNoEclipseFoldersAtAll = existingEclipseFoldersAfterImport.isEmpty();

				boolean createVirtualRoot = false;
				createVirtualRoot = createVirtualRoot || hasNoEclipseFoldersAtAll;
				createVirtualRoot = createVirtualRoot || !existingEclipseFoldersAfterImport.contains(newRootFolder);

				
				/* result is okay, so use this setup in preferences now */
				EGradleIdePreferences preferences = getPreferences();
				preferences.setRootProjectPath(newRootFolder.getAbsolutePath());
				preferences.setGlobalJavaHomePath(globalJavaHome);
				preferences.setGradleBinInstallFolder(gradleInstallPath);
				preferences.setGradleCallCommand(gradleCommand);
				preferences.setGradleShellType(shell);
				preferences.setGradleCallTypeID(callTypeId);

				worked = importProjects(monitor, worked, existingEclipseFoldersAfterImport);
				importProgressMessage(monitor, "Imports done. Start eclipse refresh operations");

				/* ---------------- */
				/* update workspace */
				/* ---------------- */
				if (executeAssemble) {
					/*
					 * execute assemble task and - if enabled - after execution
					 * the 'clean projects' operation
					 */
					processExecutionResult = executeGradleAssembleAndDoFullCleanBuild(rootProject, monitor,
							cleanProjects);
					if (processExecutionResult.isNotOkay()) {
						throw new InvocationTargetException(
								new GradleExecutionException("Assemble task result was no okay"));
					}
				} else {
					/*
					 * if assemble is turned off but user wants to have
					 * cleanProjects, this must be done here too
					 */
					if (cleanProjects) {
						IWorkbenchWindow window = getActiveWorkbenchWindow();
						cleanAllProjects(true, window, monitor);
					}
				}
				
				/* recreate virtual root project */
				if (createVirtualRoot) {
					createOrRecreateVirtualRootProject();
				}

			} catch (Exception e) {
				throw new InvocationTargetException(e);
			} finally {
				monitor.done();

				if (autoBuildEnabled) {
					try {
						setWorkspaceAutoBuild(true);
					} catch (CoreException e) {
						IDEUtil.logError("Reenabling workspace auto build failed!", e);
					}
				}
			}
		}
	}

	private int undoFormerClosedProjects(IProgressMonitor monitor, int worked, boolean virtualRootExistedBefore,
			List<IProject> projectsToClose, ProcessExecutionResult processExecutionResult)
			throws CoreException, VirtualRootProjectException {
		/*
		 * UNDO !
		 */
		openSystemConsole(true);

		if (processExecutionResult.isCanceledByuser()) {
			importProgressMessage(monitor, "import canceled - undo former project closing");
		} else {
			getDialogSupport().showError(
					"Was not able to execute 'gradle eclipse'. Look into opened gradle system console for more details.\n\n"
							+ "Will now UNDO former actions!\n\n"
							+ "Please check your settings are correct in egradle preferences.\n"
							+ "Be aware importing with gradle wrapper needs a wrapper inside your imported root project!\n"
							+ "Also your projects have to apply eclipse plugin inside build.gradle.");
			importProgressMessage(monitor, "import failed - undo former project closing");
		}
		/*
		 * reopen former project parts because import was not successful
		 */
		for (IProject projectToClose : projectsToClose) {
			importProgressMessage(monitor, "reopen already existing project:" + projectToClose.getName());
			projectToClose.open(monitor);
			monitor.worked(++worked); // also count to show progress
		}
		if (virtualRootExistedBefore) {
			createOrRecreateVirtualRootProject();
		}
		return worked;
	}

	private int importProjects(IProgressMonitor monitor, int worked, List<File> foldersToImport) throws CoreException {
		/* start import of all eclipse projects inside multiproject */
		for (File folder : foldersToImport) {
			importProgressMessage(monitor, "importing: " + folder.getAbsolutePath());
			IProject project = IDEUtil.importProject(folder, monitor);
			project.open(monitor);
			automaticalDeriveBuildFoldersHandler.deriveBuildFolders(project, monitor);
			monitor.worked(++worked);
		}
		return worked;
	}

	private int deleteProjects(IProgressMonitor monitor, int worked, List<IProject> projectsToClose)
			throws CoreException {
		/* delete the projects */
		for (IProject projectToClose : projectsToClose) {
			importProgressMessage(monitor, "delete already existing project:" + projectToClose.getName());
			projectToClose.delete(false, true, monitor);
			monitor.worked(++worked);
		}
		return worked;
	}

	private int closeProjectsWhichWillBeDeletedOrReimported(IProgressMonitor monitor, int worked,
			List<IProject> projectsToClose) throws CoreException {
		/* close the projects which will be deleted/reimported */
		for (IProject projectToClose : projectsToClose) {
			importProgressMessage(monitor, "close already existing project:" + projectToClose.getName());
			if (isRootProject(projectToClose)) {
				/* ignore on close */
			} else {
				projectToClose.close(monitor);
			}
			monitor.worked(++worked);
		}
		return worked;
	}

	private List<IProject> fetchEclipseProjectsAlreadyInNewRootProject(File newRootFolder) throws CoreException {
		List<IProject> projectsToClose = new ArrayList<>();
		for (IProject project : getAllProjects()) {
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

	private ProcessExecutionResult executeGradleEclipse(GradleRootProject rootProject, IProgressMonitor progressMonitor)
			throws GradleExecutionException, Exception {
		OutputHandler outputHandler = getSystemConsoleOutputHandler();
		/*
		 * we do process executor create now in endless running variant because
		 * cancel state is provided now for this wizard
		 */
		ProcessExecutor processExecutor = new SimpleProcessExecutor(outputHandler, true,
				ProcessExecutor.ENDLESS_RUNNING);

		UIGradleExecutionDelegate delegate = new UIGradleExecutionDelegate(outputHandler, processExecutor,
				context -> context.setCommands(GradleCommand.build("cleanEclipse eclipse")), rootProject) {
			@Override
			protected GradleContext createContext(GradleRootProject rootProject) throws GradleExecutionException {
				String shellId = null;
				if (shell != null) {
					shellId = shell.getId();
				}
				return createContext(rootProject, globalJavaHome, gradleCommand, gradleInstallPath, shellId);
			}
		};
		delegate.setCleanAllProjects(false, false); // do NOT make clean
													// projects here or do a
													// refresh! this must be
													// done later!
		delegate.setRefreshAllProjects(false); //
		delegate.setShowEGradleSystemConsole(true);
		delegate.execute(progressMonitor);

		ProcessExecutionResult processExecutionResult = delegate.getResult();
		return processExecutionResult;
	}

	private ProcessExecutionResult executeGradleAssembleAndDoFullCleanBuild(GradleRootProject rootProject,
			IProgressMonitor progressMonitor, boolean clean) throws GradleExecutionException, Exception {
		OutputHandler outputHandler = getSystemConsoleOutputHandler();
		/*
		 * we do process executor create now in endless running variant because
		 * cancel state is provided now for this wizard
		 */
		ProcessExecutor processExecutor = new SimpleProcessExecutor(outputHandler, true,
				ProcessExecutor.ENDLESS_RUNNING);

		UIGradleExecutionDelegate delegate = new UIGradleExecutionDelegate(outputHandler, processExecutor,
				context -> context.setCommands(GradleCommand.build("assemble")), rootProject) {
			@Override
			protected GradleContext createContext(GradleRootProject rootProject) throws GradleExecutionException {
				String shellId = null;
				if (shell != null) {
					shellId = shell.getId();
				}
				return createContext(rootProject, globalJavaHome, gradleCommand, gradleInstallPath, shellId);
			}
		};
		delegate.setRefreshAllProjects(true);
		if (clean) {
			delegate.setCleanAllProjects(true, true);
		}
		delegate.setShowEGradleSystemConsole(true);

		delegate.execute(progressMonitor);

		ProcessExecutionResult processExecutionResult = delegate.getResult();
		return processExecutionResult;
	}
}
