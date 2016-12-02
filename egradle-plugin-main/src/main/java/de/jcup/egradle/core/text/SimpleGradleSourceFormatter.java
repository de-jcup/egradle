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

		String textWithWantedLineBreaks =  transformToTextWithWantedLineBreaks(text);

		List<String> linesArray = null;
		linesArray= transformToLines(textWithWantedLineBreaks, charset);
		/* remove old indent */
		linesArray = buildLinesWithNoWhitespacesBeforeText(linesArray);
		/* make indent */
		linesArray = buildLinesWithIndentButIgnoreStrings(linesArray);
		StringBuilder sb = new StringBuilder();
		for (String string : linesArray) {
			sb.append(string);
		}
		return sb.toString();
	}

	protected List<String> buildLinesWithNoWhitespacesBeforeText(List<String> lines) {
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

	protected List<String> buildLinesWithIndentButIgnoreStrings(List<String> lines) {
		List<String> indentLines = new ArrayList<>();
		int indent = 0;

		for (String line : lines) {
			FormattedLineContext context = new FormattedLineContext();
			context.turnOffFormatting();
			context.turnOnNotInStringliteralDetection();

			visitLineWithContext(line, context);
			if (context.hasTextOutsideOfStringLiterals("{")) {
				indentLines.add(indentStr(indent) + line);
				indent++;
			} else if (context.hasTextOutsideOfStringLiterals("}"))  {
				indent--;
				indentLines.add(indentStr(indent) + line);
			} else {
				indentLines.add(indentStr(indent) + line);
			}
		}
		return indentLines;
	}

	private void visitLineWithContext(String line, FormattedLineContext context) {
		char[] charArray = line.toCharArray();
		for (char c : charArray) {
			context.visit(c);
			boolean handled = false;
			handled = handled || handleGString(context, c);
			handled = handled || handleString(context, c);
		}
		String currentText = context.getCurrentText();
		if (context.hasState( FormattedLineState.NOT_IN_STRING)) {
			context.appendFormatted(removeToMuchSpacesAndAddWantedLineBreaksR(currentText));
			context.appendNotInString(currentText);
		} else {
			context.appendFormatted(currentText);
		}
	}
	
	private String removeLineBreaksFromCurlyBraces(String linePart) {
		linePart = REMOVE_LEADING_LINE_BREAKS_CURLYBRACKET_OPEN.matcher(linePart).replaceAll("{");
		linePart = REMOVE_LEADING_LINE_BREAKS_CURLYBRACKET_CLOSE.matcher(linePart).replaceAll("}");
		linePart = REMOVE_TRAILING_LINE_BREAKS_CURLYBRACKET_OPEN.matcher(linePart).replaceAll("{");
		linePart = REMOVE_TRAILING_LINE_BREAKS_CURLYBRACKET_CLOSE.matcher(linePart).replaceAll("}");
		return linePart;
	}

	protected String transformToTextWithWantedLineBreaks(String line) {
		FormattedLineContext context = new FormattedLineContext();

		visitLineWithContext(line, context);

		String newLine = context.getFormattedText();
		
		return newLine;
	}

	boolean handleGString(FormattedLineContext context, char c) {
		if (context.hasState( FormattedLineState.IN_NORMAL_STRING)) {
			return false;
		}
		if (c != '\"') {
			return false;
		}
		if (context.wasBackslashBefore()) {
			return false;
		}
		if (context.hasState( FormattedLineState.IN_GSTRING)) {
			context.appendFormatted(context.getCurrentText());
			context.changeState(FormattedLineState.NOT_IN_STRING);
		} else {
			/* WAS NOT IN GSTRING */
			context.appendFormatted(removeToMuchSpacesAndAddWantedLineBreaksR(context.getCurrentText()));
			context.changeState(FormattedLineState.IN_GSTRING);
		}
		context.resetCurrentText();
		return true;
	}

	boolean handleString(FormattedLineContext context, char c) {
		if (context.hasState( FormattedLineState.IN_GSTRING)) {
			return false;
		}
		if (c != '\'') {
			return false;
		}
		if (context.wasBackslashBefore()) {
			return false;
		}
		if (context.hasState( FormattedLineState.IN_NORMAL_STRING)) {
			context.appendFormatted(context.getCurrentText());
			context.changeState( FormattedLineState.NOT_IN_STRING);
		} else {
			/* WAS NOT IN GSTRING */
			context.appendFormatted(removeToMuchSpacesAndAddWantedLineBreaksR(context.getCurrentText()));
			context.changeState( FormattedLineState.IN_NORMAL_STRING);
		}
		context.resetCurrentText();
	
		return true;
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
