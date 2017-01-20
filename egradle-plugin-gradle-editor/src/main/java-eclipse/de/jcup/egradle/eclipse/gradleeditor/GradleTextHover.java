package de.jcup.egradle.eclipse.gradleeditor;

import org.eclipse.jface.internal.text.html.HTMLTextPresenter;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.codecompletion.dsl.Type;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.eclipse.gradleeditor.codecompletion.GradleContentAssistProcessor;

public class GradleTextHover implements ITextHover, ITextHoverExtension {

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
		return "<html><body><b>"+type.getName()+"</b><br><br>"+type.getDescription()+"</body></html>";
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

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		return new IInformationControlCreator() {
			
			@Override
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent,true);
			}
		};
	}

}