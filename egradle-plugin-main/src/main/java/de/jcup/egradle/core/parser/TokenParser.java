package de.jcup.egradle.core.parser;

import static de.jcup.egradle.core.parser.TokenType.*;
import static de.jcup.egradle.core.parser.DebugUtil.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

public class TokenParser {

	private static boolean GLOBAL_DEBUGENABLED = Boolean.TRUE.equals(System.getProperty("egradle.global.debug"));

	/**
	 * When enabled for parser instance, created tokens contain content string
	 * information. Interesting for debugging, but not for normal usage. When
	 * global enabled {@link TokenParser#GLOBAL_DEBUGENABLED}, every token
	 * parser collects debug information.
	 */
	private boolean debugEnabled = GLOBAL_DEBUGENABLED;

	/**
	 * When trace enabled, the complete parsing mechanism does output to console
	 */
	private boolean traceEnabled;
	private ParseContext context;
	private TokenState currentState = null;

	private MultiTokenTypeAnalyzer tokenTypeAnalyzer = new MultiTokenTypeAnalyzer();
	TokenChainer tokenChainer = new TokenChainer();

	/**
	 * Parse given input stream and return result
	 * @param is input stream to use
	 * @return result, never <code>null</code>
	 * @throws IOException
	 */
	public TokenParserResult parse(InputStream is) throws IOException {

		if (is == null) {
			throw new IllegalArgumentException("input stream may not be null!");
		}

		context = new ParseContext();

		fillContextWithLineData(is);

		parseLines();

		TokenParserResult result = context.getResult();
		if (debugEnabled) {
			result.print();
		}
		context.dispose();
		/* chain tokens */
		tokenChainer.chain(result.getRoot());
		return result;
	}

	public void enableDebugMode() {
		debugEnabled = true;
	}

	public void enableTraceMode() {
		debugEnabled = true;
		traceEnabled = true;
	}

	protected void parseLines() {
		int lineNumber = 0;
		for (Iterator<String> it = context.getLines().iterator(); it.hasNext();) {
			String line = it.next();
			context.setLineNumber(lineNumber);
			if (debugEnabled) {
				debug("LINE:" + context.getLineNumber() + ":" + line);
			}
			visit(line);
			context.incPosAndOffset();
//			context.incOffset();
			; // new line...
			lineNumber++;
			if (it.hasNext()) {
				switchState(TokenState.NEW_LINE_START);
			}
		}
		switchState(TokenState.EOF_FOUND);
	}

	protected void visit(String line) {
		if (line == null) {
			return;
		}
		if (StringUtils.isEmpty(line)) {
			return;
		}
		context.setLineChars(line.toCharArray());
		context.setInSingleComment(false);
		context.setInGString(false);
		context.setInNormalString(false);

		if (context.isInitializationDone()) {
			switchState(TokenState.INITIALIZING);
			context.markInitializationDone();
		}

		for (context.resetPos(); context.getPos() < context.getLineChars().length; context.incPosAndOffset()) {
			char c = context.getLineCharAtPos();
			
			switch (c) {
			/* ------------ BRACES ---------------- */
			case '{':
				if (context.isInComment()) {
					/* ignore inside comments */
					appendPosCharacterForNextClosing();
					continue;
				}
				if (context.isInString()) {
					/* ignore inside strings */
					appendPosCharacterForNextClosing();
					continue;
				}
				switchState(TokenState.CURLY_BRACKET_START_FOUND);
				break;
			case '}':
				if (context.isInComment()) {
					/* ignore inside comments */
					appendPosCharacterForNextClosing();
					continue;
				}
				if (context.isInString()) {
					/* ignore inside strings */
					appendPosCharacterForNextClosing();
					continue;
				}
				switchState(TokenState.CURLY_BRACKET_END_FOUND);
				break;
			/* ------------ COMMENTS ---------------- */
			case '/':
				if (context.isInString()) {
					appendPosCharacterForNextClosing();
					continue;
				}
				if (context.hasNextChar()) {
					char nc = context.getNextChar();
					if (nc == '*') {
						// context.goNextChar();
						switchState(TokenState.MULTILINE_COMMENT_START_FOUND);
					} else if (nc == '/') {
						// context.goNextChar();
						switchState(TokenState.SINGLE_LINE_COMMENT_START_FOUND);
					}
				}
				break;
			case '*':
				if (context.isInString()) {
					break;
				}
				if (context.isInMultiLineComment()) {
					if (context.hasNextChar()) {
						char nc = context.getNextChar();
						if (nc == '/') {
							// context.goNextChar();
							switchState(TokenState.MULTILINE_COMMENT_END_FOUND);
						}
					}
				}
				break;
			/* ------------ STRINGS ---------------- */
			case '\'':
				if (context.isInComment()){
					appendPosCharacterForNextClosing();
					continue;
				}
				if (context.isInGString()) {
					appendPosCharacterForNextClosing();
					continue;
				}
				if (context.isInNormalString()) {
					switchState(TokenState.SINGLE_QUOTE_END);
				} else {
					switchState(TokenState.SINGLE_QUOTE_START);
				}
				break;
			case '\"':
				/*
				 * FIXME ATR, 6.11.2016: handle escape sequences... a \" is not
				 * a string start...
				 */
				if (context.isInComment()){
					appendPosCharacterForNextClosing();
					continue;
				}
				if (context.isInGString()) {
					switchState(TokenState.DOUBLE_QUOTE_END);
				} else {
					switchState(TokenState.DOUBLE_QUOTE_START);
				}
				break;
			default:
				if (context.isInComment()) {
					/* ignore inside comments */
					appendPosCharacterForNextClosing();
					continue;
				}
				if (context.isInString()) {
					/* ignore inside strings */
					appendPosCharacterForNextClosing();
					continue;
				}
				if (Character.isWhitespace(c)) {
					switchState(TokenState.WHITESPACE_FOUND);
				} else {
					switchState(TokenState.NORMAL_CHARACTER_READING);
				}
			}
		}
	}

	void switchState(TokenState newState) {
		if (traceEnabled) {
			trace("switch state from " + currentState + " to " + newState);
		}
		boolean appendNeeded = true;
		/*
		 * Currently many tokens are ignored - e.g. comments are not created, or
		 * strings etc.)
		 */
		if (context.isInMultiLineComment()){
			if (newState==TokenState.MULTILINE_COMMENT_END_FOUND){
				appendNeeded = handleNewState(newState, appendNeeded);
			}else{
				if (traceEnabled){
					trace("IGNORE handling of new state '"+newState+"' because inside multi line comment");
				}
			}
		}else{
			appendNeeded = handleNewState(newState, appendNeeded);
		}
		this.currentState = newState;
		if (appendNeeded) {
			appendPosCharacterForNextClosing();
		}
	}

	private boolean handleNewState(TokenState newState, boolean appendNeeded) {
		switch (newState) {
		case INITIALIZING:
			closeActiveTokenAndCreateNewOne(null);
			break;
		case EOF_FOUND:
			closeActiveToken();
			break;
		case WHITESPACE_FOUND:
			closeActiveToken();
			break;
		case NEW_LINE_START:
			closeActiveToken();
			break;
		case DOUBLE_QUOTE_START:
			closeActiveTokenAndCreateNewOne(GSTRING);
			appendNeeded=false;
			context.setInGString(true);
			break;
		case DOUBLE_QUOTE_END:
			closeActiveToken();
			appendNeeded=false;
			context.setInGString(false);
			// we removed leading and trailing ' but length needs this in UI, so add 2
			context.getLastToken().setLength(context.getLastToken().getLength()+2);
			break;
		case SINGLE_QUOTE_START:
			closeActiveTokenAndCreateNewOne(STRING);
			appendNeeded=false;
			context.setInNormalString(true);
			break;
		case SINGLE_QUOTE_END:
			closeActiveToken();
			appendNeeded=false;
			context.setInNormalString(false);
			// we removed leading and trailing ' but length needs this in UI, so add 2
			context.getLastToken().setLength(context.getLastToken().getLength()+2);
			break;
		case SINGLE_LINE_COMMENT_START_FOUND:
			closeActiveTokenAndCreateNewOne(COMMENT__SINGLE_LINE);
			context.appendAllRemainingTextOfLineAndIncPos();
			appendNeeded = false;
			closeActiveToken();
			/* decrease pos and offset - I have no idea why the offset is odd here
			 * the only possibility would be the double inc at the // detection .
			 * But its necessary here:
			 */
			context.decPosAndOffset();
			break;
		case MULTILINE_COMMENT_START_FOUND:
			closeActiveTokenAndCreateNewOne(COMMENT__MULTI_LINE);
			context.setInMultiLineComment(true);
			appendPosCharacterForNextClosing();
			context.incPosAndOffset();
			appendPosCharacterForNextClosing();
			appendNeeded=false;
			
			break;
		case MULTILINE_COMMENT_END_FOUND:
			/* add close characters of multi line comment: */
			appendPosCharacterForNextClosing();
			context.incPosAndOffset();// */
			appendPosCharacterForNextClosing();
			appendNeeded=false;
			closeActiveToken();
			context.setInMultiLineComment(false);
			Token lastToken = context.getLastToken();
			/* special handling for length to provide multi line length...*/
			lastToken.setLength(context.getOffset()-lastToken.getOffset()+1);
			
			break;
		case CURLY_BRACKET_START_FOUND:
			if (currentState == TokenState.MULTILINE_COMMENT_START_FOUND) {
				/* ignore it'S inside a comment */
				return appendNeeded;
			}
			Token formerActiveParent = context.getActiveParent();
			closeActiveTokenAndCreateNewOne(TokenType.BRACE_OPENING);

			if (traceEnabled) {
				trace("add " + context.getActiveToken().toIdString() + " to parent:" + formerActiveParent.toIdString());
			}
			formerActiveParent.addChild(context.getActiveToken());
			appendPosCharacterForNextClosing();
			appendNeeded = false;
			closeActiveTokenButDoNotSetParent(); // next character will create
													// new token so {a is
			// correct done
			changeActiveParent(context.getLastToken());

			break;
		case CURLY_BRACKET_END_FOUND:
			if (currentState == TokenState.MULTILINE_COMMENT_START_FOUND) {
				/* ignore it'S inside a comment */
				return appendNeeded;
			}
			if (context.getLastToken() == null) {
				handleProblem("Curly bracket closing, but no last token available - seems a syntax failure!");
				return appendNeeded;
			}
			closeActiveToken();
			changeActiveParent(context.getActiveParent().getParent());
			closeActiveTokenAndCreateNewOne(BRACE_CLOSING);
			appendPosCharacterForNextClosing();
			appendNeeded = false;
			closeActiveToken();
			break;
		case NORMAL_CHARACTER_READING:
			ensureActiveToken();
			break;
		default:
			if (traceEnabled) {
				trace("unhandled new state:" + newState);
			}

		}
		return appendNeeded;
	}

	private void appendPosCharacterForNextClosing() {
		if (context.canFetchNextLineCharAtPos()) {
			char lineCharAtPos = context.getLineCharAtPos();
			context.appendNextClosingText(lineCharAtPos);
		}
		
	}

	private void changeActiveParent(Token token) {
		if (traceEnabled) {
			trace("CHANGE PARENT from " + context.getActiveParent() + " to last token:" + createTokenString(token));
		}
		context.setActiveParent(token);
	}

	private String createTokenString(Token token) {
		return context.createTokenString(token);
	}

	private void ensureActiveToken() {
		if (context.getActiveToken() == null) {
			closeActiveTokenAndCreateNewOne(null);
		}
	}

	protected boolean closeActiveToken() {
		return _closeActiveToken(true);
	}

	protected boolean closeActiveTokenButDoNotSetParent() {
		return _closeActiveToken(false);
	}

	private boolean _closeActiveToken(boolean autoSetParent) {
		Token closingToken = context.getActiveToken();
		if (closingToken == null) {
			return true;
		}

		if (traceEnabled) {
			trace("CLOSE " + createTokenString(closingToken) + " requested");
		}

		if (traceEnabled) {
			// traceHeadline("BEFORE-CLOSE-TOKEN");
			trace(context.toString());
		}
		String name = getCurrentTextStringTrimmed();
		if (StringUtils.isEmpty(name)) {
			/*
			 * closing not necessary, token did contain only whitespaces, so
			 * reuse it to avoid such tokens
			 */
			if (traceEnabled) {
				trace("CLOSING canceled, because whitespace only");
			}
			return false;
		}
		
		closingToken.setName(name);

		TokenType type = closingToken.getType();
		/*
		 * some stages are able to set token type already - so only if not use
		 * analyzer
		 */
		if (type == TokenType.UNKNOWN) {
			TokenType tokenType = tokenTypeAnalyzer.analyze(closingToken);
			closingToken.setType(tokenType);
		}

		if (traceEnabled) {
			trace("CLOSING " + createTokenString(closingToken));
		}

		if (autoSetParent) {
			context.getActiveParent().addChild(closingToken);
			if (traceEnabled) {
				trace("ADDED " + createTokenString(closingToken) + " to parent:"
						+ createTokenString(closingToken.getParent()));
			}
		}
		context.setLastToken(closingToken);
		if (traceEnabled) {
			trace("LAST token now:" + createTokenString(context.getLastToken()));
		}
		resetActiveToken();

		context.resetNextClosingTokenText();
		if (traceEnabled) {
			trace("CLOSE DONE");
		}
		return true;
	}

	private void resetActiveToken() {
		context.setActiveToken(null);
		if (traceEnabled) {
			trace("RESET active token");
		}
	}

	private void fillContextWithLineData(InputStream is) throws IOException {
		/* TODO ATR, 01.11.2016: define charset */
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		int i = 0;
		while ((line = br.readLine()) != null) {
			if (traceEnabled) {
				trace((i++) + ":" + line);
			}
			context.addLine(line);
		}
	}

	private String getCurrentTextString() {
		return context.getCurrentTextString();
	}

	private String getCurrentTextStringTrimmed() {
		return getCurrentTextString().trim();
	}

	private void handleProblem(String message) {
		if (traceEnabled) {
			trace("adding problem:" + message);
		}
		context.addProblem(message);

	}

	/**
	 * Creates a new token (if necessary) and set it too active one. If a former
	 * active token is detected, it will be closed - so name , parent etc. will
	 * be setup
	 * 
	 * @param type
	 * @return true when new active token was necessary
	 */
	private boolean closeActiveTokenAndCreateNewOne(TokenType type) {
		boolean newTokenNecessary = closeActiveToken();
		if (traceEnabled) {
			trace(context.toString());
		}
		Token token = null;
		if (newTokenNecessary) {
			token = new Token(context.createNewTokenId());
			if (traceEnabled) {
				trace("CREATED " + createTokenString(context.getActiveToken()));
			}
			context.setActiveToken(token);
		} else {
			token = context.getActiveToken();
			if (traceEnabled) {
				trace("REUSE " + createTokenString(context.getActiveToken()));
			}
		}
		token.setType(type);
		token.setOffset(context.getOffset());
		token.setLineNumber(context.getLineNumber());

		return newTokenNecessary;
	}

}
