package de.jcup.egradle.eclipse.gradleeditor.document;

public enum GradleSimpleKeyWords implements DocumentKeyWord {

	FROM("from"),
	
	TYPE("type:"),

	DEPENDENCIES("dependencies"),
	
	REPOSITORIES("repositories"),

	ALL_PROJECTS("allprojects"),

	SUB_PROJECTS("subprojects"),
	
	ARTIFACTS("artifacts"),
	
	;

	private String text;

	private GradleSimpleKeyWords(String text) {
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
