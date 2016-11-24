package de.jcup.egradle.eclipse.gradleeditor.handlers;

import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;

public class OpenQuickOutlineHandler extends AbstractEGradleEditorHandler {

	public static final String COMMAND_ID = "egradle.editor.commands.quickoutline";

	@Override
	protected void executeOnGradleEditor(GradleEditor gradleEditor) {
		gradleEditor.openQuickOutline();
	}


}
