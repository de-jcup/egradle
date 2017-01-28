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

import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.ItemType;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.eclipse.gradleeditor.codeassist.GradleContentAssistProcessor;

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
		HoverData data = getLanguageElementAt(hoverRegion.getOffset());
		if (data == null) {
			return null;
		}
		if (data.item == null) {
			return null;
		}
		if (data.element == null) {
			return null;
		}
		String description=null;
		if (data.element instanceof Method){
			Method method = (Method) data.element;
			Type returnType = method.getReturnType();
			if (returnType==null || data.item.getItemType()==ItemType.CLOSURE){
				/* when VOID(null) or it is a closure type use description instead of type */
				description=method.getDescription();
			}else{
				/* use method type as description...*/
				description=returnType.getDescription();
			}
		}else if (data.element instanceof Property){
			Property property = (Property) data.element;
			Type returnType = property.getType();
			if (returnType==null || data.item.getItemType()==ItemType.CLOSURE){
				/* when VOID(null) or it is a closure type use use property description instead of type */
				description=property.getDescription();
			}else{
				/* use method type as description...*/
				description=returnType.getDescription();
			}
		}
		return "<html><body><b>"+data.element.getName()+"</b><br><br>"+description+"</body></html>";
	}
	
	private class HoverData{
		private LanguageElement element;
		private Item item;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return new Region(offset, 0);
	}

	HoverData getLanguageElementAt(int offset) {
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
		HoverData data = new HoverData();
		data.item=item;
		GradleFileType fileType = gradleSourceViewerConfiguration.getFileType();
		GradleLanguageElementEstimater estimator = gprocessor.getEstimator();
		LanguageElement element = estimator.estimate(item,fileType);
		data.element=element;
		return data;
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