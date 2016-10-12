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
package de.jcup.egradle.eclipse.editors;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class GradlePartitionScanner extends RuleBasedPartitionScanner {
	public final static String GRADLE_COMMENT = "__gradle_comment";
	public final static String GRADLE_KEYWORD = "__gradle_keyword";
	public static final String GRADLE_STRING = "__gradle_string";;
	public static final String GRADLE_APPLY = "__gradle_apply";
	public final static String XML_TAG = "__xml_tag";

	public GradlePartitionScanner() {

		IToken gradleComment = new Token(GRADLE_COMMENT);
		IToken gradleKeyWord = new Token(GRADLE_KEYWORD);
		IToken gradleString = new Token(GRADLE_STRING);
		IToken gradleApplyPlugin = new Token(GRADLE_APPLY);

//		IToken tag = new Token(XML_TAG);

		IPredicateRule[] rules = new IPredicateRule[7];
		int index = 0;
		rules[index++] = new MultiLineRule("/*", "*/", gradleComment);
		rules[index++] = new SingleLineRule("//", "", gradleComment);
		rules[index++] = new PatternRule("dependencies", " ", gradleKeyWord, ' ', true);
		rules[index++] = new PatternRule("task", " ", gradleKeyWord, ' ', true);
		rules[index++] = new PatternRule("xapply", null, gradleApplyPlugin, ' ', true);
		rules[index++] = new MultiLineRule("\"", "\"", gradleString);
		rules[index++] = new MultiLineRule("\'", "\'", gradleString);

		// rules[1] = new MultiCommentRule(tag);

		setPredicateRules(rules);
	}
}
