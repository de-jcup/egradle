package de.jcup.egradle.codeassist;

public enum BracketInsertion {
	/**
	 * Will result in multined curly braces indented as before
	 */
	CURLY_BRACES('{', '}', true), 
	
	/**
	 * Will result in simple "[  ]" and cursor will be after first space
	 */
	EDGED_BRACES('[', ']', false),
	
	;
	
	private char start;
	private char end;
	private boolean multiLine;

	private BracketInsertion(char start, char end, boolean multiLine) {
		this.start = start;
		this.end = end;
		this.multiLine = multiLine;
	}

	public boolean isMultiLine() {
		return multiLine;
	}

	public char getStart() {
		return start;
	}

	public char getEnd() {
		return end;
	}

	public static BracketInsertion valueOfStartChar(char c) {
		for (BracketInsertion data : BracketInsertion.values()) {
			if (data.start==c){
				return data;
			}
		}
		return null;
	}

	public String createOneLineTemplate() {
		StringBuilder sb = new StringBuilder();
		sb.append(start);
		sb.append("  ");
		sb.append(end);
		return sb.toString();
	}

	public int createOneLineNewOffset(int offset) {
		return offset+1;
	}

	public String createMultiLineTemplate(String cursorVariable) {
		StringBuilder sb = new StringBuilder();
		sb.append(start);
		sb.append("\n    ");
		sb.append(cursorVariable);
		sb.append("\n");
		sb.append(end);
		return  sb.toString();
	}



}