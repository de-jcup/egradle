package de.jcup.egradle.core.token;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class TokenImpl implements Token {
	private static Map<Integer, String> INDENT_CACHE = new HashMap<>();
	private int id;
	private String name;
	private int lineNumber = -1;
	private TokenImpl parent;
	private List<TokenImpl> children = new ArrayList<>();
	private int offset;
	private TokenType type;
	private Token forward;
	private Token backward;
	private int length=-1;

	/**
	 * Creates token with given id. The id should be unique inside tokens context!!!
	 * @param id identifier, should be unique in tokens context!
	 */
	public TokenImpl(int id) {
		this.id = id;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.token.Token#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.token.Token#getLineNumber()
	 */
	@Override
	public int getLineNumber() {
		return lineNumber;
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.token.Token#getChildren()
	 */
	@Override
	public List<TokenImpl> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.token.Token#getParent()
	 */
	@Override
	public TokenImpl getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.token.Token#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return children.size() > 0;
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.token.Token#getOffset()
	 */
	@Override
	public int getOffset() {
		return offset;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.token.Token#getLength()
	 */
	@Override
	public int getLength() {
		if (length<0){
			if (name==null){
				length=0;
			}else{
				length=name.length();
			}
		}
		return length;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + id + ") ['" + name + "', LNr:" + lineNumber + ", offset:" + offset
				+ ", length:"+getLength()+", type:" + getType() + ", parent:" + (parent != null ? parent.toIdString() : "null") + "]";
	}

	public Token setType(TokenType tokenType) {
		this.type = tokenType;
		return this;
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.token.Token#getType()
	 */
	@Override
	public TokenType getType() {
		if (type == null) {
			type = TokenType.UNKNOWN;
		}
		return type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addChild(TokenImpl child) {
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
		sb.append("TokenImpl(");
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

	private void printToken(int indent, Token tokenImpl, PrintStream stream) {
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
		stream.println(tokenImpl);
		if (tokenImpl.hasChildren()) {
			for (Token child : tokenImpl.getChildren()) {
				printToken(indent + 3, child, stream);
			}
		}

	}

	/**
	 * Define next following token to this one
	 * 
	 * @param tokenImpl
	 */
	void defineForward(Token tokenImpl) {
		this.forward = tokenImpl;
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.token.Token#goForward()
	 */
	@Override
	public Token goForward() {
		if (forward==null){
			throw new IllegalStateException("Cannot go forward from :"+toIdString());
		}
		return forward;
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.token.Token#canGoForward()
	 */
	@Override
	public boolean canGoForward() {
		return forward != null;
	}

	void defineBackward(Token tokenImpl) {
		backward = tokenImpl;
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.token.Token#goBackward()
	 */
	@Override
	public Token goBackward() {
		if (backward==null){
			throw new IllegalStateException("Cannot go backward from :"+toIdString());
		}
		return backward;
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.token.Token#canGoBackward()
	 */
	@Override
	public boolean canGoBackward() {
		return backward != null;
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.token.Token#getFirstChild()
	 */
	@Override
	public Token getFirstChild() {
		if (!hasChildren()){
			throw new IllegalStateException("There exists no first child for :"+toIdString());
		}
		return children.get(0);
	}

}
