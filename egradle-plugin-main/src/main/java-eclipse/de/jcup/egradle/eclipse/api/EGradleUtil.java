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
import static de.jcup.egradle.eclipse.preferences.PreferenceConstants.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.debug.ui.IJavaDebugUIConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.Bundle;

import de.jcup.egradle.core.Constants;
import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.virtualroot.VirtualProjectCreator;
import de.jcup.egradle.core.virtualroot.VirtualRootProjectException;
import de.jcup.egradle.eclipse.Activator;
import de.jcup.egradle.eclipse.EGradleMessageDialog;
import de.jcup.egradle.eclipse.decorators.EGradleProjectDecorator;
import de.jcup.egradle.eclipse.virtualroot.EclipseVirtualProjectPartCreator;

public class EGradleUtil {

	private static VirtualProjectCreator virtualProjectCreator = new VirtualProjectCreator();
	
	/**
	 * Get image by path from image registry. If not already registered a new
	 * image will be created and registered. If not createable a fallback image
	 * is used instead
	 * 
	 * @param path
	 * @return image
	 */
	public static Image getImage(String path) {
		return getImage(path, Activator.PLUGIN_ID);
	}

	/**
	 * Get image by path from image registry. If not already registered a new
	 * image will be created and registered. If not createable a fallback image
	 * is used instead
	 * 
	 * @param path
	 * @param pluginId
	 *            - plugin id to identify which plugin image should be loaded
	 * @return image
	 */
	public static Image getImage(String path, String pluginId) {
		ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
		Image image = imageRegistry.get(path);
		if (image == null) {
			ImageDescriptor imageDesc = createImageDescriptor(path, pluginId);
			image = imageDesc.createImage();
			if (image == null) {
				image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
			}
			imageRegistry.put(path, image);
		}
		return image;
	}

	public static ImageDescriptor createImageDescriptor(String path) {
		return createImageDescriptor(path, Activator.PLUGIN_ID);
	}

	public static ImageDescriptor createImageDescriptor(String path, String pluginId) {
		Bundle bundle = Platform.getBundle(pluginId);

		URL url = FileLocator.find(bundle, new Path(path), null);

		ImageDescriptor imageDesc = ImageDescriptor.createFromURL(url);
		return imageDesc;
	}

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
		if (window == null) {
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

	public static void log(IStatus status) {
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
	 * Returns gradle root project. if nothing defined an error dialog appears
	 * and shows information
	 * 
	 * @return root project or <code>null</code>
	 */
	public static GradleRootProject getRootProject() {
		return getRootProject(true);
	}

	private static GradleRootProject getRootProject(boolean showErrorDialog) {
		String path = PREFERENCES.getStringPreference(P_ROOTPROJECT_PATH);
		if (StringUtils.isEmpty(path)) {
			if (showErrorDialog) {
				EGradleMessageDialog.INSTANCE.showError("No root project path set. Please setup in preferences!");
			}
			return null;
		}
		GradleRootProject rootProject;
		try {
			rootProject = new GradleRootProject(new File(path));
		} catch (IOException e1) {
			if (showErrorDialog) {
				EGradleMessageDialog.INSTANCE.showError(e1.getMessage());
			}
			return null;
		}
		return rootProject;
	}

	public static void safeAsyncExec(Runnable runnable) {
		getSafeDisplay().asyncExec(runnable);
	}

	public static Display getSafeDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	public static void throwCoreException(String message) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));

	}

	private static final IProgressMonitor NULL_PROGESS = new NullProgressMonitor();

	public static void refreshAllProjectDecorations() {
		getSafeDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				IWorkbench workbench = PlatformUI.getWorkbench();
				if (workbench == null) {
					return;
				}
				IDecoratorManager manager = workbench.getDecoratorManager();
				
				EGradleProjectDecorator decorator = (EGradleProjectDecorator) manager.getBaseLabelProvider("de.jcup.egradle.eclipse.decorators.EGradleProjectDecorator");
				IProject[] projects = getAllProjects();
				/* test if virtual root project is visible */
				for (IProject project: projects){
					String name = project.getName();
					if (Constants.VIRTUAL_ROOTPROJECT_NAME.equals(name)){
						/* ok found - so recreate ...*/
						try {
							createOrRecreateVirtualRootProject();
						} catch (VirtualRootProjectException e) {
							log(e);
						}
						break;
					}
				}
				if(decorator != null){ // decorator is enabled
					
				    LabelProviderChangedEvent event = new LabelProviderChangedEvent(decorator,projects);
				    decorator.fireLabelProviderChanged(event);
				}
			}

		});

	}

	public static void refreshAllProjects() {
		refreshAllProjects(null);
	}

	public static void refreshAllProjects(IProgressMonitor monitor) {
		if (monitor == null) {
			monitor = NULL_PROGESS;
		}
		IProject[] projects = getAllProjects();
		for (IProject project : projects) {
			try {
				if (monitor.isCanceled()) {
					break;
				}
				monitor.subTask("refreshing project " + project.getName());
				project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			} catch (CoreException e) {
				log(e);
			}
		}

	}

	public static IProject[] getAllProjects() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		return projects;
	}
	
	/**
	 * Creates or recreates virtual project 
	 * @throws VirtualRootProjectException
	 */
	public static void createOrRecreateVirtualRootProject() throws VirtualRootProjectException {
		GradleRootProject rootProject = EGradleUtil.getRootProject();
		Job job = new Job("Virtual root project") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				EclipseVirtualProjectPartCreator partCreator = new EclipseVirtualProjectPartCreator(rootProject,monitor);
				try {
					virtualProjectCreator.createOrUpdate(rootProject, partCreator);
					return Status.OK_STATUS;
				} catch (VirtualRootProjectException e) {
					EGradleUtil.log(e);
					return Status.CANCEL_STATUS;
				}
			}
		};
		job.schedule();
		
	}

	/**
	 * Gets the egradle temp folder (user.home/.egradle). If not existing the folder will be created
	 * @return temp folder never <code>null</code> and always existing
	 */
	public static File getTempFolder() {
		return getTempFolder(null);
	}
	
	/**
	 * Gets the egradle temp folder (user.home/.egradle/$subfolder). If not existing the folder will be created
	 * @param subFolder subfoler inside egradle tempfolder. If <code>null</code> the egradle temp folder will be returned
	 * @return temp folder never <code>null</code> and always existing
	 */
	public static File getTempFolder(String subFolder) {
		String userHome = System.getProperty("user.home");
		
		StringBuilder sb = new StringBuilder();
		sb.append(userHome);
		sb.append("/.egradle");
		if (StringUtils.isNotBlank(subFolder)){
			sb.append("/");
			sb.append(subFolder);
		}
		
		String path = sb.toString();
		
		File egradleTempFolder = new File(path);
		if (!egradleTempFolder.exists()){
			egradleTempFolder.mkdirs();
			if(!egradleTempFolder.exists()){
				throw new IllegalStateException("Was not able to create egradle temp folder:"+path);
			}
		}
		return egradleTempFolder;
	}
	

}
