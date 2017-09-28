package de.jcup.egradle.eclipse.document;

import static de.jcup.egradle.eclipse.document.GroovyDocumentIdentifiers.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordPatternRule;

import de.jcup.egradle.core.text.DocumentIdentifier;

/**
 * This abstract partition scanner handles groovy and java out of the box. Classes extending this 
 * scanner only have to handle other rules.
 * @author Albert Tregnaghi
 *
 */
public abstract class AbstractGroovyBasedDocumentPartitionScanner extends RuleBasedPartitionScanner {

	protected OnlyLettersKeyWordDetector onlyLettersWordDetector = new OnlyLettersKeyWordDetector();
	private AnnotationWordDetector onlyAnnotationWordDetector = new AnnotationWordDetector();
	private JavaWordDetector javaWordDetector = new JavaWordDetector();

	public AbstractGroovyBasedDocumentPartitionScanner() {
		List<IPredicateRule> rules = new ArrayList<>();

		addGroovyRules(rules);

		addOtherRules(rules);

		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}

	protected void addGroovyRules(List<IPredicateRule> rules) {
		IToken groovyAnnotation = createToken(ANNOTATION);
		IToken javaDocComment = createToken(GROOVY_DOC);
		IToken groovyComment = createToken(COMMENT);
		IToken groovySimpleString = createToken(STRING);
		IToken groovyGString = createToken(GSTRING);
		IToken groovyKeyWord = createToken(GROOVY_KEYWORD);
		IToken javaKeyWord = createToken(JAVA_KEYWORD);
		IToken javaLiteral = createToken(JAVA_LITERAL);

		rules.add(new MultiLineRule("/**", "*/", javaDocComment));
		rules.add(new MultiLineRule("/*", "*/", groovyComment));
		rules.add(new SingleLineRule("//", "", groovyComment));

		rules.add(new MultiLineRule("\"", "\"", groovyGString, '\\'));
		rules.add(new MultiLineRule("\'", "\'", groovySimpleString, '\\'));

		buildWordRules(rules, javaKeyWord, JavaKeyWords.values(), javaWordDetector);
		buildWordRules(rules, javaLiteral, JavaLiteralKeyWords.values(), javaWordDetector);

		buildWordRules(rules, groovyKeyWord, GroovyKeyWords.values(), javaWordDetector);

		buildAnnotationRules(rules, groovyAnnotation, onlyAnnotationWordDetector);
	}
	
	protected abstract void addOtherRules(List<IPredicateRule> rules);

	private void buildAnnotationRules(List<IPredicateRule> rules, IToken token, IWordDetector wordDetector) {
		rules.add(new WordPatternRule(wordDetector, "@", "", token));

	}

	protected void buildWordRules(List<IPredicateRule> rules, IToken token, DocumentKeyWord[] values,
			IWordDetector wordDetector) {
		for (DocumentKeyWord keyWord : values) {
			rules.add(new ExactWordPatternRule(wordDetector, createWordStart(keyWord), token));
		}
	}

	private String createWordStart(DocumentKeyWord keyWord) {
		return keyWord.getText();
	}

	protected IToken createToken(DocumentIdentifier identifier) {
		return new Token(identifier.getId());
	}

}