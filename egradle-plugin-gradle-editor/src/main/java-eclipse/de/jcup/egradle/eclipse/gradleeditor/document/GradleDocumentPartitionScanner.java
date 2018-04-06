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

import static de.jcup.egradle.eclipse.gradleeditor.document.GradleDocumentIdentifiers.*;

import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;

import de.jcup.egradle.eclipse.document.AbstractGroovyBasedDocumentPartitionScanner;
import de.jcup.egradle.eclipse.gradleeditor.document.keywords.GradleApplyKeyWords;
import de.jcup.egradle.eclipse.gradleeditor.document.keywords.GradleDefaultClosureKeyWords;
import de.jcup.egradle.eclipse.gradleeditor.document.keywords.GradleSpecialVariableKeyWords;
import de.jcup.egradle.eclipse.gradleeditor.document.keywords.GradleTaskKeyWords;

public class GradleDocumentPartitionScanner extends AbstractGroovyBasedDocumentPartitionScanner {

	protected void addOtherRules(List<IPredicateRule> rules) {
		IToken gradleClosureKeywords = createToken(GRADLE_KEYWORD);
		IToken gradleVariable = createToken(GRADLE_VARIABLE);
		IToken gradleApplyKeyWord = createToken(GRADLE_APPLY_KEYWORD);
		IToken gradleTaskKeyWord = createToken(GRADLE_TASK_KEYWORD);

		buildWordRules(rules, gradleClosureKeywords, GradleDefaultClosureKeyWords.values(), onlyLettersWordDetector);
		buildWordRules(rules, gradleApplyKeyWord, GradleApplyKeyWords.values(), onlyLettersWordDetector);
		buildWordRules(rules, gradleTaskKeyWord, GradleTaskKeyWords.values(), onlyLettersWordDetector);
		buildWordRules(rules, gradleVariable, GradleSpecialVariableKeyWords.values(), onlyLettersWordDetector);
	}
}
