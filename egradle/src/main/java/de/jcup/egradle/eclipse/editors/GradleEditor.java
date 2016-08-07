package de.jcup.egradle.eclipse.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class GradleEditor extends TextEditor {

	private ColorManager colorManager;

	public GradleEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new GradleSourceViewerConfiguration(colorManager));
		setDocumentProvider(new XMLDocumentProvider());
	}
	@Override
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
