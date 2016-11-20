/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
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
	private String value;
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

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public int getLineNumber() {
		return lineNumber;
	}

	@Override
	public List<Token> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public TokenImpl getParent() {
		return parent;
	}

	@Override
	public boolean hasChildren() {
		return children.size() > 0;
	}

	@Override
	public int getOffset() {
		return offset;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	@Override
	public int getLength() {
		if (length<0){
			if (value==null){
				length=0;
			}else{
				length=value.length();
			}
		}
		return length;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + id + ") ['" + value + "', LNr:" + lineNumber + ", offset:" + offset
				+ ", length:"+getLength()+", type:" + getType() + ", parent:" + (parent != null ? parent.toIdString() : "null") + "]";
	}

	public Token setType(TokenType tokenType) {
		this.type = tokenType;
		return this;
	}

	@Override
	public TokenType getType() {
		if (type == null) {
			type = TokenType.UNKNOWN;
		}
		return type;
	}

	public void setValue(String name) {
		this.value = name;
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
		String name = getValue();
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
	 * @param token
	 * @return chained token
	 */
	public TokenImpl chain(TokenImpl token) {
		this.forward = token;
		token.backward=this;
		return token;
	}

	@Override
	public Token goForward() {
		if (forward==null){
			throw new IllegalStateException("Cannot go forward from :"+toIdString());
		}
		return forward;
	}

	@Override
	public boolean canGoForward() {
		return forward != null;
	}

	@Override
	public Token goBackward() {
		if (backward==null){
			throw new IllegalStateException("Cannot go backward from :"+toIdString());
		}
		return backward;
	}

	@Override
	public boolean canGoBackward() {
		return backward != null;
	}

	@Override
	public Token getFirstChild() {
		if (!hasChildren()){
			throw new IllegalStateException("There exists no first child for :"+toIdString());
		}
		return children.get(0);
	}

}
