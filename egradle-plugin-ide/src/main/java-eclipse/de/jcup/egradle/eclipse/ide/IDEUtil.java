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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.GlobalBuildAction;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.progress.IProgressConstants2;

import de.jcup.egradle.core.Constants;
import de.jcup.egradle.core.GradleImportScanner;
import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.RememberLastLinesOutputHandler;
import de.jcup.egradle.core.validation.GradleOutputValidator;
import de.jcup.egradle.core.validation.ValidationResult;
import de.jcup.egradle.core.virtualroot.VirtualProjectCreator;
import de.jcup.egradle.core.virtualroot.VirtualRootProjectException;
import de.jcup.egradle.eclipse.ide.console.EGradleSystemConsole;
import de.jcup.egradle.eclipse.ide.console.EGradleSystemConsoleFactory;
import de.jcup.egradle.eclipse.ide.console.EGradleSystemConsoleProcessOutputHandler;
import de.jcup.egradle.eclipse.ide.decorators.EGradleProjectDecorator;
import de.jcup.egradle.eclipse.ide.filehandling.AutomaticalDeriveBuildFoldersHandler;
import de.jcup.egradle.eclipse.ide.handlers.UpdateOrCreateVirtualRootProjectHandler;
import de.jcup.egradle.eclipse.ide.preferences.EGradleIdePreferences;
import de.jcup.egradle.eclipse.ide.preferences.EGradleSetupGradlePreferencePage;
import de.jcup.egradle.eclipse.ide.virtualroot.EclipseVirtualProjectPartCreator;
import de.jcup.egradle.eclipse.ide.virtualroot.VirtualRootProjectNature;
import de.jcup.egradle.eclipse.ide.wizards.RootProjectImportSupport;
import de.jcup.egradle.eclipse.ui.UnpersistedMarkerHelper;
import de.jcup.egradle.eclipse.util.EclipseUtil;
import de.jcup.egradle.eclipse.util.ProjectDescriptionCreator;
import de.jcup.egradle.template.FileStructureTemplate;
import de.jcup.egradle.template.FileStructureTemplateManager;

public class IDEUtil {

	private static final IProgressMonitor NULL_PROGESS = new NullProgressMonitor();

	private static VirtualProjectCreator virtualProjectCreator = new VirtualProjectCreator();

	private static OutputHandler systemConsoleOutputHandler;
	private static UnpersistedMarkerHelper buildScriptProblemMarkerHelper = new UnpersistedMarkerHelper(
			"de.jcup.egradle.script.problem");

	/**
	 * @return system console output outputHandler, never <code>null</code>
	 */
	public static OutputHandler getSystemConsoleOutputHandler() {
		if (systemConsoleOutputHandler == null) {
			systemConsoleOutputHandler = new EGradleSystemConsoleProcessOutputHandler();
		}
		return systemConsoleOutputHandler;
	}

	public static RememberLastLinesOutputHandler createOutputHandlerForValidationErrorsOnConsole() {
		int max;
		if (getPreferences().isOutputValidationEnabled()) {
			max = Constants.VALIDATION_OUTPUT_SHRINK_LIMIT;
		} else {
			max = 0;
		}
		return new RememberLastLinesOutputHandler(max);
	}

	public static ProjectContext getAllEclipseProjectsInCurrentGradleRootProject() {
		ProjectContext context = new ProjectContext();
		RootProjectImportSupport support = new RootProjectImportSupport();
		GradleRootProject gradleRootProject = IDEUtil.getRootProject(true);
		if (gradleRootProject == null) {
			return context; // context is empty - okay.
		}
		List<IProject> projects;
		File rootFolder = gradleRootProject.getFolder();
		try {
			projects = support.fetchEclipseProjectsInRootProject(rootFolder);
			context.getProjects().addAll(projects);
		} catch (CoreException e) {
			logError("Was not able to fetch eclipse projects from current root project:" + rootFolder, e);
		}
		return context;
	}

	/**
	 * Creates or refreshes virtual root project. If project exists but isn't
	 * opened it will be automatically opened
	 * 
	 * @param projectName
	 * @param monitor
	 * @param projectDescriptionCreator
	 * @param natureIds
	 * @return project
	 * @throws CoreException
	 */
	public static IProject createOrRefreshProject(String projectName, IProgressMonitor monitor,
			ProjectDescriptionCreator projectDescriptionCreator, String... natureIds) throws CoreException {
		if (monitor == null) {
			monitor = NULL_PROGESS;
		}
		IProject project = getProject(projectName);
		if (!project.exists()) {
			IProjectDescription initialDescription = projectDescriptionCreator.createNewProjectDescription(projectName);
			project.create(initialDescription, monitor);

		} else {
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}

		if (!project.isOpen()) {
			project.open(monitor);
		}
		/*
		 * the next lines are important: only when we do set description on
		 * project again the nature will be created AND configured as wished -
		 * necessary to get builder running
		 * 
		 */
		IProjectDescription descriptionCopy = project.getDescription();
		descriptionCopy.setNatureIds(natureIds);
		project.setDescription(descriptionCopy, monitor);
		return project;
	}

	/**
	 * Imports a project by given description file (.project). If a project
	 * already exists in workspace with same name it will be automatically
	 * deleted (without content delete)
	 * 
	 * @param projectFileOrFolder
	 *            file (.project) or the folder containing it
	 * @param monitor
	 * @return project, never <code>null</code>
	 * @throws CoreException
	 */
	public static IProject importProject(File projectFileOrFolder, IProgressMonitor monitor) throws CoreException {
		File projectFile = null;
		if (projectFileOrFolder.isDirectory()) {
			projectFile = new File(projectFileOrFolder, GradleImportScanner.ECLIPSE_PROJECTFILE_NAME);
		} else {
			projectFile = projectFileOrFolder;
		}
		Path path = new Path(projectFile.getAbsolutePath());
		IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(path);
		IProject project = getProject(description.getName());
		/*
		 * always delete project if already existing - but without content
		 * delete
		 */
		project.delete(false, true, monitor);
		project.create(description, monitor);
		return project;
	}

	public static void setWorkspaceAutoBuild(boolean flag) throws CoreException {
		IWorkspace workspace = getWorkspace();
		IWorkspaceDescription description = workspace.getDescription();
		description.setAutoBuilding(flag);
		workspace.setDescription(description);
	}

	public static boolean isWorkspaceAutoBuildEnabled() throws CoreException {
		IWorkspace workspace = getWorkspace();
		IWorkspaceDescription description = workspace.getDescription();
		return description.isAutoBuilding();
	}

	private static IProject getProject(String projectName) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject(projectName);
		return project;
	}

	/**
	 * Get image by path from image registry. If not already registered a new
	 * image will be created and registered. If not createable a fallback image
	 * is used instead
	 * 
	 * @param path
	 * @return image
	 */
	public static Image getImage(String path) {
		return EclipseUtil.getImage(path, IDEActivator.PLUGIN_ID);
	}

	public static ImageDescriptor createImageDescriptor(String path) {
		return EclipseUtil.createImageDescriptor(path, IDEActivator.PLUGIN_ID);
	}

	/**
	 * Open system console
	 * 
	 * @param ensureNoScrollLock
	 *            - if <code>true</code> scroll lock will be disabled
	 */
	public static void openSystemConsole(boolean ensureNoScrollLock) {
		EclipseUtil.safeAsyncExec(new Runnable() {

			@Override
			public void run() {
				IConsole eGradleSystemConsole = EGradleSystemConsoleFactory.INSTANCE.getConsole();
				IWorkbenchPage page = EclipseUtil.getActivePage();
				String id = IConsoleConstants.ID_CONSOLE_VIEW;
				IConsoleView view;
				try {
					view = (IConsoleView) page.showView(id);
					view.display(eGradleSystemConsole);

					if (ensureNoScrollLock) {
						view.setScrollLock(false);
					}

				} catch (PartInitException e) {
					logError("Was not able to show system console", e);
				}
			}

		});

	}

	/**
	 * Does output on {@link EGradleSystemConsole} instance - asynchronous
	 * inside SWT thread
	 * 
	 * @param message
	 */
	public static void outputToSystemConsole(String message) {
		EclipseUtil.getSafeDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				getSystemConsoleOutputHandler().output(message);

			}

		});
	}

	public static void refreshAllProjectDecorations() {
		getSafeDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				IWorkbench workbench = PlatformUI.getWorkbench();
				if (workbench == null) {
					return;
				}
				IDecoratorManager manager = workbench.getDecoratorManager();

				IProject[] projects = getAllProjects();

				EGradleProjectDecorator decorator = (EGradleProjectDecorator) manager
						.getBaseLabelProvider("de.jcup.egradle.eclipse.ide.decorators.EGradleProjectDecorator");
				/* decorate */
				if (decorator != null) { // decorator is enabled

					LabelProviderChangedEvent event = new LabelProviderChangedEvent(decorator, projects);
					decorator.fireLabelProviderChanged(event);
				}

				UpdateOrCreateVirtualRootProjectHandler.requestRefresh();
			}

		});

	}

	/**
	 * Does a refresh to projects. If enabled build folders are automatically
	 * derived
	 * 
	 * @param context
	 * 
	 * @param monitor
	 */
	public static void refreshProjects(ProjectContext context, IProgressMonitor monitor) {
		if (monitor == null) {
			monitor = NULL_PROGESS;
		}
		AutomaticalDeriveBuildFoldersHandler automaticalDeriveBuildFoldersHandler = new AutomaticalDeriveBuildFoldersHandler();
		if (context == null) {
			outputToSystemConsole("start refreshing all projects");
		} else {
			outputToSystemConsole("start refreshing " + context.getProjects().size() + " projects");
		}
		IProject[] projects = getAllProjects();
		for (IProject project : projects) {
			try {
				if (monitor.isCanceled()) {
					break;
				}
				if (context != null) {
					if (!context.getProjects().contains(project)) {
						// this project is not inside context - so skip
						continue;
					}
				}
				String text = "refreshing project " + project.getName();
				monitor.subTask(text);
				project.refreshLocal(IResource.DEPTH_INFINITE, monitor);

				automaticalDeriveBuildFoldersHandler.deriveBuildFolders(project, monitor);

			} catch (CoreException e) {
				logError("Was not able to refresh all projects", e);
				outputToSystemConsole(Constants.CONSOLE_FAILED + " to refresh project " + project.getName());
			}
		}
		outputToSystemConsole(Constants.CONSOLE_OK);

	}

	public static void cleanProjects(ProjectContext context, boolean buildAfterClean, IWorkbenchWindow window,
			IProgressMonitor monitor) {
		if (monitor == null) {
			monitor = NULL_PROGESS;
		}
		final IBuildConfiguration[] buildConfigurations = extractBuildConfigurations(context);
		if (monitor.isCanceled()) {
			return;
		}
		String message = createBuildInfoMessage(buildAfterClean, buildConfigurations);

		outputToSystemConsole(message);
		// see org.eclipse.ui.internal.ide.dialogs.CleanDialog#buttonPressed
		WorkspaceJob cleanJob = new WorkspaceJob(message) {
			@Override
			public boolean belongsTo(Object family) {
				return ResourcesPlugin.FAMILY_MANUAL_BUILD.equals(family);
			}

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				if (buildConfigurations == null) {
					doCleanAll(monitor);
				} else {
					doClean(buildConfigurations, monitor);
				}

				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				if (!buildAfterClean) {
					return Status.OK_STATUS;
				}

				if (buildConfigurations != null) {
					SubMonitor progress = SubMonitor.convert(monitor, 1);
					progress.setTaskName("build cleaned gradle projects in eclipse");
					try {
						ResourcesPlugin.getWorkspace().build(buildConfigurations,
								IncrementalProjectBuilder.INCREMENTAL_BUILD, true, progress.split(1));
						return Status.OK_STATUS;
					} catch (CoreException e) {
						logError("Was not able to build eclipse projects after clean", e);
						return e.getStatus();
					}

				}

				/* no build configurations set - use global variant... */
				if (window == null) {
					logWarning(
							"Not able to trigger global build after clean because no active workbench window found!");
					return Status.OK_STATUS;
				}

				GlobalBuildAction build = new GlobalBuildAction(window, IncrementalProjectBuilder.INCREMENTAL_BUILD);
				build.doBuild();

				return Status.OK_STATUS;

			}
		};
		cleanJob.setRule(getWorkspace().getRuleFactory().buildRule());
		cleanJob.setUser(true);
		cleanJob.setProperty(IProgressConstants2.SHOW_IN_TASKBAR_ICON_PROPERTY, Boolean.TRUE);
		cleanJob.schedule();

		outputToSystemConsole(Constants.CONSOLE_OK);

	}

	protected static String createBuildInfoMessage(boolean buildAfterClean,
			final IBuildConfiguration[] buildConfigurations) {
		StringBuilder sb = new StringBuilder();
		sb.append("trigger cleaning ");
		if (buildAfterClean) {
			sb.append("and build ");
		}
		sb.append("of ");
		if (buildConfigurations == null) {
			sb.append("all ");
		} else {
			sb.append(buildConfigurations.length);
		}
		sb.append("build configurations in eclipse");
		String message = sb.toString();
		return message;
	}

	protected static IBuildConfiguration[] extractBuildConfigurations(ProjectContext context) {
		final IBuildConfiguration[] result;
		if (context == null) {
			result = null;
		} else {
			List<IBuildConfiguration> list = new ArrayList<>();
			for (IProject project : context.getProjects()) {
				try {
					IBuildConfiguration[] projectBuildConfigurations = project.getBuildConfigs();
					if (projectBuildConfigurations == null) {
						continue;
					}
					for (IBuildConfiguration buildConfiguration : projectBuildConfigurations) {
						if (buildConfiguration == null) {
							continue;
						}
						list.add(buildConfiguration);
					}
				} catch (CoreException e) {
					logError("Was not able to get build config for project:" + project, e);
				}
			}
			result = list.toArray(new IBuildConfiguration[list.size()]);
		}
		return result;
	}

	/**
	 * Performs the actual clean operation.
	 * 
	 * @param monitor
	 *            The monitor that the build will report to
	 * @throws CoreException
	 *             thrown if there is a problem from the core builder.
	 */
	protected static void doCleanAll(IProgressMonitor monitor) throws CoreException {
		getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
	}

	/**
	 * Performs the actual clean operation.
	 * 
	 * @param buildConfigurations
	 *            the build configurations
	 * @param monitor
	 *            The monitor that the build will report to
	 * @param buildConfigs
	 * @throws CoreException
	 *             thrown if there is a problem from the core builder.
	 */
	protected static void doClean(IBuildConfiguration[] buildConfigurations, IProgressMonitor monitor)
			throws CoreException {
		getWorkspace().build(buildConfigurations, IncrementalProjectBuilder.CLEAN_BUILD, true, monitor);
	}

	public static void openGradleSetupPage(){
		
		IPreferencePage page = new EGradleSetupGradlePreferencePage();
		page.setTitle("Gradle Setup");
		showPreferencePage(page);
	}
	
	private static void showPreferencePage(IPreferencePage page){
		EclipseUtil.safeAsyncExec(new Runnable(){

			@Override
			public void run() {
				Shell shell = getSafeDisplay().getActiveShell();
				
				PreferenceManager mgr = new PreferenceManager();
				IPreferenceNode node = new PreferenceNode("1", page);
				mgr.addToRoot(node);
				PreferenceDialog dialog = new PreferenceDialog(shell, mgr);
				dialog.create();
				dialog.setMessage(page.getTitle());
				dialog.open();
			}
			
		});

	}
	
	public static void removeAllValidationErrorsOfConsoleOutput() {
		try {
			buildScriptProblemMarkerHelper.removeAllRegisteredMarkers();
		} catch (CoreException e) {
			logError("Was not able to remove all valdiation errors of console output", e);
		}
	}

	/**
	 * Shows console view
	 */
	public static void showConsoleView() {
		IWorkbenchPage activePage = getActivePage();
		if (activePage != null) {
			try {
				activePage.showView(IConsoleConstants.ID_CONSOLE_VIEW);
			} catch (PartInitException e) {
				logWarning("Was not able to show console");
			}

		}
	}

	/**
	 * Returns preferences for IDE
	 * 
	 * @return preferences
	 */
	public static EGradleIdePreferences getPreferences() {
		return EGradleIdePreferences.getInstance();
	}
	
	public static void setNoRootProjectFolder() throws CoreException {
		IDEUtil.getPreferences().setRootProjectPath("");
		EclipseVirtualProjectPartCreator.deleteVirtualRootProjectFull(NULL_PROGESS);
		refreshAllProjectDecorations();
	}
	/**
	 * Set new root project folder by given file
	 * 
	 * @param folder
	 * @throws CoreException
	 * @throws IllegalArgumentException
	 *             when folder is not a directory or is <code>null</code>
	 */
	public static void setNewRootProjectFolder(File folder) throws CoreException {
		if (folder == null) {
			throwCoreException("new root folder may not be null!");
		}
		if (!folder.isDirectory()) {
			throwCoreException("new root folder must be a directory, but is not :\n" + folder.getAbsolutePath());
		}
		IDEUtil.getPreferences().setRootProjectPath(folder.getAbsolutePath());
		EclipseVirtualProjectPartCreator.deleteVirtualRootProjectFull(NULL_PROGESS);
		refreshAllProjectDecorations();
		try {
			GradleRootProject rootProject = getRootProject();
			if (rootProject != null && rootProject.isMultiProject()) {
				createOrRecreateVirtualRootProject();
			}
		} catch (VirtualRootProjectException e) {
			throwCoreException("Cannot create virtual root project!", e);
		}
	}

	public static boolean existsValidationErrors() {
		/* Not very smart integrated, because static but it works... */
		return buildScriptProblemMarkerHelper.hasRegisteredMarkers();
	}

	/**
	 * If given list of console output contains error messages error markers for
	 * files will be created
	 * 
	 * @param consoleOutput
	 */
	public static void showValidationErrorsOfConsoleOutput(List<String> consoleOutput) {
		boolean validationEnabled = getPreferences().isOutputValidationEnabled();
		if (!validationEnabled) {
			return;
		}
		GradleOutputValidator validator = new GradleOutputValidator();
		ValidationResult result = validator.validate(consoleOutput);
		if (result.hasProblem()) {
			try {
				IResource resource = null;

				String scriptPath = result.getScriptPath();
				File rootFolder = getRootProjectFolderWithoutErrorHandling();
				if (rootFolder == null) {
					/*
					 * this problem should not occure, because other gradle
					 * actions does check this normally before. as a fallback
					 * simply do nothing
					 */
					logInfo("Was not able to validate, because no root folder set!");
					return;
				}
				String rootFolderPath = rootFolder.getAbsolutePath();
				File file = new File(scriptPath);
				if (!file.exists()) {
					resource = getWorkspace().getRoot();
					buildScriptProblemMarkerHelper.createErrorMarker(resource,
							"Build file which prodocues error does not exist:" + file.getAbsolutePath(), 0);
					return;
				}
				IWorkspace workspace = getWorkspace();
				resource = workspace.getRoot().getFileForLocation(Path.fromOSString(scriptPath));
				if (resource == null) {
					if (scriptPath.startsWith(rootFolderPath)) {
						scriptPath = scriptPath.substring(rootFolderPath.length());
					}
					IProject virtualRootProject = workspace.getRoot().getProject(Constants.VIRTUAL_ROOTPROJECT_NAME);
					if (virtualRootProject.exists()) {
						resource = virtualRootProject.getFile(scriptPath);
					}
				}

				if (resource == null) {
					// fall back to workspace root - so at least we can create
					// an
					// error marker...
					resource = getWorkspace().getRoot();
				}
				buildScriptProblemMarkerHelper.createErrorMarker(resource, result.getErrorMessage(), result.getLine());

			} catch (Exception e) {
				logError("Was not able to show validation errors", e);
			}
		}
		return;
	}

	/**
	 * Returns gradle root project. if no root project can be resolved an error
	 * dialog appears and shows information
	 * 
	 * @return root project or <code>null</code>
	 */
	public static GradleRootProject getRootProject() {
		return getRootProject(true);
	}

	/**
	 * Returns gradle root project or null
	 * 
	 * @param showErrorDialog
	 *            - if <code>true</code> an error dialog is shown when root
	 *            project is {@link Null}. if <code>false</code> no error dialog
	 *            is shown
	 * @return root project or <code>null</code>
	 */
	public static GradleRootProject getRootProject(boolean showErrorDialog) {
		String path = getPreferences().getRootProjectPath();
		if (StringUtils.isEmpty(path)) {
			if (showErrorDialog) {
				getDialogSupport().showMissingRootProjectDialog("No EGradle root project defined.");
			}
			return null;
		}
		GradleRootProject rootProject;
		try {
			rootProject = new GradleRootProject(new File(path));
		} catch (IOException e1) {
			if (showErrorDialog) {
				getDialogSupport().showError(e1.getMessage());
			}
			return null;
		}
		return rootProject;
	}

	/**
	 * Get the root project folder. If not resolvable an error dialog is shown
	 * to user and a {@link IOException} is thrown
	 * 
	 * @return root project folder never <code>null</code>
	 * @throws IOException
	 *             - if root folder would be <code>null</code>
	 */
	public static File getRootProjectFolder() throws IOException {
		GradleRootProject rootProject = getRootProject();
		if (rootProject == null) {
			throw new IOException("No gradle root project available");
		}
		return rootProject.getFolder();
	}

	/**
	 * Returns root project folder or <code>null</code>. No error dialogs or
	 * exceptions are thrown
	 * 
	 * @return root project folder or <code>null</code>
	 */
	public static File getRootProjectFolderWithoutErrorHandling() {
		GradleRootProject rootProject = getRootProject(false);
		if (rootProject == null) {
			return null;
		}
		return rootProject.getFolder();
	}

	/**
	 * If a virtual root project exists, it will be returned, otherwise
	 * <code>null</code>
	 * 
	 * @return vr project or <code>null</code>
	 */
	public static IProject getVirtualRootProject() {
		IProject[] projects = getAllProjects();
		for (IProject project : projects) {
			if (hasVirtualRootProjectNature(project)) {
				return project;
			}
		}
		return null;
	}

	/**
	 * Returns true when given project has virtual root project nature
	 * 
	 * @param project
	 * @return <code>true</code> when given project has the virtual root project
	 *         nature
	 */
	public static boolean hasVirtualRootProjectNature(IProject project) {
		if (project == null) {
			return false;
		}
		boolean virtualProjectNatureFound;
		try {
			virtualProjectNatureFound = project.hasNature(VirtualRootProjectNature.NATURE_ID);
			return virtualProjectNatureFound;
		} catch (CoreException e) {
			/* ignore ... project not found anymore */
			return false;
		}
	}

	/**
	 * Returns true when given project is configured as root project
	 * 
	 * @param project
	 * @return <code>true</code> when project location is same as root project
	 */
	public static boolean isRootProject(IProject project) {
		if (project == null) {
			return false;
		}
		File rootFolder = getRootProjectFolderWithoutErrorHandling();
		if (rootFolder == null) {
			return false;
		}
		try {
			File projectLocation = getResourceHelper().toFile(project.getLocation());
			return rootFolder.equals(projectLocation);
		} catch (CoreException e) {
			/* ignore ... project not found anymore */
			return false;
		}
	}

	/**
	 * Calculates if given project is a sub project for current root. If no root
	 * project is setup, this method will always return false.
	 * 
	 * @param p
	 * @return <code>true</code> when project is sub project of current root
	 *         project
	 * @throws CoreException
	 */
	public static boolean isSubprojectOfCurrentRootProject(IProject p) throws CoreException {
		if (p == null) {
			return false;
		}
		if (!p.exists()) {
			return false;
		}
		File rootFolder = getRootProjectFolderWithoutErrorHandling();
		if (rootFolder == null) {
			return false;
		}

		IPath path = p.getLocation();
		File parentFolder = getResourceHelper().toFile(path);
		if (parentFolder == null) {
			return false;
		}
		if (!parentFolder.exists()) {
			return false;
		}
		if (!rootFolder.equals(parentFolder)) {
			parentFolder = parentFolder.getParentFile();
		}
		if (!rootFolder.equals(parentFolder)) {
			return false;
		}
		return true;
	}

	public static EGradleMessageDialogSupport getDialogSupport() {
		return EGradleMessageDialogSupport.INSTANCE;
	}

	/**
	 * Creates or recreates virtual project - this is done asynchronous. If
	 * there exists already a virtual root project it will be deleted full
	 * before the asynchronous creation process starts!
	 * 
	 * @throws VirtualRootProjectException
	 */
	public static void createOrRecreateVirtualRootProject() throws VirtualRootProjectException {
		createOrRecreateVirtualRootProject(null);
	}

	/**
	 * Creates or recreates virtual project - this is done asynchronous. If
	 * there exists already a virtual root project it will be deleted full
	 * before the asynchronous creation process starts!
	 * 
	 * @param postProcessing
	 *            - a runnable which will be executed after virtual root project
	 *            is (sucessfully) created, can be <code>null</code>
	 * 
	 * @throws VirtualRootProjectException
	 */
	public static void createOrRecreateVirtualRootProject(Runnable postProcessing) throws VirtualRootProjectException {
		GradleRootProject rootProject = getRootProject();
		if (rootProject == null) {
			return;
		}

		try {
			EclipseVirtualProjectPartCreator.deleteVirtualRootProjectFull(NULL_PROGESS);
		} catch (CoreException e1) {
			throw new VirtualRootProjectException("Was not able to delete former virtual root project", e1);
		}

		Job job = new Job("Virtual root project") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				EclipseVirtualProjectPartCreator partCreator = new EclipseVirtualProjectPartCreator(rootProject,
						monitor);
				try {
					virtualProjectCreator.createOrUpdate(rootProject, partCreator);

					if (postProcessing != null) {
						postProcessing.run();
					}

					return Status.OK_STATUS;
				} catch (VirtualRootProjectException e) {
					getDialogSupport().showError(e.getMessage());
					logError("Was not able to update virtual root project", e);
					return Status.CANCEL_STATUS;
				}
			}
		};
		job.schedule(1000L); // 1 second delay to give IDE the chance to delete
								// old parts

	}

	public static void logInfo(String info) {
		getLog().log(new Status(IStatus.INFO, IDEActivator.PLUGIN_ID, info));
	}

	public static void logWarning(String warning) {
		getLog().log(new Status(IStatus.WARNING, IDEActivator.PLUGIN_ID, warning));
	}

	public static void logError(String error, Throwable t) {
		getLog().log(new Status(IStatus.ERROR, IDEActivator.PLUGIN_ID, error, t));
	}

	private static ILog getLog() {
		ILog log = IDEActivator.getDefault().getLog();
		return log;
	}

	/**
	 * @return list of all new-project templates, never <code>null</code>
	 */
	public static List<FileStructureTemplate> getNewProjectTemplates() {
		FileStructureTemplateManager manager = IDEActivator.getDefault().getNewProjectTemplateManager();
		return manager.getTemplates();
	}

	/**
	 * 
	 * @return gradle wrapper template or <code>null</code>
	 */
	public static FileStructureTemplate getGradleWrapperTemplate() {
		FileStructureTemplateManager manager = IDEActivator.getDefault().getGradlWrappertTemplateManager();
		List<FileStructureTemplate> templates = manager.getTemplates();
		if (templates.isEmpty()) {
			return null;
		}
		return templates.get(0);

	}

}
