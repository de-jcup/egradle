package de.jcup.egradle.eclipse.gradleeditor.document;

public class GradlePartionerFactory {

	public static GradlePartitioner create(){
		String[] legalContentTypes = GradleDocumentIdentifiers.allIdsToStringArray();

		GradleDocumentPartitionScanner scanner = new GradleDocumentPartitionScanner();
		GradlePartitioner partitioner = new GradlePartitioner(scanner, legalContentTypes);
		
		return partitioner;
	}
}
