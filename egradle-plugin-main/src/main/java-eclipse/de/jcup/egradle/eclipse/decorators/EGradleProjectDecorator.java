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
 package de.jcup.egradle.eclipse.decorators;

import static de.jcup.egradle.eclipse.api.EGradleUtil.*;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.PlatformUI;

import de.jcup.egradle.eclipse.Activator;
import de.jcup.egradle.eclipse.api.EGradleUtil;

/**
 * Dedicated decorator for projects only. Will only decorate subprojects inside
 * gradle root project folder and also the virtual project as well
 * 
 * @author Albert Tregnaghi
 *
 */
public class EGradleProjectDecorator extends LabelProvider implements ILightweightLabelDecorator {

	private static ImageDescriptor egradleProjectDescriptor = new LazyImageDescriptor(
			EGradleUtil.createImageDescriptor("icons/gradle-project-decorator.gif"));

	private static ImageDescriptor emptyDecoratorDescriptor = new LazyImageDescriptor(EGradleUtil.createImageDescriptor("icons/empty-4x4-decorator.gif"));
	
	@Override
	public void decorate(Object element, IDecoration decoration) {
		/* no decoration when plugin is not running */
		if (Activator.getDefault() == null) {
			return;
		}

		/* no decoration when workbench is not running */
		if (!PlatformUI.isWorkbenchRunning()) {
			return;
		}

		/* do not decoratore if its not a project */
		if (!(element instanceof IProject)) {
			return;
		}

		IProject p = (IProject) element;
		File rootFolder = EGradleUtil.getRootProjectFolderWithoutErrorHandling();
		if (rootFolder == null) {
			return;
		}
		if (EGradleUtil.hasVirtualRootProjectNature(p)){
			decoration.addPrefix("EGradle ");
			decoration.addSuffix(" ("+rootFolder.getName()+")");
			decorateImage(decoration);
			/* Because the virtual root project is not hosted in SCM - at least with EGit, there is always an
			 * ugly question icon at IDecoration.BOTTOM_RIGHT . To avoid this
			 * we simply render an empty deocorator here. This is okay, because diff 
			 * changes in project are rendered as usual
			 */
			decoration.addOverlay(emptyDecoratorDescriptor, IDecoration.BOTTOM_RIGHT);
			return;
		}
		IPath path = p.getLocation();

		/* we simply check if the project is inside root project */
		try {
			File parentFolder = getResourceHelper().toFile(path);
			if (parentFolder == null) {
				return;
			}
			if (!parentFolder.exists()) {
				return;
			}
			if (!rootFolder.equals(parentFolder)) {
				parentFolder = parentFolder.getParentFile();
			}
			if (!rootFolder.equals(parentFolder)) {
				return;
			}
			if (EGradleUtil.getPreferences().isSubProjectIconDecorationEnabled()){
				decorateImage(decoration);
			}
		} catch (CoreException e) {
			EGradleUtil.log(e);
		}

	}

	private void decorateImage(IDecoration decoration) {
		// TOP_LEFT
		decoration.addOverlay(egradleProjectDescriptor, IDecoration.TOP_LEFT);
	}

	@Override
	public void fireLabelProviderChanged(LabelProviderChangedEvent event) {
		super.fireLabelProviderChanged(event);
	}

	/**
	 * An image descriptor which only creates the image data once - no matter
	 * what the origin image descriptor does
	 */
	private static class LazyImageDescriptor extends ImageDescriptor {
		ImageDescriptor descriptor;

		ImageData data;

		private LazyImageDescriptor(ImageDescriptor descriptor) {
			this.descriptor = descriptor;
		}

		@Override
		public ImageData getImageData() {
			if (data == null) {
				data = descriptor.getImageData();
			}
			return data;
		}
	}
}
