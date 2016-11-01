package de.jcup.egradle.eclipse.gradleeditor.outline;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import de.jcup.egradle.core.parser.AbstractGradleToken;
import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;

public class GradleEditorContentOutlinePage extends ContentOutlinePage {

	private GradleEditor gradleEditor;

	public GradleEditorContentOutlinePage(GradleEditor gradleEditor) {
		this.gradleEditor = gradleEditor;
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new GradleEditorOutlineContentProvider());
		viewer.setLabelProvider(new GradleEditorOutlineLabelProvider());
		viewer.addSelectionChangedListener(this);
		viewer.setInput(gradleEditor.getEditorInput());
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		super.selectionChanged(event);
		ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection){
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object firstElement = ss.getFirstElement();
			if (firstElement instanceof AbstractGradleToken){
				AbstractGradleToken gElement = (AbstractGradleToken) firstElement;
				int offset = gElement.getOffset();
				int length = gElement.getLength();

				gradleEditor.setSelectedRange(offset, length);
			}
		}
	}
}
