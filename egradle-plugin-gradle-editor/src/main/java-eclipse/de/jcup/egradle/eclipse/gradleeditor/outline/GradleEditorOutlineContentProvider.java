package de.jcup.egradle.eclipse.gradleeditor.outline;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.IFileEditorInput;

public class GradleEditorOutlineContentProvider implements ITreeContentProvider {

	private static Object[] EMPTY = new Object[] {};

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IFileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput) inputElement;
			return new Object[] { "file:" + fileEditorInput.getFile() };

		}
		return new Object[] { "no content" };
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return EMPTY;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}

}
