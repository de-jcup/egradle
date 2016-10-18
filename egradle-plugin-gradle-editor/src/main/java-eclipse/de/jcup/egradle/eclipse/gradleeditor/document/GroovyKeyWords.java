package de.jcup.egradle.eclipse.gradleeditor.document;

public enum GroovyKeyWords implements DocumentKeyWord {

	DEF("def"),
	
	ASSERT("assert"),
	
	NEW("new ")

	;

	private String text;

	private GroovyKeyWords(String text) {
		this.text = text;
	}


	@Override
	public boolean isWordStart(char c) {
		return text.charAt(0) == c;
	}

	@Override
	public boolean isWordPart(char c) {
		return text.indexOf(c) != -1;
	}

	@Override
	public String getText() {
		return text;
	}
}
