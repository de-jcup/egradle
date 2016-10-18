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
import static de.jcup.egradle.eclipse.gradleeditor.document.GradleDocumentIdentifiers.*;
public class GradleDocumentPartitionScanner extends RuleBasedPartitionScanner {

	public GradleDocumentPartitionScanner() {

		IToken groovyComment = createToken(GROOVY_COMMENT);
		IToken groovyString = createToken(GROOVY_STRING);
		IToken groovyKeyWord = createToken(GROOVY_KEYWORD);

		IToken gradleKeyWord = createToken(GRADLE_KEYWORD);
		IToken gradleLinkKeyWord = createToken(GRADLE_LINK_KEYWORD);
		IToken gradleTaskKeyWord = createToken(GRADLE_TASK_KEYWORD);

		List<IPredicateRule> rules = new ArrayList<>();
		rules.add(new MultiLineRule("/*", "*/", groovyComment));
		rules.add(new SingleLineRule("//", "", groovyComment));
		rules.add(new MultiLineRule("\"", "\"", groovyString));
		rules.add(new MultiLineRule("\'", "\'", groovyString));

		for (DocumentKeyWord keyWord: GradleSimpleKeyWords.values()){
			rules.add(new WordPatternRule(keyWord, keyWord.getText(), null, gradleKeyWord));
		}
		for (DocumentKeyWord keyWord: GradleLinkKeyWords.values()){
			rules.add(new WordPatternRule(keyWord, keyWord.getText(), null, gradleLinkKeyWord));
		}
		for (DocumentKeyWord keyWord: GroovyKeyWords.values()){
			rules.add(new WordPatternRule(keyWord, keyWord.getText(), null, groovyKeyWord));
		}
		for (DocumentKeyWord keyWord: GradleTaskKeyWords.values()){
			rules.add(new WordPatternRule(keyWord, keyWord.getText(), null, gradleTaskKeyWord));
		}

		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}

	private IToken createToken(GradleDocumentIdentifier identifier) {
		return new Token(identifier.getId());
	}
}
