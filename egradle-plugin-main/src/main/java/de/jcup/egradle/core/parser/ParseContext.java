package de.jcup.egradle.core.parser;

import java.util.ArrayList;
import java.util.List;

class ParseContext {
	private char[] lineChars;
	private int pos;
	private int offset;
	private int lineNumber;
	private StringBuilder currentText = new StringBuilder();

	private List<String> lines = new ArrayList<>();
	private TokenParserResult result = new TokenParserResult();
	private int tokencounter;

	/**
	 * Create a unique ID in current parse context
	 * 
	 * @return unique identifier
	 */
	public int createNewTokenId() {
		return tokencounter++;
	}

	void addLine(String line) {
		lines.add(line);
	}

	TokenParserResult getResult() {
		return result;
	}

	public int getOffset() {
		return offset;
	}

	public char[] getLineChars() {
		return lineChars;
	}

	void setLineChars(char[] lineChars) {
		this.lineChars = lineChars;
	}

	void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public boolean hasNextChar() {
		int nextElement = pos + 1;
		return nextElement < lineChars.length;
	}

	public char getNextChar() {
		int nextElement = pos + 1;
		return lineChars[nextElement];
	}

	void goNextChar() {
		offset++;
		pos++;
	}

	public int getPos() {
		return pos;
	}

	public void resetPos() {
		pos = 0;
	}

	public void incPos() {
		pos++;
	}

	public void incOffset() {
		offset++;
	}

	public char getLineCharAtPos() {
		return lineChars[pos];
	}

	public List<String> getLines() {
		return lines;
	}

	public void dispose() {
		lines = null;
		lineChars = null;
		result = null;
	}

	public String getCurrentTextString() {
		if (currentText == null) {
			return null;
		}
		return currentText.toString();
	}

	public void resetCurrentText() {
		currentText = new StringBuilder();
	}

	public void appendCurrentText(char c) {
		currentText.append(c);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ParseContext:");
		sb.append("\nline :" + getLines().get(getLineNumber()));
		sb.append("\n  pos:" + buildPointerString());
		sb.append("\ncText:" + getCurrentTextString());
		sb.append("\n");
		return sb.toString();
	}

	private String buildPointerString() {
		StringBuilder sb = new StringBuilder();
		int pointerPos = getPos() - 1;
		for (int i = 0; i < pointerPos; i++) {
			sb.append('-');
		}
		sb.append('^');
		String pointer = sb.toString();
		return pointer;
	}

	public void addProblem(String problem) {
		result.addProblem(problem, getOffset());
	}
}