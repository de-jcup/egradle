package de.jcup.egradle.core.text;

public class FormattedLineContext {
	
	private FormattedLineState state = FormattedLineState.NOT_IN_STRING;
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

	public boolean hasState(FormattedLineState state) {
		return this.state==state;
	}

	public void changeState(FormattedLineState state) {
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