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
 package de.jcup.egradle.core.token.parser;

enum TokenParserState {
	INITIALIZING,

	/* parenthesis, curly brackets/braces */
	CURLY_BRACKET_END_FOUND,

	CURLY_BRACKET_START_FOUND,
	
	NORMAL_BRACKET_END_FOUND,

	NORMAL_BRACKET_START_FOUND,

	/* whitespace detected */
	WHITESPACE_FOUND,
	
	/* END OF FILE */
	EOF_FOUND,
	
	/* multi line comment*/

	MULTILINE_COMMENT_END_FOUND,

	MULTILINE_COMMENT_START_FOUND,

	/* single line comment*/
	SINGLE_LINE_COMMENT_START_FOUND,
	
	/* new line start*/
	NEW_LINE_START,
	
	/* normal strings :*/
	SINGLE_QUOTE_START,
	
	SINGLE_QUOTE_END,
	
	/* GStrings */
	DOUBLE_QUOTE_START,
	
	DOUBLE_QUOTE_END, 
	
	/* normal character reading */
	NORMAL_CHARACTER_READING,
	

}