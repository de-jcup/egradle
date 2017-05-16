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
package de.jcup.egradle.eclipse.ide.launch;

import static de.jcup.egradle.eclipse.ide.IDEUtil.*;
import static de.jcup.egradle.eclipse.ide.launch.EGradleLauncherConstants.*;
import static de.jcup.egradle.eclipse.util.EclipseUtil.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import de.jcup.egradle.core.Constants;
import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.util.EclipseUtil;
/**
 * Short cut launcher for EGradle
 * 
 * @author Albert Tregnaghi
 *
 */
public class EGradleLaunchShortCut implements ILaunchShortcut2 {
	

	public IResource getLaunchableResource(IEditorPart editorpart) {
		return getLaunchableResource(editorpart.getEditorInput());
	}

	private IResource getLaunchableResource(IEditorInput editorInput) {
		return editorInput.getAdapter(IResource.class);
	}

	public IResource getLaunchableResource(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			if (ss.size() == 1) {
				Object element = ss.getFirstElement();
				if (element instanceof IAdaptable) {
					return getLaunchableResource((IAdaptable) element);
				}
			}
		}
		return null;
	}

	private IResource getLaunchableResource(IAdaptable element) {
		return element.getAdapter(IResource.class);
	}

	public ILaunchConfiguration[] getLaunchConfigurations(IEditorPart editorpart) {
		// let the framework resolve configurations based on resource mapping
		return null;
	}

	public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection) {
		// let the framework resolve configurations based on resource mapping
		return null;
	}

	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		Object additionalScope = resolveEditorAdditonalScope(editor);
		IResource resource = (IResource) input.getAdapter(IResource.class);
		if (resource != null) {
			searchAndLaunch(new Object[] { resource }, additionalScope, mode, getTypeSelectionTitle(),
					getEditorEmptyMessage());
		}
	}

	protected Object resolveEditorAdditonalScope(IEditorPart editor) {
		/* per default we do nothing here */
		return null;
	}

	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			searchAndLaunch(((IStructuredSelection) selection).toArray(), null, mode, getTypeSelectionTitle(),
					getSelectionEmptyMessage());
		}
	}

	/**
	 * Returns a configuration from the given collection of configurations that
	 * should be launched, or <code>null</code> to cancel. Default
	 * implementation opens a selection dialog that allows the user to choose
	 * one of the specified launch configurations. Returns the chosen
	 * configuration, or <code>null</code> if the user cancels.
	 * 
	 * @param configList
	 *            list of configurations to choose from
	 * @return configuration to launch or <code>null</code> to cancel
	 */
	protected ILaunchConfiguration chooseConfiguration(List<ILaunchConfiguration> configList) {
		IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setElements(configList.toArray());
		dialog.setTitle(getTypeSelectionTitle());
		dialog.setMessage(getChooseConfigurationTitle());
		dialog.setMultipleSelection(false);
		int result = dialog.open();
		labelProvider.dispose();
		if (result == Window.OK) {
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		return null;
	}

	protected String getChooseConfigurationTitle() {
		return "Choose EGradle config";
	}

	/**
	 * Creates and returns a new configuration based on the specified type.
	 * 
	 * @param additionalScope additional scope which can be given
	 * @param type
	 *            type to create a launch configuration for
	 * 
	 * @return launch configuration configured to launch the specified type
	 */
	protected ILaunchConfiguration createConfiguration(IResource resource, Object additionalScope) {
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		try {
			String projectName = createGradleProjectName(resource);
			String proposal = createLaunchConfigurationNameProposal(projectName, resource, additionalScope);

			ILaunchConfigurationType configType = getConfigurationType();
			wc = configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName(proposal));
			createCustomConfiguration(resource, additionalScope, wc, projectName);

			config = wc.doSave();
		} catch (CoreException exception) {
			MessageDialog.openError(EclipseUtil.getActiveWorkbenchShell(), "EGradle create configuration failed",
					exception.getStatus().getMessage());
		}
		return config;
	}

	protected String createLaunchConfigurationNameProposal(String projectName, IResource resource,
			Object additionalScope) {
		if (StringUtils.isBlank(projectName)){
			return "RootProject";
		}
		return projectName;
	}

	protected void createCustomConfiguration(IResource resource, Object additionalScope,
			ILaunchConfigurationWorkingCopy wc, String projectName) {
		createProjectNameConfiguration(wc, projectName);
		createTaskConfiguration(wc);

		wc.setMappedResources(new IResource[] { getResourceToMap(resource) });
	}

	protected IResource getResourceToMap(IResource resource) {
		if (resource instanceof IProject) {
			/* project itself is always correct */
			return resource;
		}
		if (resource instanceof IFile) {
			if (isResourceToMapFilesAllowed()) {
				return resource;
			}
			return findProject(resource);
		}
		if (resource instanceof IFolder) {
			if (isResourceToMapFoldersAllowed()) {
				return resource;
			}
			return findProject(resource);
		}
		return resource;
	}

	private IResource findProject(IResource resource) {
		IProject project = resource.getProject();
		return project;
	}

	/**
	 * Returns true, when the launch configuration resource mapping is allowed
	 * to directly map to an IFile.
	 * 
	 * @return <code>true</code> when allowed, <code>false</code> when not (so
	 *         the project of the file will be used instead)
	 */
	protected boolean isResourceToMapFilesAllowed() {
		return false;
	}

	/**
	 * Returns true, when the launch configuration resource mapping is allowed
	 * to directly map to an IFolder.
	 * 
	 * @return <code>true</code> when allowed, <code>false</code> when not (so
	 *         the project of the folder will be used instead)
	 */
	protected boolean isResourceToMapFoldersAllowed() {
		return false;
	}

	protected void createProjectNameConfiguration(ILaunchConfigurationWorkingCopy wc, String projectName) {
		setProjectNameIgnoreVirtualRootProjectNames(wc, projectName);
	}

	public static void setProjectNameIgnoreVirtualRootProjectNames(ILaunchConfigurationWorkingCopy wc, String projectName) {
		if (Constants.VIRTUAL_ROOTPROJECT_NAME.equals(projectName)){
			wc.setAttribute(PROPERTY_PROJECTNAME,"");
		}else{
			wc.setAttribute(PROPERTY_PROJECTNAME, projectName);
		}
	}

	protected void createTaskConfiguration(ILaunchConfigurationWorkingCopy wc) {
		wc.setAttribute(PROPERTY_TASKS, "assemble");
	}

	private String createGradleProjectName(IResource resource) {
		IProject project = resource.getProject();
		if (hasVirtualRootProjectNature(project)) {
			return "";
		}
		/* when the project itself is the root - leave project name empty!*/
		if (isRootProject(project)){
			return "";
		}
		try {
			File projectRealFolderName = getResourceHelper().toFile(project);
			return projectRealFolderName.getName();
		} catch (CoreException e) {
			throw new IllegalStateException(e);
		}
	}

	protected ILaunchConfiguration findLaunchConfiguration(IResource resource, Object additionalScope,
			ILaunchConfigurationType configType) {
		List<ILaunchConfiguration> configs = getCandidates(resource, additionalScope, configType);
		int count = configs.size();
		if (count == 1) {
			return configs.get(0);
		}
		if (count > 1) {
			return chooseConfiguration(configs);
		}
		return null;
	}

	/**
	 * Collect the listing of {@link ILaunchConfiguration}s that apply to the
	 * given {@link IType} and {@link ILaunchConfigurationType}
	 * 
	 * @param resource
	 *            the type
	 * @param configType
	 *            the {@link ILaunchConfigurationType}
	 * @param additonalScope
	 *            additional scope can be <code>null</code>
	 * @return the list of {@link ILaunchConfiguration}s or an empty list, never
	 *         <code>null</code>
	 * @since 3.8
	 */
	List<ILaunchConfiguration> getCandidates(IResource selectedResource, Object additionalScope,
			ILaunchConfigurationType configType) {
		/*
		 * Convert selected resource to resource to search for... Implementation
		 * decides. E.g. standard way is that only project is the marked
		 * identifier, so files and folders selected will result in their
		 * project. A junit integration could support selected test files also,
		 * Reason for this beahviour: We would get too much launch
		 * configuration for selections doing exact same stuff
		 */
		IResource resource = getResourceToMap(selectedResource);
		List<ILaunchConfiguration> candidateConfigs = Collections.emptyList();
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager()
					.getLaunchConfigurations(configType);
			candidateConfigs = new ArrayList<ILaunchConfiguration>(configs.length);
			for (int i = 0; i < configs.length; i++) {
				ILaunchConfiguration config = configs[i];
				if (isConfigACandidate(resource, additionalScope, config)) {
					candidateConfigs.add(config);
				}
			}
		} catch (CoreException e) {
			IDEUtil.logError("Was not able to get launch candidates", e);
		}
		return candidateConfigs;
	}

	protected boolean isConfigACandidate(IResource resource, Object additionalScope, ILaunchConfiguration config)
			throws CoreException {
		String projectName = createGradleProjectName(resource);
		return config.getAttribute(PROPERTY_PROJECTNAME, "").equals(projectName);
	}

	/**
	 * Returns the type of configuration this shortcut is applicable to.
	 * 
	 * @return the type of configuration this shortcut is applicable to
	 */
	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType("de.jcup.egradle.launchConfigurationType");
	}

	/**
	 * Returns an error message to use when the editor does not contain a type
	 * that can be launched.
	 * 
	 * @return error message when editor cannot be launched
	 */
	protected String getEditorEmptyMessage() {
		return "Nothing available to launch from EGradle";
	}

	/**
	 * Returns the singleton launch manager.
	 * 
	 * @return launch manager
	 */
	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	/**
	 * Returns an error message to use when the selection does not contain a
	 * type that can be launched.
	 * 
	 * @return error message when selection cannot be launched
	 */
	protected String getSelectionEmptyMessage() {
		return "Selection does not contain something launchable by EGradle";
	}

	/**
	 * Convenience method to return the active workbench window shell.
	 * 
	 * @return active workbench window shell
	 */
	protected Shell getShell() {
		return EclipseUtil.getActiveWorkbenchShell();
	}

	/**
	 * Returns a title for a type selection dialog used to prompt the user when
	 * there is more than one type that can be launched.
	 * 
	 * @return type selection dialog title
	 */
	protected String getTypeSelectionTitle() {
		return "More than one can be launched:";
	}

	protected void launch(IResource type, Object additionalScope, String mode) {
		List<ILaunchConfiguration> configs = getCandidates(type, additionalScope, getConfigurationType());
		if (configs != null) {
			ILaunchConfiguration config = null;
			int count = configs.size();
			if (count == 1) {
				config = configs.get(0);
			} else if (count > 1) {
				config = chooseConfiguration(configs);
				if (config == null) {
					return;
				}
			}
			if (config == null) {
				config = createConfiguration(type, additionalScope);
			}
			if (config != null) {
				DebugUITools.launch(config, mode);
			}
		}
	}

	/**
	 * Resolves a type that can be launched from the given scope and launches in
	 * the specified mode.
	 * 
	 * @param resources
	 *            the java children to consider for a type that can be launched
	 * @param mode
	 *            launch mode
	 * @param selectTitle
	 *            prompting title for choosing a type to launch
	 * @param additionalScope
	 *            additional scope for launch
	 * @param emptyMessage
	 *            error message when no types are resolved for launching
	 */
	private void searchAndLaunch(Object[] resources, Object additionalScope, String mode, String selectTitle,
			String emptyMessage) {
		IResource resource = null;
		Object object = resources[0];
		if (object instanceof IResource) {
			resource = (IResource) object;
		} else if (object instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) object;
			resource = getLaunchableResource(adaptable);
		} else {
			throw new IllegalArgumentException("EGradle launch shortcut cannot handle object type:" + object);
		}
		if (resource != null) {
			launch(resource, additionalScope, mode);
		}
	}
}
