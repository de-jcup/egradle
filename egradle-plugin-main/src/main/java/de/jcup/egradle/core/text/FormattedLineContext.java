package de.jcup.egradle.core.text;

public class FormattedLineContext {

	private FormattedLineState state = FormattedLineState.NORMAL;
	private char before = '0';
	private StringBuilder currentText = new StringBuilder();
	private StringBuilder textNotInString = new StringBuilder();
	private StringBuilder formattedLine = new StringBuilder();
	private char current;
	private boolean formattingEnabled = true;
	private boolean notInStringLiteralDetectionEnabled = false;

	public FormattedLineContext(FormattedLineContext contextBefore) {
		if (contextBefore==null){
			return;
		}
		state=contextBefore.state;
	}

	public void visit(char c) {
		before = current;
		currentText.append(c);
		current = c;
	}
	
	public char getBefore() {
		return before;
	}

	public String getCurrentText() {
		return currentText.toString();
	}

	public void appendFormatted(String text) {
		if (!formattingEnabled) {
			return;
		}
		formattedLine.append(text);
	}

	public String getFormattedText() {
		if (!formattingEnabled) {
			throw new IllegalStateException("Formatting was disabled!");
		}
		return formattedLine.toString();
	}

	public void appendNotInString(String text) {
		if (!notInStringLiteralDetectionEnabled) {
			return;
		}
		textNotInString.append(text);
	}

	public boolean hasState(FormattedLineState state) {
		return this.state == state;
	}

	public boolean isAlreadyInAnotherStringState(FormattedLineState expected) {
		if (expected == state) {
			return false;
		}
		if (state == FormattedLineState.NORMAL) {
			return false;
		}
		return true;
	}

	public void changeState(FormattedLineState state) {
		this.state = state;
	}

	public void resetCurrentText() {
		currentText = new StringBuilder();
	}

	public boolean wasBackslashBefore() {
		return before == '\\';
	}

	public boolean hasTextOutsideOfStringLiterals(String string) {
		if (!notInStringLiteralDetectionEnabled) {
			throw new IllegalStateException("not in String literal detection is not enabled");
		}
		return textNotInString.indexOf(string) != -1;
	}

	public void turnOffFormatting() {
		formattingEnabled = false;
	}

	public void turnOnNotInStringliteralDetection() {
		notInStringLiteralDetectionEnabled = true;
	}

	public boolean isComment() {
		return state==FormattedLineState.IN_SINGLE_LINE_COMMENT || state == FormattedLineState.IN_MULTI_LINE_COMMENT;
	}

}