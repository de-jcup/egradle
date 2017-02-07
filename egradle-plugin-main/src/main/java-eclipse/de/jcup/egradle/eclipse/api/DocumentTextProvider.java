package de.jcup.egradle.eclipse.api;

import static org.apache.commons.lang3.Validate.*;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import de.jcup.egradle.core.TextProvider;
import de.jcup.egradle.core.TextProviderException;

/**
 * Document text provider uses eclipse document
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
