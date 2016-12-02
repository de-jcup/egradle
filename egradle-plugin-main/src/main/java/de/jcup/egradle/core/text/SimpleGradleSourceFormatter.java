package de.jcup.egradle.core.text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A very simply gradle source formatter
 * 
 * @author Albert Tregnaghi
 *
 */
public class SimpleGradleSourceFormatter {

	private static final String ALL_LINE_SEP_GROUP = "\\s*[\\r\\n|\\n\\r]?\\s*";

	private static final Pattern REMOVE_TOO_MANY_SPACES = Pattern.compile("[ ]{2,}");

	private static final Pattern REMOVE_LEADING_LINE_BREAKS_CURLYBRACKET_OPEN = Pattern
			.compile(ALL_LINE_SEP_GROUP + "\\{", Pattern.DOTALL);
	private static final Pattern REMOVE_LEADING_LINE_BREAKS_CURLYBRACKET_CLOSE = Pattern
			.compile(ALL_LINE_SEP_GROUP + "\\}", Pattern.DOTALL);

	private static final Pattern REMOVE_TRAILING_LINE_BREAKS_CURLYBRACKET_OPEN = Pattern
			.compile("\\{" + ALL_LINE_SEP_GROUP, Pattern.DOTALL);
	private static final Pattern REMOVE_TRAILING_LINE_BREAKS_CURLYBRACKET_CLOSE = Pattern
			.compile("\\}" + ALL_LINE_SEP_GROUP, Pattern.DOTALL);

	private static final Pattern ADD_LEADING_SPACE_TO_CURLYBRACKET_OPEN = Pattern.compile("\\{");
	private static final Pattern ADD_TRAILING_LINE_BREAK_TO_CURLYBRACKET_CLOSE = Pattern.compile("\\}");

	private GradleSourceFormatterConfig config;

	public void setConfig(GradleSourceFormatterConfig config) {
		this.config = config;
	}

	/*
	 * FIXME ATR, 30.11.2016: normal strings etc. with having { inside will be
	 * wrong formatted
	 */
	public GradleSourceFormatterConfig getConfig() {
		if (config == null) {
			config = new GradleSourceFormatterConfig();
		}
		return config;
	}

	public String format(String text, String charset) throws SourceFormatException {
		if (text == null) {
			return null;
		}

		/* remove all line breaks */
		List<String> originLines = transformToLines(text, charset);

		/* in each line add wanted line breaks */
		StringBuilder withWantedLineBreaks = new StringBuilder();
		for (String line : originLines) {
			withWantedLineBreaks.append(handleLine(line));
		}
		List<String> lines = transformToLines(withWantedLineBreaks.toString(), charset);

		/* remove old indent */
		List<String> indentLines = buildLinesWithNoWhitespacesBeforeText(lines);
		/* make indent */
		indentLines = buildLinesWithIndent(indentLines);
		StringBuilder sb = new StringBuilder();
		for (String string : indentLines) {
			sb.append(string);
		}
		return sb.toString();
	}

	private List<String> buildLinesWithIndent(List<String> lines) {
		List<String> indentLines = new ArrayList<>();
		int indent = 0;

		for (String line : lines) {
			ParseState state = ParseState.NOT_IN_STRING;

			char before = '0';
			StringBuilder lineWithoutGstrings = new StringBuilder();
			for (char c : line.toCharArray()) {
				before = c;
				if (c == '\"' && before != '\\') {
					if (state == ParseState.IN_GSTRING) {
						state = ParseState.NOT_IN_STRING;
					} else {
						/* WAS NOT IN GSTRING */
						state = ParseState.IN_GSTRING;
					}
				}
				before = c;
				if (state == ParseState.NOT_IN_STRING) {
					lineWithoutGstrings.append(c);
				}
			}
			if (lineWithoutGstrings.indexOf("{") != -1) {
				indentLines.add(indentStr(indent) + line);
				indent++;
			} else if (lineWithoutGstrings.indexOf("}") != -1) {
				indent--;
				indentLines.add(indentStr(indent) + line);
			} else {
				indentLines.add(indentStr(indent) + line);
			}
		}
		return indentLines;
	}

	private List<String> buildLinesWithNoWhitespacesBeforeText(List<String> lines) {
		List<String> indentLines = new ArrayList<>();
		for (String line : lines) {
			StringBuilder leftTrimmed = new StringBuilder();
			boolean isText = false;
			for (char c : line.toCharArray()) {
				if (c == ' ' || c == ' ' || c == '\t') {
					/* still no text */
				} else {
					isText = true;
				}
				if (isText) {
					leftTrimmed.append(c);
				}
			}
			indentLines.add(leftTrimmed.toString());
		}
		return indentLines;
	}

	private String removeLineBreaksFromCurlyBraces(String linePart) {
		linePart = REMOVE_LEADING_LINE_BREAKS_CURLYBRACKET_OPEN.matcher(linePart).replaceAll("{");
		linePart = REMOVE_LEADING_LINE_BREAKS_CURLYBRACKET_CLOSE.matcher(linePart).replaceAll("}");
		linePart = REMOVE_TRAILING_LINE_BREAKS_CURLYBRACKET_OPEN.matcher(linePart).replaceAll("{");
		linePart = REMOVE_TRAILING_LINE_BREAKS_CURLYBRACKET_CLOSE.matcher(linePart).replaceAll("}");
		return linePart;
	}

	private enum ParseState {
		IN_GSTRING,

		IN_NORMAL_STRING,

		IN_SLASHY_STRING,

		NOT_IN_STRING,

	}

	private class FormatterContext {
		private ParseState state = ParseState.NOT_IN_STRING;
		private char before = '0';
		private StringBuilder sb = new StringBuilder();
	}

	private String handleLine(String line) {
		StringBuilder result = new StringBuilder();

		FormatterContext context = new FormatterContext();

		char[] charArray = line.toCharArray();
		for (char c : charArray) {
			context.sb.append(c);
			handleGString(result, context, c);
			handleString(result, context, c);
			context.before = c;
		}
		if (context.state == ParseState.NOT_IN_STRING) {
			result.append(removeToMuchSpacesAndAddWantedLineBreaksR(context.sb.toString()));
		} else {
			result.append(context.sb.toString());
		}

		return result.toString();
	}

	private void handleGString(StringBuilder result, FormatterContext context, char c) {
		if (context.state == ParseState.IN_NORMAL_STRING) {
			return;
		}
		if (c != '\"') {
			return;
		}
		if (context.before == '\\') {
			return;
		}
		if (context.state == ParseState.IN_GSTRING) {
			result.append(context.sb.toString());
			context.state = ParseState.NOT_IN_STRING;
		} else {
			/* WAS NOT IN GSTRING */
			result.append(removeToMuchSpacesAndAddWantedLineBreaksR(context.sb.toString()));
			context.state = ParseState.IN_GSTRING;
		}
		context.sb = new StringBuilder();
	}

	private void handleString(StringBuilder result, FormatterContext context, char c) {
		if (context.state == ParseState.IN_GSTRING) {
			return;
		}
		if (c != '\'') {
			return;
		}
		if (context.before == '\\') {
			return;
		}
		if (context.state == ParseState.IN_NORMAL_STRING) {
			result.append(context.sb.toString());
			context.state = ParseState.NOT_IN_STRING;
		} else {
			/* WAS NOT IN GSTRING */
			result.append(removeToMuchSpacesAndAddWantedLineBreaksR(context.sb.toString()));
			context.state = ParseState.IN_NORMAL_STRING;
		}
		context.sb = new StringBuilder();
	}

	private String removeToMuchSpacesAndAddWantedLineBreaksR(String linePart) {
		linePart = REMOVE_TOO_MANY_SPACES.matcher(linePart).replaceAll(" ");
		linePart = removeLineBreaksFromCurlyBraces(linePart);
		if (linePart.trim().length() == 0) {
			return "";
		}

		/* add wanted line breaks */
		String openingBracket = getConfig().getBeforeCurlyBracketOpened() + "{"
				+ getConfig().getAfterCurlyBracketOpened();
		linePart = ADD_LEADING_SPACE_TO_CURLYBRACKET_OPEN.matcher(linePart).replaceAll(openingBracket);
		String closingBracket = getConfig().getBeforeCurlyBracketClosed() + "}"
				+ getConfig().getAfterCurlyBracketClosed();
		linePart = ADD_TRAILING_LINE_BREAK_TO_CURLYBRACKET_CLOSE.matcher(linePart).replaceAll(closingBracket);
		return linePart;
	}

	private String indentStr(int indent) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indent; i++) {
			sb.append(getConfig().getOneIndent());
		}
		return sb.toString();
	}

	private List<String> transformToLines(String code, String charset) throws SourceFormatException {
		StringLineSeparator sep = new StringLineSeparator();
		InputStream is = new ByteArrayInputStream(code.getBytes());
		List<String> linesWithDelimiters;
		try {
			linesWithDelimiters = sep.readLinesWithLineDelimiter(is, charset);
			return linesWithDelimiters;
		} catch (IOException e) {
			throw new SourceFormatException("Cannot transform to lines", e);
		}

	}

	public static void main(String[] args) throws SourceFormatException {
		// @formatter:off
		String code=
		"def jacocoRemoteAction(doDump, doReset, doAppend)\n\n\n {\n\n\n\n\n\n"+
		"	def  server1String = 'localhost:6300'\n"+
		"	def  server2String = 'localhost:6300'\n"+
		"}";
		// @formatter:on
		System.out.println(code);
		System.out.println("----------");
		System.out.println("result:");
		System.out.println("----------");
		String result = new SimpleGradleSourceFormatter().format(code, "UTF-8");
		System.out.println(result);
		System.out.println("done");
	}

}
