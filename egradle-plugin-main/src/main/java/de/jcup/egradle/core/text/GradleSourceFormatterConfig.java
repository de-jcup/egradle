package de.jcup.egradle.core.text;

public class GradleSourceFormatterConfig {
	private static final String LINE_SEPARATOR = "\n";
	private static final String DEFAULT_INDENT = "   ";
	private String oneIndent;
	private String afterCurlyBracketOpened;
	private String beforeCurlyBracketOpened;
	private String afterCurlyBracketClosed;
	private String beforeCurlyBracketClosed;

	public void setAfterCurlyBracketOpened(String afterCurlyBracket) {
		this.afterCurlyBracketOpened = afterCurlyBracket;
	}

	public void setBeforeCurlyBracketOpened(String beforeCurlyBracket) {
		this.beforeCurlyBracketOpened = beforeCurlyBracket;
	}

	public void setAfterCurlyBracketClosed(String afterCurlyBracketClosed) {
		this.afterCurlyBracketClosed = afterCurlyBracketClosed;
	}

	public void setBeforeCurlyBracketClosed(String beforeCurlyBracketClosed) {
		this.beforeCurlyBracketClosed = beforeCurlyBracketClosed;
	}

	public String getAfterCurlyBracketOpened() {
		if (afterCurlyBracketOpened == null) {
			afterCurlyBracketOpened = LINE_SEPARATOR;
		}
		return afterCurlyBracketOpened;
	}

	public String getAfterCurlyBracketClosed() {
		if (afterCurlyBracketClosed == null) {
			afterCurlyBracketClosed = LINE_SEPARATOR;
		}
		return afterCurlyBracketClosed;
	}

	public String getBeforeCurlyBracketOpened() {
		if (beforeCurlyBracketOpened == null) {
			beforeCurlyBracketOpened = " ";
		}
		return beforeCurlyBracketOpened;
	}

	public String getBeforeCurlyBracketClosed() {
		if (beforeCurlyBracketClosed == null) {
			beforeCurlyBracketClosed = LINE_SEPARATOR;
		}
		return beforeCurlyBracketClosed;
	}

	/**
	 * Set one oneIndent
	 * 
	 * @param oneIndent
	 */
	public void setOneIndent(String oneIndent) {
		this.oneIndent = oneIndent;
	}

	public String getOneIndent() {
		if (oneIndent == null) {
			oneIndent = DEFAULT_INDENT;
		}
		return oneIndent;
	}
}
