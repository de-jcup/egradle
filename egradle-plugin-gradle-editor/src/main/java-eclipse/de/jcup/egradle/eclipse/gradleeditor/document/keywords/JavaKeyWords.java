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
 package de.jcup.egradle.eclipse.gradleeditor.document.keywords;

// see http://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html
public enum JavaKeyWords implements DocumentKeyWord {

	ABSTRACT("abstract"),
	
	CONTINUE("continue"),
	
	FOR("for"),

	NEW("new"),
	
	SWITCH("switch"),
	
	ASSERT("assert"),
	
	DEFAULT("default"),
	
	GOTO("goto"),
	
	PACKAGE("package"),
	
	SYNCHRONIZED("synchronized"),
	
	BOOLEAN("boolean"),
	
	DO("do"),
	
	IF("if"),
	
	PRIVATE("private"),
	
	THIS("this"),
	
	BREAK("break"),
	
	DOUBLE("double"),
	
	IMPLEMENTS("implements"),
	
	PROTECTED("protected"),
	
	THROW("throw"),
	
	BYTE("byte"),
	
	ELSE("else"),
	
	IMPORT("import"),
	
	PUBLIC("public"),
	
	THROWS("throws"),
	
	CASE("case"),
	
	ENUM("enum"), 
	
	INSTANCE_OF("instanceof"),
			
	RETURN("return"),
	
	TRANSIENT("transient"),
	
	CATCH("catch"),
	
	EXTENDS("extends"),
	
	INT("int"),
	
	SHORT("short"),
	
	TRY("try"),
	
	CHAR("char"),
	
	FINAL("final"),
	
	INTERFACE("interface"),
	
	STATIC("static"),
	
	VOID("void"),
	
	CLASS("class"),
	
	FINALLY("finally"),
	
	LONG("long"),
			
	STRICTFP("strictfp"),
	
	VOLATILE("volatile"),
	
	CONST("const"),
	
	FLOAT("float"),
	
	NATIVE("native"),
	
	SUPER("super"),
	
	WHILE("while")

	;

	private String text;

	private JavaKeyWords(String text) {
		this.text = text;
	}


	@Override
	public String getText() {
		return text;
	}
}
