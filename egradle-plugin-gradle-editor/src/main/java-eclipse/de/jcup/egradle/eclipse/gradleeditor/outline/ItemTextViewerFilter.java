package de.jcup.egradle.eclipse.gradleeditor.outline;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osgi.util.TextProcessor;

import de.jcup.egradle.core.api.Matcher;
import de.jcup.egradle.core.model.Item;

class ItemTextViewerFilter extends ViewerFilter {
	private Matcher<String> matcher;

	public ItemTextViewerFilter() {
	}

	public void setMatcher(Matcher<String> matcher) {
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

	/**
	 * Returns whether the given element makes it through this filter.
	 *
	 * @param viewer
	 *            the viewer
	 * @param parentPath
	 *            the parent element path
	 * @param element
	 *            the element
	 * @return <code>true</code> if element is included in the filtered set, and
	 *         <code>false</code> if excluded
	 */
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
	/**
	 * 
	 * @param element
	 * @return {@link Boolean#TRUE} when matching, {@link Boolean#FALSE} when not an item or not matching, <code>null</code> when not matching - but maybe a child 
	 */
	public Boolean isMatching(Object element){
		if (!( element instanceof Item)){
			return Boolean.FALSE;
		}
		Item item = (Item) element;
		String matchName = item.getName();
		matchName = TextProcessor.deprocess(matchName);
		if (matchName != null && matcher.matches(matchName)){
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