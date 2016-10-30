/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
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