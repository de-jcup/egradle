package de.jcup.egradle.eclipse.gradleeditor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import de.jcup.egradle.codecompletion.dsl.Type;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.eclipse.gradleeditor.codecompletion.GradleContentAssistProcessor;

public class GradleTextHover implements ITextHover {

	private GradleSourceViewerConfiguration gradleSourceViewerConfiguration;
	private ISourceViewer sourceViewer;
	private String contentType;

	public GradleTextHover(GradleSourceViewerConfiguration gradleSourceViewerConfiguration, ISourceViewer sourceViewer,
			String contentType) {
		this.gradleSourceViewerConfiguration = gradleSourceViewerConfiguration;
		this.sourceViewer=sourceViewer;
		this.contentType=contentType;
	}

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		Type type = getTypeAt(hoverRegion.getOffset());
		if (type == null) {
			return null;
		}
		return "Type:"+type.getName()+": "+type.getDescription();
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return new Region(offset, 0);
	}

	public Type getTypeAt(int offset) {
		IContentAssistant assist = gradleSourceViewerConfiguration.getContentAssistant(sourceViewer);
		if (assist == null) {
			return null;
		}
		IContentAssistProcessor processor = assist.getContentAssistProcessor(contentType);
		if (!(processor instanceof GradleContentAssistProcessor)) {
			return null;
		}
		GradleContentAssistProcessor gprocessor = (GradleContentAssistProcessor) processor;
		Model model = gprocessor.getModel();
		if (model==null){
			return null;
		}
		Item item = model.getItemAt(offset);
		if (item == null) {
			return null;
		}
		Type type = gprocessor.getEstimator().estimateFromGradleProjectAsRoot(item);
		return type;
	}

	// public String getHoverInfo(ITextViewer tv, IRegion r) {
	// try {
	// IDocument doc = tv.getDocument();
	// EscriptModel em = EscriptModel.getModel(doc, null);
	// return em.getElementAt(r.getOffset()).
	// getHoverHelp();
	// }
	// catch (Exception e) {
	// return "";
	// }
	// }
}