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
package de.jcup.egradle.eclipse.gradleeditor.document;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class GradleDocumentPartitionScanner extends RuleBasedPartitionScanner {
	public final static String GRADLE_COMMENT = "__gradle_comment";
	public final static String GRADLE_KEYWORD = "__gradle_keyword";
	public static final String GRADLE_STRING = "__gradle_string";;
	public static final String GRADLE_APPLY = "__gradle_apply";

	public GradleDocumentPartitionScanner() {

		IToken gradleComment = new Token(GRADLE_COMMENT);
		IToken gradleKeyWord = new Token(GRADLE_KEYWORD);
		IToken gradleString = new Token(GRADLE_STRING);
		IToken gradleApplyPlugin = new Token(GRADLE_APPLY);

		List<IPredicateRule> rules = new ArrayList<>();
		rules.add(new MultiLineRule("/*", "*/", gradleComment));
		rules.add(new SingleLineRule("//", "", gradleComment));
		rules.add(new PatternRule("dependencies", " ", gradleKeyWord, ' ', true));
		rules.add(new PatternRule("task", " ", gradleKeyWord, ' ', true));
		rules.add(new PatternRule("apply", " ", gradleApplyPlugin, ' ', true));
		rules.add(new MultiLineRule("\"", "\"", gradleString));
		rules.add(new MultiLineRule("\'", "\'", gradleString));

		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}
}
