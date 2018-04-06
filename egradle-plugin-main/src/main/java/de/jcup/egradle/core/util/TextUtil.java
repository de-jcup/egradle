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
package de.jcup.egradle.core.util;

public class TextUtil {

	private static final String EMPTY_STRING = "";

	/**
	 * Returns string containing only letters or digits starting from given
	 * offset. As soon as another character appears in text scanning will
	 * stop.<br>
	 * <br>
	 * Examples:
	 * <ul>
	 * <li>"alpha123 " will return "alpha123"</li>
	 * <li>"alpha123.abc" will return "alpha123"</li>
	 * <li>"alpha123(xx)" will return "alpha123"</li>
	 * </ul>
	 * 
	 * @param offset
	 * @param text
	 * @return string never <code>null</code>
	 */
	public static String getLettersOrDigitsAt(int offset, String text) {
		if (text == null) {
			return EMPTY_STRING;
		}
		if (offset < 0) {
			return EMPTY_STRING;
		}
		int textLength = text.length();
		int pos = offset;
		if (pos >= textLength) {
			return EMPTY_STRING;
		}
		StringBuilder sb = new StringBuilder();
		while (true) {
			if (pos >= textLength) {
				break;
			}
			char c = text.charAt(pos++);
			if (!Character.isLetterOrDigit(c)) {
				break;
			}
			sb.append(c);
		}
		return sb.toString();
	}

	public static String createSpaceString(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 * Trims only right whitespaces<br>
	 * <br>
	 * 
	 * <pre>
	 * "a "    -> "a"
	 * " a"    -> " a"
	 * " a "   -> " a"
	 * " a b " -> " a b"
	 * "a b "  -> "a b"
	 * </pre>
	 * 
	 * @param text
	 * @return trimmed text (right whitespaces removed)
	 */
	public static String trimRightWhitespaces(String text) {
		if (text == null) {
			return null;
		}
		if (text.length() == 0) {
			return text;
		}
		if (text.trim().length() == 0) {
			return EMPTY_STRING;
		}
		char[] charArray = text.toCharArray();

		boolean whitespaceFound = false;
		boolean normalCharFound = false;
		for (int i = charArray.length - 1; i >= 0; i--) {
			char c = charArray[i];
			if (Character.isWhitespace(c)) {
				whitespaceFound = true;
			} else {
				normalCharFound = true;
				/* normal character found */
				if (whitespaceFound) {
					/* char before was a whitespace! */
					return text.substring(0, i + 1);
				}
			}
		}
		if (whitespaceFound && !normalCharFound) {
			/* means only whitespaces found */
			return EMPTY_STRING;
		}
		return text;
	}
}
