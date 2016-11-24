package de.jcup.egradle.eclipse.gradleeditor;

import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;

public final class GradlePairMatcher extends DefaultCharacterPairMatcher {

	public GradlePairMatcher(char[] pairs) {
		super(pairs, IDocumentExtension3.DEFAULT_PARTITIONING, true);
	}

}