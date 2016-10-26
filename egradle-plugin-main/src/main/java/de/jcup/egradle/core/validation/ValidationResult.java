package de.jcup.egradle.core.validation;

public class ValidationResult {
	boolean whereFound;
	boolean whatWentWrongFound;
	ProblemType problemType;
	String problemMessage;
	int line=-1;
	String problemScriptPath;
	int column=-1;

	public boolean hasProblem(){
		boolean hasProblem = false;
		hasProblem=hasProblem || hasScriptEvaluationProblem();
		hasProblem=hasProblem || hasCompileProblem();
		return hasProblem;
		
	}
	
	boolean hasScriptEvaluationProblem() {
		return problemType == ProblemType.A_PROBLEM_OCCURRED_WHILE_EVALUATING;
	}

	public int getLine() {
		return line;
	}

	public String getScriptPath() {
		return problemScriptPath;
	}

	public String getErrorMessage() {
		return problemMessage;
	}

	boolean hasCompileProblem() {
		return problemType == ProblemType.COULD_NOT_COMPILE_SCRIPT || problemType== ProblemType.COULD_NOT_COMPILE_SETTINGS;
	}

	public int getColumn() {
		return column;
	}
}