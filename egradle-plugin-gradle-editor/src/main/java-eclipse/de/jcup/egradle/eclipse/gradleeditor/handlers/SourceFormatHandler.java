package de.jcup.egradle.eclipse.gradleeditor.handlers;

import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;

public class SourceFormatHandler extends AbstractEGradleEditorHandler {

	public static final String COMMAND_ID = "egradle.editor.commands.sourceformat";

	@Override
	protected void executeOnGradleEditor(GradleEditor gradleEditor) {
		gradleEditor.formatSource();
	}


}
