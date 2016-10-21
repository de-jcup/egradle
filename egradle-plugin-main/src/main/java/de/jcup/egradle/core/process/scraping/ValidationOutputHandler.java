package de.jcup.egradle.core.process.scraping;

import de.jcup.egradle.core.api.FormatConverter;
import de.jcup.egradle.core.process.OutputHandler;

public class ValidationOutputHandler implements OutputHandler {
	private static final String LINE2 = "line:";
	private static final int LINE2_LENGTH = LINE2.length();
	private static final String SCRIPT = "Script '";
	private static final int SCRIPT_LENGTH = SCRIPT.length();
	private ValidationResult problem = new ValidationResult();
	private FormatConverter converter = new FormatConverter();

	public class ValidationResult {
		private boolean whereFound;
		private boolean whatWentWrongFound;
		private ProblemType problemType;
		private String problemMessage;
		private int problemLine;
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
		A_PROBLEM_OCCURRED_WHILE_EVALUATING("A problem occurred evaluating script");

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
	@Override
	public void output(String line) {
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
		if (problem.problemMessage != null) {
			return;
		}
		int scriptTextStart = line.indexOf(SCRIPT);
		if (scriptTextStart == -1) {
			return;
		}
		String remaining = line.substring(scriptTextStart+SCRIPT_LENGTH);
		int scriptTextEnd = remaining.indexOf("'");
		if (scriptTextEnd == -1) {
			return;
		}
		problem.problemScriptPath = remaining.substring(0, scriptTextEnd);
		int lineIndex = remaining.indexOf(LINE2);
		if (lineIndex == -1) {
			return;
		}
		String lineNr = remaining.substring(lineIndex + LINE2_LENGTH);
		problem.problemLine = converter.convertToInt(lineNr.trim());
	}
}
