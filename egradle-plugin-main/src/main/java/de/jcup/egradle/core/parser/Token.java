package de.jcup.egradle.core.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Token {

	
	
	private int id;
	private String name;
	private int lineNumber = -1;
	private Token parent;
	private List<Token> children = new ArrayList<>();
	private int offset;
	private TokenType type;

	Token(int id){
		this.id=id;
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
		return getClass().getSimpleName() + "("+id+") ['"+name +"', LNr:" + lineNumber + ", offset:" + offset + ", type:"+getType()+", parent:"+(parent!=null ? parent.id : "null") +"]";
	}

	Token setType(TokenType tokenType) {
		this.type = tokenType;
		return this;
	}

	/**
	 * Returns token type. If no token type is set {@link TokenType#UNKNOWN} will be returned
	 * @return token type , never <code>null</code>
	 */
	public TokenType getType() {
		if (type==null){
			type = TokenType.UNKNOWN;
		}
		return type;
	}

	public void setName(String name) {
		this.name=name;
	}

	public void addChild(Token child) {
		children.add(child);
		child.parent=this;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber=lineNumber;
	}

}
