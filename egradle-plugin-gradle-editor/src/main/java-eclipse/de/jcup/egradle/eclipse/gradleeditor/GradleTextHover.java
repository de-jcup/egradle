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
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater.EstimationResult;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.ItemType;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.eclipse.gradleeditor.codeassist.GradleContentAssistProcessor;
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
		if (hoverRegion instanceof HoverData){
			data = (HoverData)hoverRegion;
		}
		if (data==null){
			data = getLanguageElementAt(hoverRegion.getOffset());
		}
		if (data == null) {
			return null;
		}
		if (data.item == null) {
			return null;
		}
		if (data.element == null) {
			return null;
		}
		String description = null;
		if (data.element instanceof Method) {
			Method method = (Method) data.element;
			Type returnType = method.getReturnType();
			if (returnType == null || data.item.getItemType() == ItemType.CLOSURE) {
				/*
				 * when VOID(null) or it is a closure type use description
				 * instead of type
				 */
				description = method.getDescription();
			} else {
				/* use method type as description... */
				description = returnType.getDescription();
			}
		} else if (data.element instanceof Property) {
			Property property = (Property) data.element;
			Type returnType = property.getType();
			if (returnType == null || data.item.getItemType() == ItemType.CLOSURE) {
				/*
				 * when VOID(null) or it is a closure type use use property
				 * description instead of type
				 */
				description = property.getDescription();
			} else {
				/* use method type as description... */
				description = returnType.getDescription();
			}
		} else {
			description = data.element.getDescription();
		}
		return "<html><body><b>" + data.element.getName() + "</b><br><br>" + description + "</body></html>";
	}

	private class HoverData implements IRegion{
		private LanguageElement element;
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

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		HoverData data= getLanguageElementAt(offset);
		if (data!=null){
			return data;
		}
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
		if (model == null) {
			return null;
		}
		Item item = model.getItemAt(offset);
		if (item == null) {
			return null;
		}
		HoverData data = new HoverData();
		data.offset=offset;
		data.item = item;
		String name = item.getName();
		if (name!=null){
			data.length=name.length();
		}
		data.offset=item.getOffset();

		GradleFileType fileType = gradleSourceViewerConfiguration.getFileType();
		GradleLanguageElementEstimater estimator = gprocessor.getEstimator();
		EstimationResult result = estimator.estimate(item, fileType);
		LanguageElement element = null;
		if (result != null) {
			element = result.getElement();
		}
		data.element = element;
		return data;
	}

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		if (creator == null) {
			creator = new GradleTextHoverControlCreator();
		}
		return creator;
	}

	private class GradleTextHoverControlCreator implements IInformationControlCreator {

		@Override
		public IInformationControl createInformationControl(Shell parent) {
			if (SimpleBrowserInformationControl.isAvailableFor(parent)) {
				SimpleBrowserInformationControl control = new SimpleBrowserInformationControl(parent);
				control.add(new LocationAdapter() {
					@Override
					public void changed(LocationEvent event) {
						System.out.println(GradleTextHover.class.getSimpleName() + "-locationevent:" + event);
					}
				});
				return control;
			} else {
				return new DefaultInformationControl(parent, true);
			}
		}
	}

}