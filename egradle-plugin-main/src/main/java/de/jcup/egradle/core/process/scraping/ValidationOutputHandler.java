package de.jcup.egradle.core.process.scraping;

import de.jcup.egradle.core.api.FormatConverter;
import de.jcup.egradle.core.process.OutputHandler;

public class ValidationOutputHandler implements OutputHandler {
	private static final String LINE2 = "line:";
	private ValidationResult problem = new ValidationResult();
	private FormatConverter converter = new FormatConverter();

	public class ValidationResult {
		private boolean whereFound;
		private boolean whatWentWrongFound;
		private ProblemType problemType;
		private String problemMessage;
		private int problemLine=-1;
		private String problemScriptPath;

		public boolean hasScriptEvaluationProblem() {
			return problemType == ProblemType.A_PROBLEM_OCCURRED_WHILE_EVALUATING;
		}

		public int getLine() {
			return problemLine;
		}

		public String getScriptPath() {
			return problemScriptPath;
		}

		public String getErrorMessage() {
			return problemMessage;
		}
	}

	public ValidationResult getResult() {
		return problem;
	}

	private enum ProblemType {
		A_PROBLEM_OCCURRED_WHILE_EVALUATING("A problem occurred evaluating");

		private String text;

		private ProblemType(String text) {
			this.text = text;
		}
	}

	// * Where:
	// Script '..path.../build-jacoco.gradle' line: 23
	// * What went wrong:
	// A problem occurred evaluating script.
	// > Could not get unknown property 'x' for root project 'xyz' of type
	// org.gradle.api.Project.
	//
	// example 2
	//	Build file '/home/albert/dev/src/git/gradle-project-template/build.gradle' line: 1"
	@Override
	public void output(String line) {
		System.out.println("output:"+line);
		if (!problem.whereFound) {
			if (line.indexOf("* Where:") != -1) {
				problem.whereFound = true;
				return;
			}
			return;
		}
		/* where was found */
		handleScriptText(line);
		handleProblemMessage(line);
	}

	private void handleProblemMessage(String line) {
		if (problem.problemType != null) {
			if (problem.problemMessage==null){
				if (line.startsWith(">")) {
					problem.problemMessage = line.substring(1).trim();
				}
			}
			return;
		}
		if (!problem.whatWentWrongFound) {
			problem.whatWentWrongFound = line.indexOf("* What went wrong") != -1;
			return;
		}
		if (problem.problemType == null) {
			if (line.startsWith(ProblemType.A_PROBLEM_OCCURRED_WHILE_EVALUATING.text)) {
				problem.problemType = ProblemType.A_PROBLEM_OCCURRED_WHILE_EVALUATING;
			}
		}

		if (problem.problemMessage != null) {
			return;
		}
		
	}

	private void handleScriptText(String line) {
		if (problem.problemLine!=-1){
			return;
		}
		if (problem.problemMessage != null) {
			return;
		}
		/* try to fetch line data*/
		if (line.indexOf("line:")==-1){
			return;
		}
		int scriptTextStart = line.indexOf('\'');
		if (scriptTextStart == -1) {
			return;
		}
		String remaining = line.substring(scriptTextStart+1);
		int scriptTextEnd = remaining.indexOf("'");
		if (scriptTextEnd == -1) {
			return;
		}
		problem.problemScriptPath = remaining.substring(0, scriptTextEnd);
		int lineIndex = remaining.indexOf(LINE2);
		if (lineIndex == -1) {
			return;
		}
		String lineNr = remaining.substring(lineIndex+LINE2.length());
		problem.problemLine = converter.convertToInt(lineNr.trim());
	}
}
