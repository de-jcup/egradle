package de.jcup.egradle.eclipse.decorators;

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
import de.jcup.egradle.eclipse.api.FileHelper;

public class EGradleMarkRootProjectProjectChildsDecorator extends LabelProvider implements ILightweightLabelDecorator {

	private static ImageDescriptor egradleProjectDescriptor = new LazyImageDescriptor(
			EGradleUtil.createImageDescriptor("icons/gradle-project-decorator.gif"));

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
		IPath path = p.getLocation();

		/* we simply check if the project is inside root project */
		try {
			File parentFolder = FileHelper.SHARED.toFile(path);
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

			decoration.addOverlay(egradleProjectDescriptor, IDecoration.TOP_LEFT);
		} catch (CoreException e) {
			EGradleUtil.log(e);
		}

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
