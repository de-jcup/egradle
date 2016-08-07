package de.jcup.egradle.eclipse.editors;

import org.eclipse.jface.text.rules.*;

public class XMLPartitionScanner extends RuleBasedPartitionScanner {
	public final static String GRADLE_COMMENT = "__gradle_comment";
	public final static String GRADLE_KEYWORD = "__gradle_keyword";
	public static final String GRADLE_STRING = "__gradle_string";;
	public static final String GRADLE_APPLY = "__gradle_apply";
	public final static String XML_TAG = "__xml_tag";

	public XMLPartitionScanner() {

		IToken gradleComment = new Token(GRADLE_COMMENT);
		IToken gradleKeyWord = new Token(GRADLE_KEYWORD);
		IToken gradleString = new Token(GRADLE_STRING);
		IToken gradleApplyPlugin = new Token(GRADLE_APPLY);

		IToken tag = new Token(XML_TAG);

		IPredicateRule[] rules = new IPredicateRule[7];
		int index=0;
		rules[index++] = new MultiLineRule("/*", "*/", gradleComment);
		rules[index++] = new SingleLineRule("//", "", gradleComment);
		rules[index++] = new PatternRule("dependencies", " ", gradleKeyWord,' ',true);
		rules[index++] = new PatternRule("task", " ", gradleKeyWord,' ',true);
		rules[index++] = new PatternRule("apply", " ", gradleApplyPlugin,' ',true);
		rules[index++] = new MultiLineRule("\"", "\"", gradleString);
		rules[index++] = new MultiLineRule("\'", "\'", gradleString);
		
//		rules[1] = new MultiCommentRule(tag);

		setPredicateRules(rules);
	}
}
