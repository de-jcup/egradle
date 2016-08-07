package de.jcup.egradle.eclipse.editors;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class XMLWhitespaceDetector implements IWhitespaceDetector {

	@Override
	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}
