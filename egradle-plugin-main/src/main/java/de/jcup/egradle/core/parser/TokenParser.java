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

	private Token activeToken;
	private Token lastToken;

	private MultiTokenTypeAnalyzer tokenTypeAnalyzer = new MultiTokenTypeAnalyzer();

	private Token activeParent;

	public TokenParserResult parse(InputStream is) throws IOException {
		return parse(is, null);
	}

	public TokenParserResult parse(InputStream is, TokenFilter filter) throws IOException {

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
		switchState(TokenState.INITIALIZING);
		for (Iterator<String> it = context.getLines().iterator(); it.hasNext();) {
			String line = it.next();
			context.setLineNumber(lineNumber);
			if (debugEnabled) {
				debug("LINE:" + context.getLineNumber() + ":" + line);
			}
			visit(line);
			context.incOffset();
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
		for (context.resetPos(); context.getPos() < context.getLineChars().length; context.incPos()) {
			char c = context.getLineCharAtPos();
			context.incOffset();
			switch (c) {
			case '{':
				if (currentState == TokenState.MULTILINE_COMMENT_START_FOUND) {
					/* ignore inside comments */
					continue;
				}
				if (currentState == TokenState.SINGLE_QUOTE_START) {
					continue;
				}
				if (currentState == TokenState.DOUBLE_QUOTE_START) {
					continue;
				}
				switchState(TokenState.CURLY_BRACKET_START_FOUND);
				break;
			case '}':
				if (currentState == TokenState.MULTILINE_COMMENT_START_FOUND) {
					/* ignore inside comments */
					continue;
				}
				if (currentState == TokenState.SINGLE_QUOTE_START) {
					continue;
				}
				if (currentState == TokenState.DOUBLE_QUOTE_START) {
					continue;
				}
				switchState(TokenState.CURLY_BRACKET_END_FOUND);
				closeActiveToken();
				break;
			case '/':
				if (currentState == TokenState.SINGLE_QUOTE_START) {
					continue;
				}
				if (currentState == TokenState.DOUBLE_QUOTE_START) {
					continue;
				}
				if (context.hasNextChar()) {
					char nc = context.getNextChar();
					if (nc == '*') {
						context.goNextChar();
						switchState(TokenState.MULTILINE_COMMENT_START_FOUND);
					} else if (nc == '/') {
						context.goNextChar();
						switchState(TokenState.SINGLE_LINE_COMMENT_START_FOUND);
					}
				}
				break;
			case '*':
				if (currentState == TokenState.SINGLE_QUOTE_START) {
					continue;
				}
				if (currentState == TokenState.DOUBLE_QUOTE_START) {
					continue;
				}
				if (currentState == TokenState.MULTILINE_COMMENT_START_FOUND) {
					if (context.hasNextChar()) {
						char nc = context.getNextChar();
						if (nc == '/') {
							context.goNextChar();
							switchState(TokenState.MULTILINE_COMMENT_END_FOUND);
						}
					}
				}
				break;
			case '\'':
				if (currentState == TokenState.SINGLE_QUOTE_START) {
					switchState(TokenState.SINGLE_QUOTE_END);
				} else {
					switchState(TokenState.SINGLE_QUOTE_START);
				}
				break;
			case '\"':
				if (currentState == TokenState.DOUBLE_QUOTE_START) {
					switchState(TokenState.DOUBLE_QUOTE_END);
				} else {
					switchState(TokenState.DOUBLE_QUOTE_START);
				}
				break;
			default:
				switchState(TokenState.NORMAL_CHARACTER_READING);
			}
			append(c);

		}
	}

	private void append(char c) {
		context.appendCurrentText(c);
	}

	void switchState(TokenState newState) {
		if (traceEnabled) {
			trace("switch state from " + currentState + " to " + newState);
		}
		/*
		 * Currently many tokens are ignored - e.g. comments are not created, or
		 * strings etc.)
		 */

		switch (newState) {
		case INITIALIZING:
			newActiveToken(null);
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
			newActiveToken(GSTRING);
			break;
		case SINGLE_QUOTE_START:
			newActiveToken(STRING);
			break;
		case MULTILINE_COMMENT_START_FOUND:
			newActiveToken(COMMENT__MULTI_LINE);
			break;
		case MULTILINE_COMMENT_END_FOUND:
			closeActiveToken();
			break;
		case SINGLE_LINE_COMMENT_START_FOUND:
			newActiveToken(COMMENT__SINGLE_LINE);
			break;
		case CURLY_BRACKET_START_FOUND:
			if (currentState == TokenState.MULTILINE_COMMENT_START_FOUND) {
				/* ignore it'S inside a comment */
				return;
			}
			newActiveToken(TokenType.BRACE_OPENING);
			switchParentToLastToken();

			break;
		case CURLY_BRACKET_END_FOUND:
			if (currentState == TokenState.MULTILINE_COMMENT_START_FOUND) {
				/* ignore it'S inside a comment */
				return;
			}
			if (lastToken == null) {
				handleProblem("Curly bracket closing, but wrong position!");
				return;
			}
			newActiveToken(BRACE_CLOSING);
			// closeActiveToken();
			/* switch back to parent token! */
			switchParentToActiveParentsParent();
			setActiveTokenToLastToken();
			if (traceEnabled) {
				trace("switched back to parent:" + createTokenString(activeToken));
			}
			break;
		case NORMAL_CHARACTER_READING:
			ensureActiveToken();
			break;
		default:
			if (traceEnabled) {
				trace("unhandled new state:" + newState);
			}

		}
		this.currentState = newState;
	}

	private void switchParentToLastToken() {
		if (traceEnabled) {
			trace("SWITCH PARENT (A) from " + activeParent + " to last token:" + createTokenString(lastToken));
		}
		activeParent = lastToken;
	}

	private void switchParentToActiveParentsParent() {
		if (activeParent != null) {
			if (traceEnabled) {
				trace("SWITCH PARENT (B) from " + createTokenString(activeParent) + " to last token:"
						+ createTokenString(activeParent.getParent()));
			}
			activeParent = activeParent.getParent();
		} else {
			if (traceEnabled) {
				trace("SWITCH PARENT (C) to active parents parent not possible - no active parent set!");
			}
		}
	}

	private void ensureActiveToken() {
		if (activeToken == null) {
			newActiveToken(null);
		}
	}

	/**
	 * Closes current token, will set name, type etc.
	 * 
	 * @return <code>true</code>when active token is closed, <code>false</code>
	 *         when no active token was available or active token contains only
	 *         whitespaces
	 */
	private boolean closeActiveToken() {
		if (traceEnabled) {
			trace("CLOSE active " + createTokenString(activeToken) + " requested");
		}
		if (activeToken == null) {
			if (traceEnabled) {
				trace("NULL active token - close not necessary");
			}
			return true;
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
		activeToken.setName(name);

		TokenType type = activeToken.getType();
		/*
		 * some stages are able to set token type already - so only if not use
		 * analyzer
		 */
		if (type == TokenType.UNKNOWN) {
			TokenType tokenType = tokenTypeAnalyzer.analyze(activeToken);
			activeToken.setType(tokenType);
		}

		if (traceEnabled) {
			trace("CLOSING " + createTokenString(activeToken));
		}

		if (activeParent == null) {
			context.getResult().add(activeToken);
			if (traceEnabled) {
				trace("ADDED " + createTokenString(activeToken) + " to root");
			}
		} else {
			activeParent.addChild(activeToken);
			if (traceEnabled) {
				trace("ADDED " + createTokenString(activeToken) + " to parent:"
						+ createTokenString(activeToken.getParent()));
			}
		}
		setActiveTokenToLastToken();
		resetActiveToken();

		context.resetCurrentText();
		if (traceEnabled) {
			trace("CLOSE DONE");
		}
		return true;
	}

	private String createTokenString(Token token) {
		if (token == null) {
			return "No Token";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Token(");
		sb.append(token.getId());
		sb.append(")");
		String name = token.getName();
		if (StringUtils.isNotEmpty(name)) {
			sb.append("[");
			sb.append(name);
			sb.append("]");
		}
		return sb.toString();
	}

	private void setActiveTokenToLastToken() {
		if (traceEnabled) {
			trace("LAST token now:" + createTokenString(activeToken));
		}
		lastToken = activeToken;

	}

	private void resetActiveToken() {
		activeToken = null;
		if (traceEnabled) {
			trace("RESET active token to " + createTokenString(activeToken));
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

	private boolean newActiveToken(TokenType type) {
		boolean newTokenNecessary = closeActiveToken();
		if (!newTokenNecessary) {
			if (traceEnabled) {
				trace("KEEP " + createTokenString(activeToken) + " because close not necessary");
			}
		}
		if (traceEnabled) {
			trace(context.toString());
		}
		Token token = null;
		if (newTokenNecessary) {
			token = new Token(context.createNewTokenId());
		} else {
			token = activeToken;
		}
		token.setType(type);
		token.setOffset(context.getOffset());
		token.setLineNumber(context.getLineNumber());
		if (traceEnabled) {
			trace(createTokenString(token) + (newTokenNecessary ? " created" : " reused"));
		}
		activeToken = token;

		return newTokenNecessary;
	}

}
