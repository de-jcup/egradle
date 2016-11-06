package de.jcup.egradle.core.parser;

import java.util.ArrayList;
import java.util.List;

class ParseContext {
	private char[] lineChars;
	private int pos;
	private int offset;
	private int lineNumber;
	private StringBuilder nextClosingTokenText = new StringBuilder();

	private List<String> lines = new ArrayList<>();
	private TokenParserResult result;
	private int tokencounter;
	private boolean inSingleComment;
	private boolean inMultiLineComment;
	private boolean inString;
	private Token rootToken;
	private Token activeParent;
	private Token activeToken;
	private Token lastToken;
	private boolean initializationDone;

	public Token getActiveParent() {
		return activeParent;
	}

	public void setActiveParent(Token activeParent) {
		this.activeParent = activeParent;
	}

	public void setActiveToken(Token activeToken) {
		this.activeToken = activeToken;
	}

	public Token getActiveToken() {
		return activeToken;
	}

	public void setLastToken(Token lastToken) {
		this.lastToken = lastToken;
	}

	public Token getLastToken() {
		return lastToken;
	}

	public ParseContext() {
		this.rootToken = new Token(createNewTokenId());
		rootToken.setType(TokenType.ROOT);

		activeParent = rootToken;

		result = new TokenParserResult(rootToken);
	}

	public void setInSingleComment(boolean inSingleComment) {
		this.inSingleComment = inSingleComment;
	}

	public boolean isInComment(){
		return isInSingleComment() || isInMultiLineComment();
	}
	
	public boolean isInSingleComment() {
		return inSingleComment;
	}

	public void setInMultiLineComment(boolean inMultiLineComment) {
		this.inMultiLineComment = inMultiLineComment;
	}

	public boolean isInMultiLineComment() {
		return inMultiLineComment;
	}

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

	public boolean canFetchNextLineCharAtPos() {
		if (lineChars==null){
			return false;
		}
		if (lineChars.length <= pos) {
			return false;
		}
		return true;
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
		rootToken = null;
	}

	public String getCurrentTextString() {
		if (nextClosingTokenText == null) {
			return null;
		}
		return nextClosingTokenText.toString();
	}

	public void resetNextClosingTokenText() {
		nextClosingTokenText = new StringBuilder();
	}

	public void appendNextClosingText(char c) {
		nextClosingTokenText.append(c);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ParseContext:");
		sb.append("\nline :" + getLines().get(getLineNumber()));
		sb.append("\n  pos:" + buildPointerString()+"("+pos+")");
		sb.append("\ncurrentText:" + getCurrentTextString());
		sb.append("\nactiveParent:" + createTokenString(activeParent));
		sb.append("\nlastToken:" + createTokenString(lastToken));
		sb.append("\nactiveToken:" + createTokenString(activeToken));
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

	public String createTokenString(Token token) {
		if (token == null) {
			return "No Token";
		}
		return token.toIdString();
	}

	public void addProblem(String problem) {
		result.addProblem(problem, getOffset());
	}

	public void setInString(boolean inString) {
		this.inString = inString;
	}

	public boolean isInString() {
		return inString;
	}

	public Token getRootToken() {
		return rootToken;
	}

	public void markInitializationDone() {
		initializationDone = true;
	}

	public boolean isInitializationDone() {
		return initializationDone;
	}

	public void appendAllRemainingTextOfLineAndIncPos() {
		for (int i=getPos();i<lineChars.length;i++){
			appendNextClosingText(getLineCharAtPos());
			incPos();
		}
		
	}

}