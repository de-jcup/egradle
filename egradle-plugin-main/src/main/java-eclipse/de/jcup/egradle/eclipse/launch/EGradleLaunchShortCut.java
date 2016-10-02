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
package de.jcup.egradle.eclipse.launch;

import static de.jcup.egradle.eclipse.launch.EGradleLauncherConstants.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.api.FileHelper;

/**
 * Short cut launcher for Egradle
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
		// TODO ATR, 12.08.2016: maybe this should be used for test executions?
		IResource resource = (IResource) input.getAdapter(IResource.class);
		if (resource != null) {
			searchAndLaunch(new Object[] { resource }, mode, getTypeSelectionTitle(), getEditorEmptyMessage());
		}
	}

	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			searchAndLaunch(((IStructuredSelection) selection).toArray(), mode, getTypeSelectionTitle(),
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
	 * @param type
	 *            type to create a launch configuration for
	 * @return launch configuration configured to launch the specified type
	 */
	protected ILaunchConfiguration createConfiguration(IResource resource) {
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		try {

			String projectName = createProjectName(resource);
			ILaunchConfigurationType configType = getConfigurationType();
			wc = configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName(projectName));
			createCustomConfiguration(wc, projectName);
			wc.setMappedResources(new IResource[] { resource });
			config = wc.doSave();
		} catch (CoreException exception) {
			MessageDialog.openError(EGradleUtil.getActiveWorkbenchShell(), "EGradle create configuration failed",
					exception.getStatus().getMessage());
		}
		return config;
	}

	protected void createCustomConfiguration(ILaunchConfigurationWorkingCopy wc, String projectName) {
		createProjectNameConfiguration(wc, projectName);
		createTaskConfiguration(wc);
	}

	protected void createProjectNameConfiguration(ILaunchConfigurationWorkingCopy wc, String projectName) {
		wc.setAttribute(PROPERTY_PROJECTNAME, projectName);
	}

	protected void createTaskConfiguration(ILaunchConfigurationWorkingCopy wc) {
		wc.setAttribute(PROPERTY_TASKS, "assemble");
	}

	private String createProjectName(IResource resource) {
		IProject project = resource.getProject();
		if (EGradleUtil.isVirtualRootProject(project)){
			return "";
		}
		try {
			File projectRealFolderName = FileHelper.SHARED.toFile(project.getLocation());
			return projectRealFolderName.getName();
		} catch (CoreException e) {
			throw new IllegalStateException(e);
		}
	}

	protected ILaunchConfiguration findLaunchConfiguration(IResource resource, ILaunchConfigurationType configType) {
		List<ILaunchConfiguration> configs = getCandidates(resource, configType);
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
	 * @param type
	 *            the type
	 * @param ctype
	 *            the {@link ILaunchConfigurationType}
	 * @return the list of {@link ILaunchConfiguration}s or an empty list, never
	 *         <code>null</code>
	 * @since 3.8
	 */
	List<ILaunchConfiguration> getCandidates(IResource type, ILaunchConfigurationType ctype) {
		List<ILaunchConfiguration> candidateConfigs = Collections.emptyList();
		try {
			String projectName = createProjectName(type);
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(ctype);
			candidateConfigs = new ArrayList<ILaunchConfiguration>(configs.length);
			for (int i = 0; i < configs.length; i++) {
				ILaunchConfiguration config = configs[i];
				if (config.getAttribute(PROPERTY_PROJECTNAME, "")
						.equals(projectName)) {
					candidateConfigs.add(config);
				}
			}
		} catch (CoreException e) {
			EGradleUtil.log(e);
		}
		return candidateConfigs;
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
		return EGradleUtil.getActiveWorkbenchShell();
	}

	/**
	 * Returns a title for a type selection dialog used to prompt the user when
	 * there is more than one type that can be launched.
	 * 
	 * @return type selection dialog title
	 */
	protected String getTypeSelectionTitle() {
		return "There is more than one type being launchable:";
	}

	protected void launch(IResource type, String mode) {
		List<ILaunchConfiguration> configs = getCandidates(type, getConfigurationType());
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
				config = createConfiguration(type);
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
	 *            the java elements to consider for a type that can be launched
	 * @param mode
	 *            launch mode
	 * @param selectTitle
	 *            prompting title for choosing a type to launch
	 * @param emptyMessage
	 *            error message when no types are resolved for launching
	 */
	private void searchAndLaunch(Object[] resources, String mode, String selectTitle, String emptyMessage) {
		IResource resource = null;
		Object object = resources[0];
		// FIXME ATR, 12.08.2016 - ugly implemented. first shot.
		if (object instanceof IResource) {
			resource = (IResource) object;
		} else if (object instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) object;
			resource = getLaunchableResource(adaptable);
		} else {
			throw new IllegalArgumentException("Cannot handle object:" + object);
		}
		if (resource != null) {
			launch(resource, mode);
		}
	}
}
