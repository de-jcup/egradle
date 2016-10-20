package de.jcup.egradle.eclipse.gradleeditor.document.keywords;

public enum GroovyKeyWords implements DocumentKeyWord {

	DEF("def"),
	
	;

	private String text;

	private GroovyKeyWords(String text) {
		this.text = text;
	}


	@Override
	public String getText() {
		return text;
	}
}
