/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.egradle.eclipse.ui;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import de.jcup.egradle.eclipse.preferences.IEditorPreferences;

public class GroovyBracketInsertionCompleter extends KeyAdapter {

	private final IExtendedEditor gradleEditor;
	private IEditorPreferences preferences;

	public GroovyBracketInsertionCompleter(IExtendedEditor gradleEditor, IEditorPreferences preferences) {
		this.gradleEditor = gradleEditor;
		this.preferences=preferences;
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
		if (selectionProvider==null){
			return;
		}
		ISelection selection = selectionProvider.getSelection();
		if (! (selection instanceof ITextSelection)) {
			return;
		}
		boolean enabled = preferences.isEditorAutoCreateEndBracketsEnabled();
		if (!enabled){
			return;
		}
		ITextSelection textSelection = (ITextSelection) selection;
		int offset = textSelection.getOffset();
		
		
		try {
			IDocument document = this.gradleEditor.getDocument();
			if (document==null){
				return;
			}
			document.replace(offset-1, 1, "{ }");
			selectionProvider.setSelection(new TextSelection(offset+1, 0));
		} catch (BadLocationException e1) {
			/* ignore*/
			return;
		}
		
	}
}