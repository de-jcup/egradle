package de.jcup.egradle.core.validation;

enum ProblemType {
	A_PROBLEM_OCCURRED_WHILE_EVALUATING("A problem occurred evaluating"), 
	
	COULD_NOT_COMPILE_SCRIPT("Could not compile script"),
	
	COULD_NOT_COMPILE_SETTINGS("Could not compile settings file");

	String text;

	private ProblemType(String text) {
		this.text = text;
	}
}