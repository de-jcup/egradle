package de.jcup.egradle.eclipse.gradleeditor.document.keywords;

// see http://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html
public enum JavaLiteralKeyWords implements DocumentKeyWord {

	
	NULL("null"),
	
	TRUE("true"),
	
	FALSE("false")

	;

	private String text;

	private JavaLiteralKeyWords(String text) {
		this.text = text;
	}


	@Override
	public String getText() {
		return text;
	}
}
