package de.jcup.egradle.eclipse.gradleeditor.document;
import org.eclipse.jface.text.rules.IWordDetector;

public class JavaWordDetector implements IWordDetector{
	
	@Override
	public boolean isWordStart(char c) {
		return Character.isJavaIdentifierStart(c);
	}

	@Override
	public boolean isWordPart(char c) {
		return Character.isJavaIdentifierPart(c);
	}
}
