package de.jcup.egradle.eclipse.ui;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.MarkerUtilities;

import de.jcup.egradle.eclipse.api.EGradleUtil;

public class MarkerHelper {
	
	public static final String PROBLEM_MARKERTYPE = "de.jcup.egradle.problem";

	IMarker findMarker(IResource resource, String message, int lineNumber, String type)
			throws CoreException {
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

	public void createErrorMarker(IResource resource, String message, int lineNumber, int charStart, int charEnd)
			throws CoreException {
		createMarker(resource, message, lineNumber, PROBLEM_MARKERTYPE, IMarker.SEVERITY_ERROR, charStart, charEnd);
	}

	public void createErrorMarker(IResource resource, String message, int lineNumber) throws CoreException {
		createMarker(resource, message, lineNumber, PROBLEM_MARKERTYPE, IMarker.SEVERITY_ERROR, -1, -1);
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
			MarkerUtilities.createMarker(resource, map, markerType);
		}
	}

	public void removeMarkers(IFile file) {
		removeMarkers(file, PROBLEM_MARKERTYPE);
		removeMarkers(file, IMarker.TASK);
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

	public  void createTodoMarker(IFile resource, String message, int lineNumber) throws CoreException {
		createMarker(resource, message, lineNumber, IMarker.TASK, IMarker.SEVERITY_INFO, -1, -1);
	}
}