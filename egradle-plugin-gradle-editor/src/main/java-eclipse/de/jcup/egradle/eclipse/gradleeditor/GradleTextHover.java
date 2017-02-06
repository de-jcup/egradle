package de.jcup.egradle.eclipse.gradleeditor;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.codeassist.RelevantCodeCutter;
import de.jcup.egradle.codeassist.dsl.HTMLDescriptionBuilder;
import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater;
import de.jcup.egradle.codeassist.hover.HoverData;
import de.jcup.egradle.codeassist.hover.HoverSupport;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.gradleeditor.codeassist.GradleContentAssistProcessor;
import de.jcup.egradle.eclipse.gradleeditor.control.SimpleBrowserInformationControl;

public class GradleTextHover implements ITextHover, ITextHoverExtension {

	private HTMLDescriptionBuilder builder;
	private GradleSourceViewerConfiguration gradleSourceViewerConfiguration;
	private ISourceViewer sourceViewer;
	private String contentType;
	private GradleTextHoverControlCreator creator;
	
	private String fgColor;
	private String bgColor;
	private RelevantCodeCutter codeCutter;
	private HoverSupport hoverSupport;
	
	public GradleTextHover(GradleSourceViewerConfiguration gradleSourceViewerConfiguration, ISourceViewer sourceViewer,
			String contentType) {
		this.gradleSourceViewerConfiguration = gradleSourceViewerConfiguration;
		this.sourceViewer = sourceViewer;
		this.contentType = contentType;
		
		builder = new HTMLDescriptionBuilder();
		codeCutter = new RelevantCodeCutter();
		hoverSupport = new HoverSupport();
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		HoverDataRegion region = getLanguageElementAt(offset,textViewer);
		if (region != null) {
			return region;
		}
		return null;// do not hover!
	}

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		if (creator == null) {
			creator = new GradleTextHoverControlCreator();
		}
		return creator;
	}

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		HoverData data = null;
		if (hoverRegion instanceof HoverDataRegion) {
			HoverDataRegion hdr = (HoverDataRegion) hoverRegion;
			data = hdr.getData();
		}
		if (data == null) {
			HoverDataRegion hdr = getLanguageElementAt(hoverRegion.getOffset(),textViewer);
			if (hdr!=null){
				data = hdr.getData();
			}
		}
		if (data == null) {
			return null;
		}
		if (data.getItem() == null) {
			return null;
		}
		if (data.getResult() == null) {
			return null;
		}
		LanguageElement element = data.getResult().getElement();
		if (element == null) {
			return null;
		}
		if (bgColor==null || fgColor==null){
			
			StyledText textWidget = textViewer.getTextWidget();
			if (textWidget!=null){
				
				/* TODO ATR, 03.02.2017: there should be an easier approach to get editors back and foreground, without syncexec */
				EGradleUtil.getSafeDisplay().syncExec(new Runnable() {
					
					@Override
					public void run() {
						bgColor = EGradleUtil.convertToHexColor(textWidget.getBackground());
						fgColor = EGradleUtil.convertToHexColor(textWidget.getForeground());
					}
				});
			}
			
		}
		return builder.buildHTMLDescription(fgColor, bgColor ,data, element);
	}

	private class HoverDataRegion implements IRegion {

		private HoverData data;

		private HoverDataRegion(HoverData data) {
			if (data==null){
				throw new IllegalArgumentException("hover data may not be null");
			}
			this.data= data;
		}
		
		@Override
		public int getLength() {
			return data.getLength();
		}

		@Override
		public int getOffset() {
			return data.getOffset();
		}
		
		public HoverData getData() {
			return data;
		}
	}
	
	/**
	 * Get language at given offset
	 * @param offset
	 * @param textViewer 
	 * @return language element or <code>null</code>
	 */
	protected HoverDataRegion getLanguageElementAt(int offset, ITextViewer textViewer) {
		IContentAssistant assist = gradleSourceViewerConfiguration.getContentAssistant(sourceViewer);
		if (assist == null) {
			return null;
		}
		IContentAssistProcessor processor = assist.getContentAssistProcessor(contentType);
		if (!(processor instanceof GradleContentAssistProcessor)) {
			return null;
		}
		GradleContentAssistProcessor gprocessor = (GradleContentAssistProcessor) processor;
		String allText = textViewer.getDocument().get();
		RelevantCodeCutter codeCutter = this.codeCutter;
		Model model = gprocessor.getModel();
		GradleFileType fileType = gradleSourceViewerConfiguration.getFileType();
		GradleLanguageElementEstimater estimator = gprocessor.getEstimator();
		
		HoverData data = hoverSupport.caclulateHoverData(allText, offset, codeCutter, model, fileType, estimator);
		if (data==null){
			return null;
		}
		return new HoverDataRegion(data);
	}
	
	
	
	

	

	

	private class GradleTextHoverControlCreator implements IInformationControlCreator {

		@Override
		public IInformationControl createInformationControl(Shell parent) {
			if (SimpleBrowserInformationControl.isAvailableFor(parent)) {
				SimpleBrowserInformationControl control = new SimpleBrowserInformationControl(parent);
				control.setBrowserEGradleLinkListener(new DefaultEGradleLinkListener(fgColor,bgColor,builder));
				return control;
			} else {
				return new DefaultInformationControl(parent, true);
			}
		}
	}

}