package de.jcup.egradle.eclipse.gradleeditor.outline;

import org.eclipse.jface.viewers.ITreeContentProvider;

public class FallbackOutlineContentProvider implements ITreeContentProvider{

	private static final Object[] NO_CHILDREN = new Object[]{};
	private static final Object[] ROOT_ELEMENTS = new Object[]{"No content available"};

	@Override
	public Object[] getElements(Object inputElement) {
		return ROOT_ELEMENTS;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return NO_CHILDREN;
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
