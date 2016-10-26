package de.jcup.egradle.eclipse.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.texteditor.MarkerUtilities;

import de.jcup.egradle.eclipse.api.EGradleUtil;

/**
 * Unpersisted marker helper is a helper object for markers. "Unpersisted",
 * because the created markers are recognized and added to a list. So they can
 * all be removed by the helper at once (this is normally not possible). But the handling inside 
 * the list does need the markers to be non persisted!!!
 * 
 * @author albert
 *
 */
public class UnpersistedMarkerHelper {

	private List<IMarker> createdMarkers = new ArrayList<>();
	private String markerType;

	public UnpersistedMarkerHelper(String markerType) {
		this.markerType = markerType;
	}

	IMarker findMarker(IResource resource, String message, int lineNumber, String type) throws CoreException {
		IMarker[] marker = resource.findMarkers(type, true, IResource.DEPTH_ZERO);
		for (int i = 0; i < marker.length; i++) {
			try {
				if (marker[i].getAttribute(IMarker.LINE_NUMBER).toString().equals(String.valueOf(lineNumber))
						&& marker[i].getAttribute(IMarker.MESSAGE).equals(message))
					return marker[i];
			} catch (NullPointerException ex) {
				continue;
			}
		}
		return null;
	}

	/**
	 * Removes all created error markers
	 * @throws CoreException
	 */
	public void removeAllErrorMarkers() throws CoreException {
		List<IMarker>workingCopy = new ArrayList<>(createdMarkers);
		for (IMarker marker : workingCopy) {
			if (IMarker.TASK.equals(marker.getType())){
				/* tasks are not deleted */
				continue;
			}
			marker.delete();
			createdMarkers.remove(marker);
		}
	}

	public void createErrorMarker(IResource resource, String message, int lineNumber, int charStart, int charEnd)
			throws CoreException {
		createMarker(resource, message, lineNumber, markerType, IMarker.SEVERITY_ERROR, charStart, charEnd);
	}

	public void createErrorMarker(IResource resource, String message, int lineNumber) throws CoreException {
		createMarker(resource, message, lineNumber, markerType, IMarker.SEVERITY_ERROR, -1, -1);
	}

	public void createMarker(IResource resource, String message, int lineNumber, String markerType, int severity,
			int charStart, int charEnd) throws CoreException {
		if (lineNumber <= 0)
			lineNumber = 1;
		IMarker marker = findMarker(resource, message, lineNumber, markerType);
		if (marker == null) {
			HashMap<String, Object> map = new HashMap<>();
			map.put(IMarker.SEVERITY, new Integer(severity));
			map.put(IMarker.LOCATION, resource.getFullPath().toOSString());
			map.put(IMarker.MESSAGE, message);
			MarkerUtilities.setLineNumber(map, lineNumber);
			MarkerUtilities.setMessage(map, message);
			if (charStart != -1) {
				MarkerUtilities.setCharStart(map, charStart);
				MarkerUtilities.setCharEnd(map, charEnd);
			}
			internalCreateMarker(resource, map, markerType);
		}
	}

	/**
	 * Creates a marker on the given resource with the given type and
	 * attributes.
	 * <p>
	 * This method modifies the workspace (progress is not reported to the
	 * user).
	 * </p>
	 *
	 * @param resource
	 *            the resource
	 * @param attributes
	 *            the attribute map
	 * @param markerType
	 *            the type of marker
	 * @throws CoreException
	 *             if this method fails
	 * @see IResource#createMarker(java.lang.String)
	 */
	protected void internalCreateMarker(final IResource resource, final Map<String, Object> attributes,
			final String markerType) throws CoreException {

		IWorkspaceRunnable r = new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource.createMarker(markerType);
				marker.setAttributes(attributes);
				createdMarkers.add(marker);

			}
		};

		resource.getWorkspace().run(r, null, IWorkspace.AVOID_UPDATE, null);
	}

	public void removeMarkers(IFile file) {
		removeMarkers(file, markerType);
		removeMarkers(file, IMarker.TASK);
		createdMarkers.remove(file);

	}

	private IMarker[] removeMarkers(IFile file, String markerType) {
		if (file == null) {
			/* maybe sync problem - guard close */
			return new IMarker[] {};
		}
		IMarker[] tasks = null;
		if (file != null) {
			try {
				tasks = file.findMarkers(markerType, true, IResource.DEPTH_ZERO);
				for (int i = 0; i < tasks.length; i++) {
					tasks[i].delete();
				}

			} catch (CoreException e) {
				EGradleUtil.log(e);
			}
		}
		if (tasks == null) {
			tasks = new IMarker[] {};
		}
		return tasks;
	}

	public void createTodoMarker(IFile resource, String message, int lineNumber) throws CoreException {
		createMarker(resource, message, lineNumber, IMarker.TASK, IMarker.SEVERITY_INFO, -1, -1);
	}

	public boolean hasErrors() {
		return ! createdMarkers.isEmpty();
	}

}