package de.jcup.egradle.core.parser;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Token {
	private static Map<Integer, String> INDENT_CACHE = new HashMap<>();
	private int id;
	private String name;
	private int lineNumber = -1;
	private Token parent;
	private List<Token> children = new ArrayList<>();
	private int offset;
	private TokenType type;
	private Token forward;
	private Token backward;

	Token(int id) {
		this.id = id;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getName() {
		return name;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Returns unmodifiable list of children, never <code>null</code>
	 * 
	 * @return unmodifiable list of children, never <code>null</code>
	 */
	public List<Token> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * Returns parent element or <code>null</code>
	 * 
	 * @return parent element or <code>null</code>
	 */
	public Token getParent() {
		return parent;
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	/**
	 * Returns offset inside complete source
	 * 
	 * @return offset inside complete source
	 */
	public int getOffset() {
		return offset;
	}

	public int getLength() {
		if (name == null) {
			return 0;
		}
		return name.length();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + id + ") ['" + name + "', LNr:" + lineNumber + ", offset:" + offset
				+ ", type:" + getType() + ", parent:" + (parent != null ? parent.id : "null") + "]";
	}

	Token setType(TokenType tokenType) {
		this.type = tokenType;
		return this;
	}

	/**
	 * Returns token type. If no token type is set {@link TokenType#UNKNOWN}
	 * will be returned
	 * 
	 * @return token type , never <code>null</code>
	 */
	public TokenType getType() {
		if (type == null) {
			type = TokenType.UNKNOWN;
		}
		return type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addChild(Token child) {
		if (child == this) {
			throw new IllegalStateException("Tried to add token to itself:" + toIdString());
		}
		children.add(child);
		child.parent = this;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getId() {
		return id;
	}

	public String toIdString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Token(");
		sb.append(getId());
		sb.append(")");
		String name = getName();
		if (StringUtils.isNotEmpty(name)) {
			sb.append("[");
			sb.append(name);
			sb.append("]");
		}
		if (type != null) {
			sb.append("[");
			sb.append(type);
			sb.append("]");
		}
		return sb.toString();
	}

	public void print() {
		print(System.out);
	}

	public void print(PrintStream stream) {
		printToken(0, this, stream);
	}

	private void printToken(int indent, Token token, PrintStream stream) {
		String indentStr = INDENT_CACHE.get(indent);
		if (indentStr == null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < indent; i++) {
				sb.append(' ');
			}
			indentStr = sb.toString();
			INDENT_CACHE.put(indent, indentStr);
		}
		stream.print(indentStr);
		stream.println(token);
		if (token.hasChildren()) {
			for (Token child : token.getChildren()) {
				printToken(indent + 3, child, stream);
			}
		}

	}

	/**
	 * Define next following token to this one
	 * 
	 * @param token
	 */
	void defineForward(Token token) {
		this.forward = token;
	}

	/**
	 * Go to next token
	 * 
	 * @return next token - throws {@link IllegalStateException} when not possible
	 */
	public Token goForward() {
		if (forward==null){
			throw new IllegalStateException("Cannot go forward from :"+toIdString());
		}
		return forward;
	}

	/**
	 * @return <code>true</code> when a next token is available otherwise
	 *         <code>false</code>
	 */
	public boolean canGoForward() {
		return forward != null;
	}

	void defineBackward(Token token) {
		backward = token;
	}

	/**
	 * Go to token before
	 * 
	 * @return token before - throws {@link IllegalStateException} when not possible
	 */
	public Token goBackward() {
		if (backward==null){
			throw new IllegalStateException("Cannot go backward from :"+toIdString());
		}
		return backward;
	}

	/**
	 * @return <code>true</code> when token before is available otherwise
	 *         <code>false</code>
	 */
	public boolean canGoBackward() {
		return backward != null;
	}

	/**
	 * @return first child - or throws {@link IllegalStateException} when no child available
	 */
	public Token getFirstChild() {
		if (!hasChildren()){
			throw new IllegalStateException("There exists no first child for :"+toIdString());
		}
		return children.get(0);
	}

}
