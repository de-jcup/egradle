package de.jcup.egradle.eclipse.gradleeditor;

import de.jcup.egradle.codeassist.CodeCompletionRegistry;
import de.jcup.egradle.codeassist.dsl.HTMLDescriptionBuilder;
import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.MethodUtils;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLTypeProvider;
import de.jcup.egradle.core.api.LinkToTypeConverter;
import de.jcup.egradle.core.api.LinkToTypeConverter.LinkData;
import de.jcup.egradle.eclipse.gradleeditor.control.BrowserEGradleLinkListener;
import de.jcup.egradle.eclipse.gradleeditor.control.SimpleBrowserInformationControl;

public class DefaultEGradleLinkListener implements BrowserEGradleLinkListener {

	private LinkToTypeConverter linkToTypeConverter;
	private String fgColor;
	private String bgColor;
	private String commentColor;
	private HTMLDescriptionBuilder builder;

	public DefaultEGradleLinkListener(String fgColor, String bgColor, String commentColor,
			HTMLDescriptionBuilder builder) {
		this.linkToTypeConverter = new LinkToTypeConverter();
		this.fgColor = fgColor;
		this.bgColor = bgColor;
		this.commentColor = commentColor;
		this.builder = builder;
	}

	@Override
	public void onEGradleHyperlinkClicked(SimpleBrowserInformationControl control, String target) {
		/* validate parameters */
		if (control==null){
			return;
		}
		if (target==null){
			return;
		}
		CodeCompletionRegistry registry = Activator.getDefault().getCodeCompletionRegistry();
		if (registry == null) {
			return;
		}
		GradleDSLTypeProvider provider = registry.getService(GradleDSLTypeProvider.class);
		if (provider == null) {
			return;
		}
		
		/* fetch data + validate */
		LinkData data = linkToTypeConverter.convertLink(target);
		if (data == null) {
			return;
		}
		String convertedName = data.getMainName();
		if (convertedName == null) {
			return;
		}
		
		Type type = provider.getType(convertedName);
		if (type == null) {
			return;
		}
		/* execute */
		LanguageElement elementTarget = null;
		
		if (elementTarget==null){
			elementTarget = scanForPropertyElement(type,data);
		}
		if (elementTarget==null){
			elementTarget = scanForMethodElement(type,data);
		}
		if (elementTarget==null){
			elementTarget=type;
		}
		
		control.setInformation(builder.buildHTMLDescription(fgColor, bgColor, commentColor, null, elementTarget, null));

	}

	@Override
	public boolean isAcceptingHyperlink(String target) {
		return linkToTypeConverter.isLinkSchemaConvertable(target);
	}
	
	private LanguageElement scanForPropertyElement(Type type, LinkData data) {
		String[] dataParameters = data.getParameterTypes();
		String propertyName = data.getSubName();
		
		if (propertyName == null) {
			return null;
		}
		if (dataParameters != null && dataParameters.length > 0) {
			/* properties have no params */
			return null;
		}
		/* scan for property */
		for (Property property: type.getProperties()) {
			if (property.getName().equals(propertyName)){
				return property;
			}
		}
		return null;
	}

	private LanguageElement scanForMethodElement(Type type, LinkData data) {
		String[] dataParameters = data.getParameterTypes();
		String dataMethodName = data.getSubName();
		
		if (dataMethodName == null) {
			return null;
		}
		if (dataParameters == null) {
			return null;
		}
		/* scan for method */
		for (Method methodOfType : type.getMethods()) {
			if (MethodUtils.hasSignature(methodOfType, dataMethodName, dataParameters,false)) {
				return methodOfType;
			}
		}
		/* second way - with shortening...*/
		for (Method methodOfType : type.getMethods()) {
			if (MethodUtils.hasSignature(methodOfType, dataMethodName, dataParameters,true)) {
				return methodOfType;
			}
		}
		return null;
	}

}
