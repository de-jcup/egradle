package de.jcup.egradle.eclipse.gradleeditor.document.keywords;

public enum GradleDefaultClosureKeyWords implements DocumentKeyWord {

	COMPILE("compile"),
	
	TEST_COMPILE("testCompile"),
	
	FROM("from"),
	
	INTO("into"),
	
	INCLUDE("include"),
	
	EXCLUDE("exclude"),
	
	DEPENDENCIES("dependencies"),
	
	REPOSITORIES("repositories"),

	ALL_PROJECTS("allprojects"),

	SUB_PROJECTS("subprojects"),
	
	ARTIFACTS("artifacts"),
	
	TEST("test"),
	
	CLEAN("clean"),
	
	BUILDSCRIPT("buildscript"),
	
	FILES("files"),
	
	FILETREE("fileTree"),
	
	CONFIGURATIONS("configurations")
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
