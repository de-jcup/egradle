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
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import de.jcup.egradle.codeassist.BracketInsertion;
import de.jcup.egradle.codeassist.SourceCodeInsertionSupport;
import de.jcup.egradle.codeassist.SourceCodeInsertionSupport.InsertionData;
import de.jcup.egradle.core.util.TextUtil;
import de.jcup.egradle.eclipse.document.GroovyDocumentIdentifiers;
import de.jcup.egradle.eclipse.preferences.IEditorPreferences;
import de.jcup.egradle.eclipse.util.EclipseUtil;

public class GroovyBracketInsertionCompleter extends KeyAdapter {

    private SourceCodeInsertionSupport support;

    private final IExtendedEditor gradleEditor;
    private IEditorPreferences preferences;

    public GroovyBracketInsertionCompleter(IExtendedEditor gradleEditor, IEditorPreferences preferences) {
        this.gradleEditor = gradleEditor;
        this.preferences = preferences;
        this.support = new SourceCodeInsertionSupport();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        final BracketInsertion data = BracketInsertion.valueOfStartChar(e.character);
        if (data == null) {
            return;
        }
        ISelectionProvider selectionProvider = this.gradleEditor.getSelectionProvider();
        if (selectionProvider == null) {
            return;
        }
        ISelection selection = selectionProvider.getSelection();
        if (!(selection instanceof ITextSelection)) {
            return;
        }
        boolean enabled = preferences.isEditorAutoCreateEndBracketsEnabled();
        if (!enabled) {
            return;
        }
        ITextSelection textSelection = (ITextSelection) selection;
        int offset = textSelection.getOffset();
        if (offset == -1) {
            return;
        }
        IDocument document = gradleEditor.getDocument();
        if (document == null) {
            return;
        }
        try {
            String contentType = document.getContentType(offset);
            if (GroovyDocumentIdentifiers.STRING.getId().equals(contentType) || GroovyDocumentIdentifiers.GSTRING.getId().equals(contentType))
                return;
        } catch (BadLocationException ex) {
            /* ignore */
            EclipseUtil.logError("Cannot get contentType", ex);
            return;
        }
		try {
			String charBeforBracket = document.get(offset - 2, 1);
			if (charBeforBracket.equals("$") && e.character == '{') {
				document.replace(offset , 0, "}");
				selectionProvider.setSelection(new TextSelection(offset, 0));
				return;
			}
		} catch (BadLocationException e2) {
			e2.printStackTrace();
            EclipseUtil.logError("Cannot set ${}", e2);
            return;
		}
      
		EclipseUtil.safeAsyncExec(new Runnable() {

            @Override
            public void run() {
                try {

                    if (data.isMultiLine()) {
                        insertMultiLiner(data, selectionProvider, offset, document);
                    } else {
                        insertOneLiner(data, selectionProvider, offset, document);
                    }
                } catch (BadLocationException e1) {
                    /* ignore */
                    EclipseUtil.logError("Cannot set content", e1);
                    return;
                }
            }

        });

    }

    private void insertOneLiner(BracketInsertion data, ISelectionProvider selectionProvider, int offset, IDocument document) throws BadLocationException {
        document.replace(offset - 1, 1, data.createOneLineTemplate());
        selectionProvider.setSelection(new TextSelection(data.createOneLineNewOffset(offset), 0));
    }

    private void insertMultiLiner(BracketInsertion data, ISelectionProvider selectionProvider, int offset, IDocument document) throws BadLocationException {
        IRegion region = document.getLineInformationOfOffset(offset);
        if (region == null) {
            return;
        }
        int length = region.getLength();

        String textBeforeColumn = document.get(offset - length, length - 1); // -1
                                                                             // to
                                                                             // get
                                                                             // not
                                                                             // he
                                                                             // bracket
                                                                             // itself
        String relevantColumnsBefore = TextUtil.trimRightWhitespaces(textBeforeColumn);
        InsertionData result = support.prepareInsertionString(data.createMultiLineTemplate(SourceCodeInsertionSupport.CURSOR_VARIABLE), relevantColumnsBefore);

        document.replace(offset - 1, 1, result.getSourceCode());
        selectionProvider.setSelection(new TextSelection(offset + result.getCursorOffset() - 1, 0));

    }
}