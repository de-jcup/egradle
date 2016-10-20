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
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordPatternRule;

import de.jcup.egradle.eclipse.gradleeditor.document.keywords.DocumentKeyWord;
import de.jcup.egradle.eclipse.gradleeditor.document.keywords.GradleDefaultClosureKeyWords;
import de.jcup.egradle.eclipse.gradleeditor.document.keywords.GradleLinkKeyWords;
import de.jcup.egradle.eclipse.gradleeditor.document.keywords.GradleSpecialVariableKeyWords;
import de.jcup.egradle.eclipse.gradleeditor.document.keywords.GradleTaskKeyWords;
import de.jcup.egradle.eclipse.gradleeditor.document.keywords.GroovyKeyWords;
import de.jcup.egradle.eclipse.gradleeditor.document.keywords.JavaKeyWords;
import de.jcup.egradle.eclipse.gradleeditor.document.keywords.JavaLiteralKeyWords;

import static de.jcup.egradle.eclipse.gradleeditor.document.GradleDocumentIdentifiers.*;
public class GradleDocumentPartitionScanner extends RuleBasedPartitionScanner {

	private OnlyLettersKeyWordDetector onlyLettersWordDetector = new OnlyLettersKeyWordDetector();

	public GradleDocumentPartitionScanner() {

		IToken groovyComment = createToken(COMMENT);
		IToken groovyString = createToken(STRING);
		IToken groovyKeyWord = createToken(GROOVY_KEYWORD);
		IToken javaKeyWord = createToken(JAVA_KEYWORD);
		IToken javaLiteral = createToken(JAVA_LITERAL);

		IToken gradleKeyWord = createToken(GRADLE_KEYWORD);
		IToken gradleVariable = createToken(GRADLE_VARIABLE);
		IToken gradleLinkKeyWord = createToken(GRADLE_LINK_KEYWORD);
		IToken gradleTaskKeyWord = createToken(GRADLE_TASK_KEYWORD);

		List<IPredicateRule> rules = new ArrayList<>();
		rules.add(new MultiLineRule("/*", "*/", groovyComment));
		rules.add(new SingleLineRule("//", "", groovyComment));
		rules.add(new MultiLineRule("\"", "\"", groovyString));
		rules.add(new MultiLineRule("\'", "\'", groovyString));
		
		buildWordRules(rules, gradleKeyWord, GradleDefaultClosureKeyWords.values());
		buildWordRules(rules, gradleLinkKeyWord, GradleLinkKeyWords.values());
		buildWordRules(rules, gradleTaskKeyWord, GradleTaskKeyWords.values());
		buildWordRules(rules, groovyKeyWord, GroovyKeyWords.values());
		buildWordRules(rules, javaKeyWord, JavaKeyWords.values());
		buildWordRules(rules, javaLiteral, JavaLiteralKeyWords.values());
		buildWordRules(rules, gradleVariable, GradleSpecialVariableKeyWords.values());
		
		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}

	private void buildWordRules(List<IPredicateRule> rules, IToken token,
			DocumentKeyWord[] values) {
		for (DocumentKeyWord keyWord: values){
			rules.add(new WordPatternRule(onlyLettersWordDetector, createWordStart(keyWord), null,token));
		}
	}

	private String createWordStart(DocumentKeyWord keyWord) {
		return keyWord.getText();
	}

	private IToken createToken(GradleDocumentIdentifier identifier) {
		return new Token(identifier.getId());
	}
}
