package de.jcup.egradle.eclipse.gradleeditor.document;
import org.eclipse.jface.text.rules.IWordDetector;

public class OnlyLettersKeyWordDetector implements IWordDetector{
	
	@Override
	public boolean isWordStart(char c) {
		if (! Character.isLetter(c)){
			return false;
		}
		return true;
	}

	@Override
	public boolean isWordPart(char c) {
		if (Character.isWhitespace(c)){
			return false;
		}
		if (! Character.isLetter(c)){
			return false;
		}
		return true;
	}
}
