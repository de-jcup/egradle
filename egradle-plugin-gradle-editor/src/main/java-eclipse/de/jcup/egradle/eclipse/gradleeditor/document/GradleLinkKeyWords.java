package de.jcup.egradle.eclipse.gradleeditor.document;

public enum GradleLinkKeyWords implements DocumentKeyWord {

	APPLY_FROM("apply from"),
	
	APPLY_PLUGIN("apply plugin"),
	
	;

	private String text;

	private GradleLinkKeyWords(String text) {
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
