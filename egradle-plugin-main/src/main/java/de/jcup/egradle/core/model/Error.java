package de.jcup.egradle.core.model;

public class Error {

	private String message;
	private int lineNumber = 1;
	private int charStart = -1;
	private int charEnd = -1;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public void setCharEnd(int charEnd) {
		this.charEnd = charEnd;
	}

	public void setCharStart(int charStart) {
		this.charStart = charStart;
	}

	public int getCharStart() {
		return charStart;
	}

	public int getCharEnd() {
		return charEnd;
	}
}
