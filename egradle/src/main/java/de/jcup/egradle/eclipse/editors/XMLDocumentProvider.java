package de.jcup.egradle.eclipse.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

public class XMLDocumentProvider extends FileDocumentProvider {

	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner = new FastPartitioner(new XMLPartitionScanner(),
					new String[] { XMLPartitionScanner.GRADLE_KEYWORD, XMLPartitionScanner.GRADLE_APPLY,XMLPartitionScanner.GRADLE_COMMENT, XMLPartitionScanner.GRADLE_STRING,});
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
}