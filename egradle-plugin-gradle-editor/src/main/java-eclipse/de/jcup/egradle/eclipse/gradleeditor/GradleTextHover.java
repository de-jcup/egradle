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
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Parameter;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.XMLProperty;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater.EstimationResult;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.eclipse.gradleeditor.codeassist.GradleContentAssistProcessor;
import de.jcup.egradle.eclipse.gradleeditor.control.BrowserLinkListener;
import de.jcup.egradle.eclipse.gradleeditor.control.SimpleBrowserInformationControl;

public class GradleTextHover implements ITextHover, ITextHoverExtension {

	private GradleSourceViewerConfiguration gradleSourceViewerConfiguration;
	private ISourceViewer sourceViewer;
	private String contentType;
	private GradleTextHoverControlCreator creator;

	public GradleTextHover(GradleSourceViewerConfiguration gradleSourceViewerConfiguration, ISourceViewer sourceViewer,
			String contentType) {
		this.gradleSourceViewerConfiguration = gradleSourceViewerConfiguration;
		this.sourceViewer = sourceViewer;
		this.contentType = contentType;
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
			description.append("Mehod:" + method.getName());
			description.append("<br>");
			description.append(method.getDescription());
			if (data.item.isClosureBlock()) {
				List<Parameter> params = method.getParameters();
				for (Parameter param : params) {
					Type type = param.getType();
					/*
					 * FIXME ATR, 30.01.2017: problematic - at this point often
					 * got only "closure" as type not the wanted one. this must
					 * be made available by xml data - or by inspecting the
					 * javadoc of method
					 */
					if (type != null) {
						description.append("closure block type:" + type.getName());
						description.append("<br>");
						description.append(type.getDescription());
						break;
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
			if (data.result.isTypeFromExtensionConfigurationPoint()) {
				description.append("Extension name:" + data.result.getExtensionName());
				description.append("<br>");
			}
			description.append("Type:" + element.getName());
			description.append("<br>");
			description.append(element.getDescription());
		}
		return "<html><body>" + description + "</body></html>";
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
				control.setBrowserLinkListener(new BrowserLinkListener() {

					@Override
					public void onHyperlinkClicked(IInformationControl control, String target) {
						control.setInformation("<html><bod>New location should be:" + target + "</body></html>");
					}

					@Override
					public boolean isAcceptingHyperlink(String target) {
						return target.startsWith("type://");
					}
				});
				return control;
			} else {
				return new DefaultInformationControl(parent, true);
			}
		}
	}

}