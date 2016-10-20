package de.jcup.egradle.eclipse.gradleeditor.document.keywords;

public enum GradleDefaultClosureKeyWords implements DocumentKeyWord {

	FROM("from"),
	
	INTO("into"),
	
	INCLUDE("include"),
	
	EXCLUDE("exclude"),
	
	DEPENDENCIES("dependencies"),
	
	REPOSITORIES("repositories"),

	ALL_PROJECTS("allprojects"),

	SUB_PROJECTS("subprojects"),
	
	ARTIFACTS("artifacts"),
	
	;

	private String text;

	private GradleDefaultClosureKeyWords(String text) {
		this.text = text;
	}


	@Override
	public String getText() {
		return text;
	}
}
