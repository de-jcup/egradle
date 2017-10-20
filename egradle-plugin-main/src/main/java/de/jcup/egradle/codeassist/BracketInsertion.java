/*
 * Copyright 2017 Albert Tregnaghi
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
 package de.jcup.egradle.codeassist;

public enum BracketInsertion {
	/**
	 * Will result in multined curly braces indented as before
	 */
	CURLY_BRACES('{', '}', true), 
	
	/**
	 * Will result in simple "[  ]" and cursor will be after first space
	 */
	EDGED_BRACES('[', ']', false),
	
	;
	
	private char start;
	private char end;
	private boolean multiLine;

	private BracketInsertion(char start, char end, boolean multiLine) {
		this.start = start;
		this.end = end;
		this.multiLine = multiLine;
	}

	public boolean isMultiLine() {
		return multiLine;
	}

	public char getStart() {
		return start;
	}

	public char getEnd() {
		return end;
	}

	public static BracketInsertion valueOfStartChar(char c) {
		for (BracketInsertion data : BracketInsertion.values()) {
			if (data.start==c){
				return data;
			}
		}
		return null;
	}

	public String createOneLineTemplate() {
		StringBuilder sb = new StringBuilder();
		sb.append(start);
		sb.append("  ");
		sb.append(end);
		return sb.toString();
	}

	public int createOneLineNewOffset(int offset) {
		return offset+1;
	}

	public String createMultiLineTemplate(String cursorVariable) {
		StringBuilder sb = new StringBuilder();
		sb.append(start);
		sb.append("\n    ");
		sb.append(cursorVariable);
		sb.append("\n");
		sb.append(end);
		return  sb.toString();
	}



}