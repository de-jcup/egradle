package de.jcup.egradle.eclipse.gradleeditor.handlers;

import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;

public class GotoMatchingBracketHandler extends AbstractEGradleEditorHandler {

	public static final String COMMAND_ID = "egradle.editor.commands.gotomatchingbracket";

	@Override
	protected void executeOnGradleEditor(GradleEditor gradleEditor) {
		gradleEditor.gotoMatchingBracket();
	}


}
