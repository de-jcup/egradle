package de.jcup.egradle.eclipse.gradleeditor.document;

public enum GradleTaskKeyWords implements DocumentKeyWord {

	TASKDEFINITION("task"),
	
	DO_FIRST("<<"),
	
	DO_LAST("doLast"),
	;

	private String text;

	private GradleTaskKeyWords(String text) {
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
