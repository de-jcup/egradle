package de.jcup.egradle.eclipse.ui;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.jcup.egradle.core.api.Matcher;

public abstract class AbstractTreeViewerFilter<T> extends ViewerFilter {

	private Matcher<T> matcher;
	
	public AbstractTreeViewerFilter() {
		super();
	}

	public void setMatcher(Matcher<T> matcher) {
		this.matcher = matcher;
	}

	@Override
	public Object[] filter(Viewer viewer, TreePath parentPath, Object[] elements) {
		int size = elements.length;
		ArrayList<Object> out = new ArrayList<>(size);
		for (int i = 0; i < size; ++i) {
			Object element = elements[i];
			if (selectTreePath(viewer, parentPath, element)) {
				out.add(element);
			}
		}
		return out.toArray();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return selectTreePath(viewer, new TreePath(new Object[] { parentElement }), element);
	}

	public boolean selectTreePath(Viewer viewer, TreePath parentPath, Object element) {
		// Cut off children of elements that are shown repeatedly.
		for (int i = 0; i < parentPath.getSegmentCount() - 1; i++) {
			if (element.equals(parentPath.getSegment(i))) {
				return false;
			}
		}
	
		if (!(viewer instanceof TreeViewer)) {
			return true;
		}
		if (matcher == null){
			return true;
		}
		TreeViewer treeViewer = (TreeViewer) viewer;
		Boolean matchingResult = isMatching(element);
		if (matchingResult!=null){
			return matchingResult;
		}
		return hasUnfilteredChild(treeViewer, parentPath, element);
	}

	@SuppressWarnings("unchecked")
	public Boolean isMatching(Object element) {
		T item =null; 
		try{
			item = (T) element;
		}catch(ClassCastException e){
			return Boolean.FALSE;
		}
		if (matcher.matches(item)){
			return Boolean.TRUE;
		}
		/* maybe children are matching*/
		return null;
	}

	private boolean hasUnfilteredChild(TreeViewer viewer, TreePath parentPath, Object element) {
		TreePath elementPath = parentPath.createChildPath(element);
		IContentProvider contentProvider = viewer.getContentProvider();
		Object[] children = contentProvider instanceof ITreePathContentProvider
				? ((ITreePathContentProvider) contentProvider).getChildren(elementPath)
				: ((ITreeContentProvider) contentProvider).getChildren(element);
		for (int i = 0; i < children.length; i++) {
			if (selectTreePath(viewer, elementPath, children[i])) {
				return true;
			}
		}
		return false;
	}

}