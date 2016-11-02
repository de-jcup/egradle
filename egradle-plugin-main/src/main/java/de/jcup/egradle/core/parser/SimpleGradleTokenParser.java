package de.jcup.egradle.core.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;

import static de.jcup.egradle.core.parser.TraceUtil.*;

public class SimpleGradleTokenParser {

	private static boolean GLOBAL_DEBUGENABLED = Boolean.TRUE.equals(System.getProperty("egradle.global.debug"));
	
	private class ParseContext {
		private GradleAST ast;
		private int lineNumber;
		public int offset;
	}

	private ParseContext context;
	private State currentState = null;
	private StringBuilder currentText;

	private AbstractGradleToken element;

	/**
	 * When trace enabled, the complete parsing mechanism does output to console
	 */
	boolean traceEnabled;
	/**
	 * When enabled for parser instance, created tokens contain content string information. Interesting for debugging, but not for normal usage.
	 * When global enabled {@link SimpleGradleTokenParser#GLOBAL_DEBUGENABLED}, every token parser collects debug information.
	 */
	boolean debugEnabled=GLOBAL_DEBUGENABLED;

	private GradleAST createASTWithLinesFilled(InputStream is) throws IOException {
		GradleAST ast = new GradleAST();
		/* TODO ATR, 01.11.2016: define charset */
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ((line = br.readLine()) != null) {
			ast.lines.add(line);
		}
		if (traceEnabled) {
			traceShowRuler();

			traceLine("AST created:");

			traceShowRuler();
			for (int i = 0; i < ast.lines.size(); i++) {
				String data = ast.lines.get(i);
				traceLine(i + ":" + data);
			}
			traceLine("\n\n");

		}
		return ast;
	}

	private String getCurrentTextString() {
		if (currentText == null) {
			return null;
		}
		return currentText.toString();
	}

	private String getCurrentTextStringTrimmed() {
		return getCurrentTextString().trim();
	}

	public GradleAST parse(InputStream is) throws IOException {

		if (is == null) {
			throw new IllegalArgumentException("input stream may not be null!");
		}
		GradleAST ast = createASTWithLinesFilled(is);
		context = new ParseContext();
		context.ast = ast;

		visitLines();

		return ast;
	}

	private void resetCurrentText() {
		if (traceEnabled) {
			traceLine("\nreset current text");
		}
		currentText = new StringBuilder();
	}

	private void switchState(State newState) {
		if (traceEnabled) {
			traceLine("switch state from " + currentState + " to " + newState);
		}
		/*
		 * Currently many tokens are ignored - e.g. comments are not created, or strings etc.)
		 */
		
		switch (newState) {
		case INITIALIZING:
			resetCurrentText();
			break;
		case NEW_LINE_TEXT_PARSING:
			resetCurrentText();
			break;
		case DOUBLE_QUOTE_START:
			resetCurrentText();
			break;
		case SINGLE_QUOTE_START:
			resetCurrentText();
			break;
		case MULTILINE_COMMENT_START_FOUND:
			resetCurrentText();
			break;
		case MULTILINE_COMMENT_END_FOUND:
			resetCurrentText();
			break;
		case SINGLE_LINE_COMMENT_START_FOUND:
			resetCurrentText();
			break;
		case BRACKET_START_FOUND:
			if (currentState==State.MULTILINE_COMMENT_START_FOUND){
				/* ignore it'S inside a comment */
				return;
			}
			AbstractGradleToken parentElement= element;
			this.element = new Closure();
			element.parent=parentElement;
			if (parentElement==null){
				context.ast.add(element);
			}else{
				parentElement.elements.add(element);
			}
			String currentText = getCurrentTextString();
			element.name=getCurrentTextStringTrimmed();
			element.offset=context.offset-currentText.length()-1;
			element.lineNumber=context.lineNumber;
			if (traceEnabled) {
				traceLine("Created element:" + element.getName());
			}
			resetCurrentText();
			break;
		case BRACKET_END_FOUND:
			if (currentState==State.MULTILINE_COMMENT_START_FOUND){
				/* ignore it'S inside a comment */
				return;
			}
			if (element!=null){
				if (debugEnabled){
					/* in debug mode set current text string as content of element - so it is easier to debug */
					element.content=getCurrentTextString();
				}
				this.element=element.getParent(); // switch back to parent / or null
			}
			resetCurrentText();
			break;
		default:
		}

		this.currentState = newState;
	}

	private void visitLine(String line) {
		if (traceEnabled) {
			traceShowRuler();
			traceLine("visit line:" + context.lineNumber + ", line=" + line);
			traceShowRuler();
		}
		if (line == null) {
			return;
		}
		if (StringUtils.isEmpty(line)) {
			return;
		}
		boolean append=true;
		char[] chars = line.toCharArray();
		for (int pos=0;pos<chars.length;pos++) {
			char c = chars[pos];
			context.offset++;
			switch (c) {
			case '{':
				if (currentState==State.MULTILINE_COMMENT_START_FOUND){
					/* ignore inside comments*/
					continue;
				}
				if (currentState==State.SINGLE_QUOTE_START){
					continue;
				}
				if (currentState==State.DOUBLE_QUOTE_START){
					continue;
				}
				switchState(State.BRACKET_START_FOUND);
				append=false;
				break;
			case '}':
				if (currentState==State.MULTILINE_COMMENT_START_FOUND){
					/* ignore inside comments*/
					continue;
				}
				if (currentState==State.SINGLE_QUOTE_START){
					continue;
				}
				if (currentState==State.DOUBLE_QUOTE_START){
					continue;
				}
				switchState(State.BRACKET_END_FOUND);
				append=false;
				break;
			case '/':
				if (currentState==State.SINGLE_QUOTE_START){
					continue;
				}
				if (currentState==State.DOUBLE_QUOTE_START){
					continue;
				}
				if (hasNextChar(pos, chars)){
					char nc = getNextChar(pos, chars);
					if (nc=='*'){
						pos = goNextChar(pos);
						switchState(State.MULTILINE_COMMENT_START_FOUND);
					}else if (nc=='/'){
						pos = goNextChar(pos);
						switchState(State.SINGLE_LINE_COMMENT_START_FOUND);
					}
				}
				append=false;
				break;
			case '*':
				if (currentState==State.SINGLE_QUOTE_START){
					continue;
				}
				if (currentState==State.DOUBLE_QUOTE_START){
					continue;
				}
				if (currentState==State.MULTILINE_COMMENT_START_FOUND){
					if (hasNextChar(pos, chars)){
						char nc = getNextChar(pos, chars);
						if (nc=='/'){
							pos = goNextChar(pos);
							switchState(State.MULTILINE_COMMENT_END_FOUND);
						}
					}
				}
				append=false;
				break;
			case '\'':
				if (currentState==State.SINGLE_QUOTE_START){
					switchState(State.SINGLE_QUOTE_END);
				}else{
					switchState(State.SINGLE_QUOTE_START);
				}
				append=true;
				break;
			case '\"':
				if (currentState==State.DOUBLE_QUOTE_START){
					switchState(State.DOUBLE_QUOTE_END);
				}else{
					switchState(State.DOUBLE_QUOTE_START);
				}
				append=true;
				break;
			default:
			}
			if (append){
				if (traceEnabled) {
					trace("" + c);
				}
				currentText.append(c);
			}
			
		}
	}

	private int goNextChar(int pos) {
		context.offset++;
		pos++;
		return pos;
	}

	private char getNextChar(int pos, char[] chars) {
		int nextElement= pos+1;
		return chars[nextElement];
	}

	private boolean hasNextChar(int pos, char[] chars) {
		int nextElement= pos+1;
		return nextElement<chars.length;
	}

	private void visitLines() {
		int lineNumber = 0;
		switchState(State.INITIALIZING);
		for (String line : context.ast.lines) {
			switchState(State.NEW_LINE_TEXT_PARSING);
			context.lineNumber = lineNumber;
			visitLine(line);
			context.offset++; // new line...
			lineNumber++;
		}
	}
}
