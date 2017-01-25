package de.jcup.egradle.eclipse.gradleeditor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import static de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorPreferenceConstants.*;
import static de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorPreferences.*;

class GradleBracketInsertionCompleter extends KeyAdapter {

	private final GradleEditor gradleEditor;

	/**
	 * @param gradleEditor
	 */
	GradleBracketInsertionCompleter(GradleEditor gradleEditor) {
		this.gradleEditor = gradleEditor;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.character != '{') {
			return;
		}
		/*
		 * do not use last caret position - because the listener ordering
		 * could be different
		 */
		ISelectionProvider selectionProvider = this.gradleEditor.getSelectionProvider();
		ISelection selection = selectionProvider.getSelection();
		if (! (selection instanceof ITextSelection)) {
			return;
		}
		boolean enabled = EDITOR_PREFERENCES.getBooleanPreference(P_EDITOR_AUTO_CREATE_END_BRACKETSY);
		if (!enabled){
			return;
		}
		ITextSelection textSelection = (ITextSelection) selection;
		int offset = textSelection.getOffset();
		
		
		try {
			IDocument document = this.gradleEditor.getDocument();
			document.replace(offset-1, 1, "{ }");
			selectionProvider.setSelection(new TextSelection(offset+1, 0));
		} catch (BadLocationException e1) {
			/* ignore*/
			return;
		}
		
	}
}