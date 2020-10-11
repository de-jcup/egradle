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
package de.jcup.egradle.eclipse.util;

import static org.apache.commons.lang3.Validate.*;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import de.jcup.egradle.core.TextProvider;
import de.jcup.egradle.core.TextProviderException;

/**
 * Document text provider uses eclipse document
 * 
 * @author Albert Tregnaghi
 *
 */
public class DocumentTextProvider implements TextProvider {

    private IDocument document;

    public DocumentTextProvider(IDocument document) {
        notNull(document, "'document' may not be null");
        this.document = document;
    }

    @Override
    public String getText() {
        return document.get();
    }

    @Override
    public String getText(int offset, int length) throws TextProviderException {
        try {
            return document.get(offset, length);
        } catch (BadLocationException e) {
            throw new TextProviderException("Cannot get document part for offset=" + offset + ", length=" + length, e);
        }
    }

    @Override
    public int getLineOffset(int offset) throws TextProviderException {
        int line;
        try {
            line = document.getLineOfOffset(offset);
            int offsetOfFirstCharacterInLine = document.getLineOffset(line);
            return offsetOfFirstCharacterInLine;
        } catch (BadLocationException e) {
            throw new TextProviderException("Cannot get line offset", e);
        }
    }

}
