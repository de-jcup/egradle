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

import java.util.List;

public interface Token {

	/**
	 * @return value - means text representation of this token
	 */
	String getValue();

	int getLineNumber();

	/**
	 * Returns unmodifiable list of children, never <code>null</code>
	 * 
	 * @return unmodifiable list of children, never <code>null</code>
	 */
	List<Token> getChildren();

	/**
	 * Returns parent element or <code>null</code>
	 * 
	 * @return parent element or <code>null</code>
	 */
	Token getParent();

	boolean hasChildren();

	/**
	 * Returns offset inside complete source
	 * 
	 * @return offset inside complete source
	 */
	int getOffset();

	int getLength();

	/**
	 * Returns token type. If no token type is set {@link TokenType#UNKNOWN}
	 * will be returned
	 * 
	 * @return token type , never <code>null</code>
	 */
	TokenType getType();

	/**
	 * Go to next token
	 * 
	 * @return next token - throws {@link IllegalStateException} when not possible
	 */
	Token goForward();

	/**
	 * @return <code>true</code> when a next token is available otherwise
	 *         <code>false</code>
	 */
	boolean canGoForward();

	/**
	 * Go to token before
	 * 
	 * @return token before - throws {@link IllegalStateException} when not possible
	 */
	Token goBackward();

	/**
	 * @return <code>true</code> when token before is available otherwise
	 *         <code>false</code>
	 */
	boolean canGoBackward();

	/**
	 * @return first child - or throws {@link IllegalStateException} when no child available
	 */
	Token getFirstChild();
	

}