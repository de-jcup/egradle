package de.jcup.egradle.eclipse.gradleeditor.outline;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;

public class GradleEditorContentOutlinePage extends ContentOutlinePage{

	private GradleEditor gradleEditor;

	public GradleEditorContentOutlinePage(GradleEditor gradleEditor) {
		this.gradleEditor = gradleEditor;
	 }

	public void createControl(Composite parent) {
	      super.createControl(parent);
	      
	      TreeViewer viewer= getTreeViewer();
	      viewer.setContentProvider(new GradleEditorOutlineContentProvider());
	      viewer.setLabelProvider(new GradleEditorOutlineLabelProvider());
	      viewer.addSelectionChangedListener(this);
	      viewer.setInput(gradleEditor.getEditorInput());
	   }
}
