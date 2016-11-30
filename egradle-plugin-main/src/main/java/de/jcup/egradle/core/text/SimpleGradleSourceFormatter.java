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

	private static final String ALL_LINE_SEP_GROUP = "[\\r\\n|\\n\\r]?\\s*";
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
	/* FIXME ATR, 30.11.2016: normal strings etc. with having { inside will be wrong formatted*/
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
		text = removeLineBreaksFromCurlyBraces(text);

		/* in each line add anted line breaks */
		List<String> originLines = transformToLines(text, charset);

		StringBuilder withWantedLineBreaks = new StringBuilder();
		for (String line : originLines) {
			withWantedLineBreaks.append(addWantedLineBreaks(line));
		}
		List<String> lines = transformToLines(withWantedLineBreaks.toString(), charset);

		/* remove old indent */
		List<String> indentLines = buildLinesWithNoWhitespacesBeforeText(lines);
		/* make indent */
		indentLines=buildLinesWithIndent(indentLines);
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
			GStringParseState state = GStringParseState.NOT_IN_GSTRING;

			char before = '0';
			StringBuilder lineWithoutGstrings = new StringBuilder();
			for (char c : line.toCharArray()) {
				before = c;
				if (c == '\"' && before != '\\') {
					if (state == GStringParseState.IN_GSTRING) {
						state = GStringParseState.NOT_IN_GSTRING;
					} else {
						/* WAS NOT IN GSTRING */
						state = GStringParseState.IN_GSTRING;
					}
				}
				before = c;
				if (state==GStringParseState.NOT_IN_GSTRING){
					lineWithoutGstrings.append(c);
				}
			}
			if (lineWithoutGstrings.indexOf("{")!=-1){
				indentLines.add(indentStr(indent)+line);
				indent++;
			}else if (lineWithoutGstrings.indexOf("}")!=-1){
				indent--;
				indentLines.add(indentStr(indent)+line);
			}else{
				indentLines.add(indentStr(indent)+line);
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

	private String removeLineBreaksFromCurlyBraces(String text) {
		text = REMOVE_LEADING_LINE_BREAKS_CURLYBRACKET_OPEN.matcher(text).replaceAll("{");
		text = REMOVE_LEADING_LINE_BREAKS_CURLYBRACKET_CLOSE.matcher(text).replaceAll("}");
		text = REMOVE_TRAILING_LINE_BREAKS_CURLYBRACKET_OPEN.matcher(text).replaceAll("{");
		text = REMOVE_TRAILING_LINE_BREAKS_CURLYBRACKET_CLOSE.matcher(text).replaceAll("}");
		return text;
	}

	private enum GStringParseState {
		IN_GSTRING, NOT_IN_GSTRING
	}

	private String addWantedLineBreaks(String line) {
		StringBuilder result = new StringBuilder();
		GStringParseState state = GStringParseState.NOT_IN_GSTRING;

		char before = '0';
		StringBuilder sb = new StringBuilder();
		char[] charArray = line.toCharArray();
		for (char c : charArray) {
			sb.append(c);
			if (c == '\"' && before != '\\') {
				if (state == GStringParseState.IN_GSTRING) {
					result.append(sb.toString());
					state = GStringParseState.NOT_IN_GSTRING;
				} else {
					/* WAS NOT IN GSTRING */
					result.append(addLineBreaks(sb.toString()));
					state = GStringParseState.IN_GSTRING;
				}
				sb = new StringBuilder();
			}
			before = c;
		}
		if (state == GStringParseState.NOT_IN_GSTRING) {
			result.append(addLineBreaks(sb.toString()));
		} else {
			result.append(sb.toString());
		}

		return result.toString();
	}

	private String addLineBreaks(String linePart) {
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
