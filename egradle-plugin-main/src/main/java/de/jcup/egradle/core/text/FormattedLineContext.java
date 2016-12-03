package de.jcup.egradle.core.text;

public class FormattedLineContext {
	
	private FormattedLineStringState state = FormattedLineStringState.NOT_IN_STRING;
	private char before = '0';
	private StringBuilder currentText = new StringBuilder();
	private StringBuilder textNotInString = new StringBuilder();
	private StringBuilder formattedLine = new StringBuilder();
	private char current;
	private boolean formattingEnabled=true;
	private boolean notInStringLiteralDetectionEnabled=false;

	public void visit(char c) {
		before = current;
		currentText.append(c);
		current = c;
	}

	public String getCurrentText() {
		return currentText.toString();
	}

	public void appendFormatted(String text) {
		if (!formattingEnabled){
			return;
		}
		formattedLine.append(text);
	}

	public String getFormattedText() {
		if (!formattingEnabled){
			throw new IllegalStateException("Formatting was disabled!");
		}
		return formattedLine.toString();
	}

	public void appendNotInString(String text) {
		if (! notInStringLiteralDetectionEnabled){
			return;
		}
		textNotInString.append(text);
	}

	public boolean hasStringState(FormattedLineStringState state) {
		return this.state==state;
	}
	
	public boolean isAlreadyInAnotherStringState(FormattedLineStringState expected){
		if (expected==state) {
			return false;
		}
		if (state==FormattedLineStringState.NOT_IN_STRING){
			return false;
		}
		return true;
	}

	public void changeState(FormattedLineStringState state) {
		 this.state=state;
	}

	public void resetCurrentText() {
		currentText=new StringBuilder();
	}

	public boolean wasBackslashBefore() {
		return before == '\\';
	}

	public boolean hasTextOutsideOfStringLiterals(String string) {
		if (! notInStringLiteralDetectionEnabled){
			throw new IllegalStateException("not in String literal detection is not enabled");
		}
		return textNotInString.indexOf(string)!=-1;
	}

	public void turnOffFormatting() {
		formattingEnabled=false;
	}
	public void turnOnNotInStringliteralDetection(){
		notInStringLiteralDetectionEnabled=true;
	}
}