package de.jcup.egradle.eclipse.gradleeditor;

import java.util.List;

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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.codeassist.CodeCompletionRegistry;
import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Parameter;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.XMLProperty;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLTypeProvider;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater.EstimationResult;
import de.jcup.egradle.core.api.LinkToTypeConverter;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.gradleeditor.codeassist.GradleContentAssistProcessor;
import de.jcup.egradle.eclipse.gradleeditor.control.BrowserEGradleLinkListener;
import de.jcup.egradle.eclipse.gradleeditor.control.SimpleBrowserInformationControl;
import de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorPreferenceConstants;
import de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorPreferences;
import de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorSyntaxColorPreferenceConstants;

public class GradleTextHover implements ITextHover, ITextHoverExtension {

	private GradleSourceViewerConfiguration gradleSourceViewerConfiguration;
	private ISourceViewer sourceViewer;
	private String contentType;
	private GradleTextHoverControlCreator creator;
	private LinkToTypeConverter linkToTypeConverter;

	public GradleTextHover(GradleSourceViewerConfiguration gradleSourceViewerConfiguration, ISourceViewer sourceViewer,
			String contentType) {
		this.gradleSourceViewerConfiguration = gradleSourceViewerConfiguration;
		this.sourceViewer = sourceViewer;
		this.contentType = contentType;
		this.linkToTypeConverter = new LinkToTypeConverter();
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
		return buildDescription(data, element);
	}

	/*
	 * Given parameters are checked for null before
	 */
	private String buildDescription(HoverData data, LanguageElement element) {
		StringBuilder description = new StringBuilder();
		if (element instanceof Method) {
			Method method = (Method) element;
			description.append("<h3>Mehod:" + method.getName());
			description.append("</h3>");
			description.append(method.getDescription());
			if (data != null) {
				if (data.item.isClosureBlock()) {
					List<Parameter> params = method.getParameters();
					for (Parameter param : params) {
						Type type = param.getType();
						/*
						 * FIXME ATR, 30.01.2017: problematic - at this point
						 * often got only "closure" as type not the wanted one.
						 * this must be made available by xml data - or by
						 * inspecting the javadoc of method
						 */
						if (type != null) {
							description.append("closure block type:" + type.getName());
							description.append("<br>");
							description.append(type.getDescription());
							break;
						}
					}
				}
			}
		} else if (element instanceof Property) {
			Property property = (Property) element;
			Type propertyType = property.getType();
			description.append("<b>Property</b> name:" + property.getName());
			description.append("<br>");
			description.append(property.getDescription());
			description.append("<br>");
			description.append("Type:");
			if (propertyType == null) {
				if (property instanceof XMLProperty) {
					XMLProperty xmlProp = (XMLProperty) property;
					description.append(" (raw) ");
					description.append(xmlProp.getTypeAsString());
				} else {
					description.append("null");
				}
			} else {
				description.append(propertyType.getName());
				description.append("<br>");
				description.append(propertyType.getDescription());
			}

		} else if (element instanceof Type) {
			if (data != null) {
				if (data.result.isTypeFromExtensionConfigurationPoint()) {
					description.append("Extension name:" + data.result.getExtensionName());
					description.append("<br>");
				}
			}
			description.append("<h3>Type:" + element.getName());
			description.append("</h3>");
			
			/* FIXME ATR, 02.02.2017: make the origin doc url parts configurable in prefs or in sdk!*/
			String linkToGradleOriginDoc =
			"https://docs.gradle.org/3.0/dsl/"+element.getName()+".html";
			
			description.append("<br><table style='background-color: black;color: #999999;border-collapse:separate;border:solid #999999 2px; border-radius:6px; -moz-border-radius:6px;'><tr><td><a href='");
			description.append(linkToGradleOriginDoc);
			description.append("''>Link to origin gradle doc (internet connection necessary)");
			description.append("</a><br><i>(URL=");
			description.append(linkToGradleOriginDoc);
			description.append(")</i></td></tr></table><br>");
			description.append(element.getDescription());
		}
		StringBuilder style = new StringBuilder();
		if (sourceViewer!=null){
			StyledText textWidget = sourceViewer.getTextWidget();
			if (textWidget!=null){
				/* TODO ATR, 03.02.2017: there should be an easier approach to get editors back and foreground, without syncexec */
				EGradleUtil.getSafeDisplay().syncExec(new Runnable() {
					
					@Override
					public void run() {
						String bgColor = convertToHexColor(textWidget.getBackground());
						String fgColor = convertToHexColor(textWidget.getForeground());
						if (bgColor!=null && fgColor!=null){
							style.append("body{");
							style.append("color:").append(fgColor);
							style.append(";background-color:").append(bgColor);
							style.append("}");
						}
						
					}
				});
			}
			
		}
		style.append("pre {background-color: black;color: #999999;border-collapse:separate;border:solid #999999 2px; border-radius:6px; -moz-border-radius:6px;}");
		style.append("a {color: #229922;}");
		
		return "<html><head><style>"+style.toString()+"</style></head><body>" + description + "</body></html>";
	}
	
	private String convertToHexColor(Color color){
		if (color ==null){
			return null;
		}
		RGB rgb = color.getRGB();
		String hex = String.format("#%02x%02x%02x", rgb.red, rgb.green,rgb.blue);
		return hex;
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

	private class HoverData implements IRegion {
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
						control.setInformation(buildDescription(null, type));

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