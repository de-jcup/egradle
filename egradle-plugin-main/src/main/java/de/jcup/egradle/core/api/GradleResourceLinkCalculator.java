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
package de.jcup.egradle.core.api;

import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class GradleResourceLinkCalculator {
	
	/**
	 * Creates a link result or <code>null</code>
	 * 
	 * @param line
	 * @param offsetInLine
	 * @return link result or <code>null</code>
	 */
	public GradleHyperLinkResult createResourceLinkString(String line, int offsetInLine) {
		if (line == null) {
			return null;
		}
		if (offsetInLine < 0) {
			return null;
		}
		int lineLength = line.length();
		if (offsetInLine >= lineLength) {
			return null;
		}
		return xyz(line, offsetInLine);
	}

	private GradleHyperLinkResult xyz(String line, int offsetInLine) {
		/* e.g. abc defg Test abc */
		/* ^-- Test must be identified */
		String rightSubString = line.substring(offsetInLine);
		StringBuilder content = new StringBuilder();
		for (char c : rightSubString.toCharArray()) {
			if (Character.isWhitespace(c)) {
				break;
			}
			if (c=='{'){
				break;
			}
			if (c=='('){
				break;
			}
			if (c==')'){
				break;
			}
			if (c=='['){
				break;
			}
			if (c=='<'){
				break;
			}
			if (c=='>'){
				break;
			}
			if (!Character.isJavaIdentifierPart(c)) {
				return null;
			}
			content.append(c);
		}
		if (StringUtils.isBlank(content)) {
			return null;
		}
		String leftSubString = line.substring(0, offsetInLine);
		char[] leftCharsArray = leftSubString.toCharArray();

		ArrayUtils.reverse(leftCharsArray);
		int startPos = offsetInLine;
		for (char c : leftCharsArray) {
			if (c=='('){
				break;
			}
			if (c=='<'){
				break;
			}
			if (Character.isWhitespace(c)) {
				break;
			}
			if (!Character.isJavaIdentifierPart(c)) {
				return null;
			}
			startPos--;
			content.insert(0, c);
		}
		String linkContent = content.toString();
	
		char firstChar = linkContent.charAt(0);
		if (!Character.isJavaIdentifierStart(firstChar)) {
			return null;
		}
		/* currently this calculator only supports correct Type syntax means first char MUST be upper cased*/
		if (! Character.isUpperCase(firstChar)){
			return null;
		}
		GradleHyperLinkResult result = new GradleHyperLinkResult();
		result.linkOffsetInLine = startPos;
		result.linkContent = linkContent;
		result.linkLength = linkContent.length();
		return result;
	}

}
