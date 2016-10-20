package de.jcup.egradle.eclipse.gradleeditor.document.keywords;

public enum GradleTaskKeyWords implements DocumentKeyWord{

	TASKDEFINITION("task"),
	
	DO_FIRST("<<"),
	
	DO_LAST("doLast"),
	;

	private String text;

	private GradleTaskKeyWords(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}
}
