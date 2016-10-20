package de.jcup.egradle.eclipse.gradleeditor.document.keywords;

public enum GradleLinkKeyWords implements DocumentKeyWord {

	APPLY_FROM("apply from"),
	
	APPLY_PLUGIN("apply plugin"),
	
	;

	private String text;

	private GradleLinkKeyWords(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}
}
