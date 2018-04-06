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
package de.jcup.egradle.eclipse.document;

public enum GroovyKeyWords implements DocumentKeyWord {

	// http://docs.groovy-lang.org/latest/html/documentation/#_keywords
	AS("as"),
	// assert by java
	// break by java
	// case by java
	// catch by java
	// class by java
	// const by java
	// continue by java
	DEF("def"),
	// default by java
	// do by java
	// else by java
	// enum by java
	// extends by java
	// false by java
	// finally by java
	// for by java
	// goto by java
	// if by java
	// implements by java
	// import by java
	IN("in"),
	// instanceof by java
	// interface by java
	// new by java
	// null by java literals
	// package by java
	// return by java
	// super by java
	// switch by java
	// this by java
	// throw by java
	// throws by java
	TRAIT("trait"),
	// true by java literal
	// try by java
	// while by java

	/* special closure names: */

	IT("it"),

	DELEGATE("delegate"),

	OWNER("owner"),

	THREADSAFE("threadsafe"), // reserved, but not used : see
								// http://www.groovy-lang.org/mailing-lists.html#nabble-td348443

	/* special GDK methods */
	USE("use"), // use method from GDK

	WITH("with"), // with(closure) method from GDK
	;

	private String text;

	private GroovyKeyWords(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}
}
