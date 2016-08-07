package de.jcup.egradle.eclipse.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class GradleSourceViewerConfiguration extends SourceViewerConfiguration {
	private XMLDoubleClickStrategy doubleClickStrategy;
	private XMLTagScanner tagScanner;
	private XMLScanner scanner;
	private ColorManager colorManager;

	public GradleSourceViewerConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, XMLPartitionScanner.GRADLE_COMMENT,
				XMLPartitionScanner.GRADLE_KEYWORD, XMLPartitionScanner.GRADLE_APPLY, XMLPartitionScanner.GRADLE_STRING,
				XMLPartitionScanner.XML_TAG };
	}

	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new XMLDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected XMLScanner getXMLScanner() {
		if (scanner == null) {
			scanner = new XMLScanner(colorManager);
			scanner.setDefaultReturnToken(
					new Token(new TextAttribute(colorManager.getColor(IGradleColorConstants.DEFAULT))));
		}
		return scanner;
	}

	protected XMLTagScanner getXMLTagScanner() {
		if (tagScanner == null) {
			tagScanner = new XMLTagScanner(colorManager);
			tagScanner
					.setDefaultReturnToken(new Token(new TextAttribute(colorManager.getColor(IGradleColorConstants.TAG))));
		}
		return tagScanner;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getXMLTagScanner());
		reconciler.setDamager(dr, XMLPartitionScanner.XML_TAG);
		reconciler.setRepairer(dr, XMLPartitionScanner.XML_TAG);

		dr = new DefaultDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(
				new TextAttribute(colorManager.getColor(IGradleColorConstants.COMMENT)));
		reconciler.setDamager(ndr, XMLPartitionScanner.GRADLE_COMMENT);
		reconciler.setRepairer(ndr, XMLPartitionScanner.GRADLE_COMMENT);

		ndr = new NonRuleBasedDamagerRepairer(
				new TextAttribute(colorManager.getColor(IGradleColorConstants.OTHER_KEYWORDS)));
		reconciler.setDamager(ndr, XMLPartitionScanner.GRADLE_KEYWORD);
		reconciler.setRepairer(ndr, XMLPartitionScanner.GRADLE_KEYWORD);
		ndr = new NonRuleBasedDamagerRepairer(
				new TextAttribute(colorManager.getColor(IGradleColorConstants.APPLY)));
		reconciler.setDamager(ndr, XMLPartitionScanner.GRADLE_APPLY);
		reconciler.setRepairer(ndr, XMLPartitionScanner.GRADLE_APPLY);
		
		ndr = new NonRuleBasedDamagerRepairer(
				new TextAttribute(colorManager.getColor(IGradleColorConstants.STRING)));
		reconciler.setDamager(ndr, XMLPartitionScanner.GRADLE_STRING);
		reconciler.setRepairer(ndr, XMLPartitionScanner.GRADLE_STRING);

		return reconciler;
	}

}