package de.jcup.egradle.eclipse.gradleeditor.document;

import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

public class GradlePartitioner extends FastPartitioner{

	public GradlePartitioner(IPartitionTokenScanner scanner, String[] legalContentTypes) {
		super(scanner, legalContentTypes);
	}

}
