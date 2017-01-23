package de.jcup.egradle.eclipse.gradleeditor.handlers;

import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;

public class ToggleCommentHandler extends AbstractEGradleEditorHandler{

	@Override
	protected void executeOnGradleEditor(GradleEditor gradleEditor) {
		gradleEditor.toggleComment();
	}


}
