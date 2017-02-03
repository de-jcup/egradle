package de.jcup.egradle.eclipse.gradleeditor;

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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.codeassist.CodeCompletionRegistry;
import de.jcup.egradle.codeassist.dsl.HTMLDescriptionBuilder;
import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLTypeProvider;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater.EstimationResult;
import de.jcup.egradle.codeassist.dsl.gradle.LanguageElementMetaData;
import de.jcup.egradle.core.api.LinkToTypeConverter;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.gradleeditor.codeassist.GradleContentAssistProcessor;
import de.jcup.egradle.eclipse.gradleeditor.control.BrowserEGradleLinkListener;
import de.jcup.egradle.eclipse.gradleeditor.control.SimpleBrowserInformationControl;

public class GradleTextHover implements ITextHover, ITextHoverExtension {

	private HTMLDescriptionBuilder builder;
	private GradleSourceViewerConfiguration gradleSourceViewerConfiguration;
	private ISourceViewer sourceViewer;
	private String contentType;
	private GradleTextHoverControlCreator creator;
	private LinkToTypeConverter linkToTypeConverter;
	private String fgColor;
	private String bgColor;

	public GradleTextHover(GradleSourceViewerConfiguration gradleSourceViewerConfiguration, ISourceViewer sourceViewer,
			String contentType) {
		this.gradleSourceViewerConfiguration = gradleSourceViewerConfiguration;
		this.sourceViewer = sourceViewer;
		this.contentType = contentType;
		this.linkToTypeConverter = new LinkToTypeConverter();
		builder = new HTMLDescriptionBuilder();
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		HoverData data = getLanguageElementAt(offset);
		if (data != null) {
			return data;
		}
		return new Region(offset, 0);
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
		if (hoverRegion instanceof HoverData) {
			data = (HoverData) hoverRegion;
		}
		if (data == null) {
			data = getLanguageElementAt(hoverRegion.getOffset());
		}
		if (data == null) {
			return null;
		}
		if (data.item == null) {
			return null;
		}
		if (data.result == null) {
			return null;
		}
		LanguageElement element = data.result.getElement();
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
		return builder.buildHoverHTML(fgColor, bgColor ,data, element);
	}

	/**
	 * Get language at given offset
	 * @param offset
	 * @return language element or <code>null</code>
	 */
	protected HoverData getLanguageElementAt(int offset) {
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
		if (model == null) {
			return null;
		}
		Item item = model.getItemAt(offset);
		if (item == null) {
			return null;
		}
		HoverData data = new HoverData();
		data.offset = offset;
		data.item = item;
		String name = item.getName();
		if (name != null) {
			data.length = name.length();
		}
		data.offset = item.getOffset();
	
		GradleFileType fileType = gradleSourceViewerConfiguration.getFileType();
		GradleLanguageElementEstimater estimator = gprocessor.getEstimator();
		EstimationResult result = estimator.estimate(item, fileType);
		data.result = result;
		return data;
	}

	

	private class HoverData implements IRegion, LanguageElementMetaData {
		private EstimationResult result;
		private Item item;
		private int length;
		private int offset;

		@Override
		public int getLength() {
			return length;
		}

		@Override
		public int getOffset() {
			return offset;
		}

		@Override
		public boolean isTypeFromExtensionConfigurationPoint() {
			if (result==null){
				return false;
			}
			return result.isTypeFromExtensionConfigurationPoint();
		}

		@Override
		public String getExtensionName() {
			if (result==null){
				return null;
			}
			return result.getExtensionName();
		}
	}

	private class GradleTextHoverControlCreator implements IInformationControlCreator {

		@Override
		public IInformationControl createInformationControl(Shell parent) {
			if (SimpleBrowserInformationControl.isAvailableFor(parent)) {
				SimpleBrowserInformationControl control = new SimpleBrowserInformationControl(parent);
				control.setBrowserEGradleLinkListener(new BrowserEGradleLinkListener() {

					@Override
					public void onEGradleHyperlinkClicked(IInformationControl control, String target) {
						String convertedName = linkToTypeConverter.convertLink(target);
						if (convertedName == null) {
							showFallBackInfo(target);
							return;
						}
						CodeCompletionRegistry registry = Activator.getDefault().getCodeCompletionRegistry();
						if (registry == null) {
							showFallBackInfo("Code completion registry not available!");
							return;
						}
						GradleDSLTypeProvider provider = registry.getService(GradleDSLTypeProvider.class);
						if (provider == null) {
							showFallBackInfo("Type provider not available!");
							return;
						}

						Type type = provider.getType(convertedName);
						if (type == null) {
							showFallBackInfo(convertedName);
							return;
						}
						control.setInformation(builder.buildHoverHTML(fgColor, bgColor, null, type));

					}

					private void showFallBackInfo(String target) {
						control.setInformation("<html><bod>New location should be:" + target + "</body></html>");
					}

					@Override
					public boolean isAcceptingHyperlink(String target) {
						return linkToTypeConverter.isLinkSchemaConvertable(target);
					}
				});
				return control;
			} else {
				return new DefaultInformationControl(parent, true);
			}
		}
	}

}