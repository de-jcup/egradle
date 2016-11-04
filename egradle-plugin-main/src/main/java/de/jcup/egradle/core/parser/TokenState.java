package de.jcup.egradle.core.parser;

enum TokenState {
	INITIALIZING,

	/* parenthesis, curly brackets/braces */
	CURLY_BRACKET_END_FOUND,

	CURLY_BRACKET_START_FOUND,

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