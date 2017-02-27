package de.jcup.egradle.codeassist.dsl;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.gradle.LanguageElementMetaData;

public class HTMLDescriptionBuilder {

	private DescriptionFinder descriptionFinder = new DescriptionFinder();


	/**
	 * Builds HTML description for given language element
	 * 
	 * @param fgColor
	 * @param bgColor
	 * @param commentColor 
	 * @param data
	 * @param element
	 * @param prefix can be <code>null</code>, otherwise its rendered before first body element
	 * @return description, never <code>null</code>
	 */
	public String buildHTMLDescription(String fgColor, String bgColor, String commentColor, LanguageElementMetaData data, LanguageElement element, String prefix) {
		StringBuilder descSb = new StringBuilder();
		if (prefix!=null){
			descSb.append(prefix);
		}
		if (element instanceof Method) {
			Method method = (Method) element;
			appendMethodDescription(element, descSb, method);
		} else if (element instanceof Property) {
			Property property = (Property) element;
			appendPropertyDescription(element, descSb, property);
		} else if (element instanceof Type) {
			Type type = (Type) element;
			appendTypeDescription(data, type, descSb);
		}else{
			/* do nothing*/
		}
		
		appendAppendix(element, descSb);
		
		String title = null;
		if (element!=null){
			title=element.getName();
		}else{
			title="element is null";
		}
		String html =  createHTMLBody(fgColor, bgColor, commentColor, title, descSb);
		return html;
	}

	private void appendAppendix(LanguageElement element, StringBuilder description) {
		description.append("<h4 id='appendix'>Appendix:</h4>");
		appendLinkToGradleOriginDoc(element, description);
		if (! (element instanceof Type)){
			return;
		}
		Type type = (Type) element;
		description.append("Now follows a list of <a href='#appendix_methods'>methods</a> and <a href='#appendix_properties'>properties</a> of "+type.getName());
		appendAppendixMethods(type, description);
		appendAppendixProperties(type, description);
	}

	private void appendAppendixLink(StringBuilder description) {
		description.append("<a href='#appendix'>Go to appendix</a><br>");
	}

	private void appendAppendixMethods(Type type, StringBuilder description) {
		description.append("<h5 id='appendix_methods'>Methods:</h5>");
		Set<Method> methods = type.getMethods();
		SortedSet<String> sortedLinkReferences =new TreeSet<>();
		for (Method m: methods){
			String methodSignature = MethodUtils.createSignature(m);
			Type declaringType = m.getParent();
			StringBuilder referenceLink = new StringBuilder();
			referenceLink.append("\n<a href='type://").append(declaringType.getName()).append("#").append(methodSignature).append("'>");
			referenceLink.append(methodSignature);
			referenceLink.append("</a>");
			sortedLinkReferences.add(referenceLink.toString());
			
		}
		appendList(description, sortedLinkReferences);
	}
	private void appendAppendixProperties(Type type, StringBuilder description) {
		SortedSet<String> sortedLinkReferences;
		description.append("<h5 id='appendix_properties'>Properties:</h5>");
		Set<Property> properties= type.getProperties();
		sortedLinkReferences =new TreeSet<>();
		
		for (Property p: properties){
			String propertySignature = p.getName();
			StringBuilder referenceLink = new StringBuilder();
			Type declaringType = p.getParent();
			referenceLink.append("\n<a href='type://").append(declaringType.getName()).append("#").append(propertySignature).append("'>");
			referenceLink.append(propertySignature);
			referenceLink.append("</a>");
			sortedLinkReferences.add(referenceLink.toString());
			
		}
		appendList(description, sortedLinkReferences);
	}

	private void appendList(StringBuilder description, SortedSet<String> sortedLinkReferences) {
		description.append("\n<ul>");
		for (String referenceLink: sortedLinkReferences){
			description.append("\n<li>");
			description.append(referenceLink);
			description.append("</li>");
		}
		description.append("\n</ul>");
	}

	private void appendLinkToGradleOriginDoc(LanguageElement element, StringBuilder descSb) {
		if (element==null){
			return;
		}
		if (descSb==null){
			return;
		}
		
		Type type=null;
		TypeChild child=null;
		if (element instanceof Type){
			type=(Type)element;
		}else if (element instanceof TypeChild){
			child = (TypeChild) element;
			type=child.getParent();
		}
		if (type==null){
			return;
		}
		String typeName = type.getName();
		if (typeName==null){
			return;
		}
		
		String linkToGradleOriginDSLDoc = null;
		if (type.isDocumented()){
			linkToGradleOriginDSLDoc=
					"https://docs.gradle.org/"+type.getVersion()+"/dsl/"+typeName+".html";
			if (child!=null){
				String appendix = "#"+typeName+":"+child.getName();
				linkToGradleOriginDSLDoc+=appendix;
			}

		}
		// https://docs.gradle.org/3.0/javadoc/org/gradle/api/Project.html
		// https://docs.gradle.org/3.0/javadoc/org/gradle/api/Project.html#file(java.lang.Object, org.gradle.api.PathValidation)
		String linkToGradleOriginAPIDoc = 
					"https://docs.gradle.org/"+type.getVersion()+"/javadoc/"+typeName.replaceAll("\\.", "/")+".html";
			if (child!=null){
				String childTarget = null;
				if (child instanceof Method){
					Method method = (Method) child;
					childTarget=MethodUtils.createSignature(method, true,false);
				}else{
					childTarget=child.getName();
				}
				// https://docs.gradle.org/3.3/javadoc/org/gradle/api/Project.html#absoluteProjectPath(java.lang.String)
				String appendix = "#"+childTarget;
				linkToGradleOriginAPIDoc+=appendix;
			}
		
		descSb.append("\n<table style='background-color: black;color: #999999;border-collapse:separate;border:solid #999999 2px; border-radius:6px; -moz-border-radius:6px;'>");
		if (linkToGradleOriginDSLDoc!=null){
			descSb.append("\n<tr><td><a href='");
			descSb.append(linkToGradleOriginDSLDoc);
			descSb.append("''>Link to online gradle DSL documentation</a>");
			descSb.append("</a><br><div class='originLinkURL'>[");
			descSb.append(linkToGradleOriginDSLDoc);
			descSb.append("]</div>");
			descSb.append("</td></tr>\n");
		}
		if (linkToGradleOriginAPIDoc!=null){
			descSb.append("\n<tr><td><a href='");
			descSb.append(linkToGradleOriginAPIDoc);
			descSb.append("''>Link to online gradle API documentation</a>");
			descSb.append("</a><br><div class='originLinkURL'>[");
			descSb.append(linkToGradleOriginAPIDoc);
			descSb.append("]</div>");
			descSb.append("</td></tr>\n");
		}
		descSb.append("\n</table><br>");
	}

	private void appendLinkToParentType(LanguageElement element, StringBuilder descSb, boolean withEndingDot) {
		if (element instanceof TypeChild){
			TypeChild child = (TypeChild) element;
			Type type = child.getParent();
			appendLinkToType(descSb, withEndingDot, type,null);
		}
	}

	private void appendLinkToType(StringBuilder descSb, boolean withEndingDot, Type type, String linkPostfix) {
		if (type!=null){
			descSb.append("<a href='type://");
			descSb.append(type.getName());
			if (linkPostfix!=null){
				descSb.append(linkPostfix);
			}
			descSb.append("'>");
			descSb.append(type.getName());
			descSb.append("</a>");
			if (withEndingDot){
				descSb.append('.');
			}
		}
	}

	private void appendMethodDescription(LanguageElement element, StringBuilder descSb, Method method) {
		descSb.append("<div class='fullName'>");
		appendLinkToParentType(element, descSb,true);
		String signature = MethodUtils.createSignature(method);
		descSb.append(signature);
		descSb.append("</div>");
		appendAppendixLink(descSb);
		if (method.getDelegationTarget()!=null){
			descSb.append("<br>Delegates to:");
			appendLinkToType(descSb, false, method.getDelegationTarget(), null);
			descSb.append("<br");
		}
		appendDescriptionPart(method,descSb);
	}
	
	private void appendPropertyDescription(LanguageElement element, StringBuilder descSb, Property property) {
		descSb.append("<div class='fullName'>");
		appendLinkToParentType(element, descSb,true);
		descSb.append(property.getName());
		descSb.append("</div>");
		appendAppendixLink(descSb);
		appendDescriptionPart(property,descSb);
	}

	private void appendTypeDescription(LanguageElementMetaData data, Type type, StringBuilder description) {
		if (type==null){
			return;
		}
		if (description==null){
			return;
		}
		if (data != null ) {
			if (data.isTypeFromExtensionConfigurationPoint()) {
				description.append("<div class='fullName'>Extension:" + data.getExtensionName());
				description.append("</div>");
			}
		}
		description.append("<div class='fullName'>");
		description.append(type.getName());
		description.append("</div>");
		appendAppendixLink(description);
		
		appendDescriptionPart(type,description);
		
	}

	private void appendDescriptionPart(LanguageElement element,StringBuilder descSb){
		if (element==null){
			return;
		}
		String description = element.getDescription();
		if (StringUtils.isEmpty(description)){
			descSb.append("No description available.");
			return;
		}
		if (description.indexOf("@inheritDoc")!=-1){
			String inheritedDescription = descriptionFinder.findDescription(element);
			if (StringUtils.isNotBlank(inheritedDescription)){
				description=inheritedDescription;
			}
		}
		descSb.append(description);
	}

	private String createHTMLBody(String fgColor, String bgColor,String commentColor, String title, StringBuilder descSb) {
		
		String style = createStyles(fgColor, bgColor, commentColor);
		
		return "<html><head><title>"+title+"</title><style>"+style+"</style></head><body>" + descSb + "</body></html>";
	}
	

	private String createStyles(String fgColor, String bgColor, String commentColor) {
		StringBuilder style = new StringBuilder();
		style.append("\nbody{");
		if (fgColor!=null ){
			style.append("color:").append(fgColor);
			style.append(";");
		}
		if (bgColor!=null ){
			style.append("background-color:").append(bgColor);
			style.append(";");
		}
		style.append("}\n");
		style.append("pre {background-color: black;color: #999999;border-collapse:separate;border:solid #999999 2px; border-radius:6px; -moz-border-radius:6px;}\n");
		style.append("table {background-color: black;color: #999999;border-collapse:separate;border:solid #999999 2px; border-radius:6px; -moz-border-radius:6px;}\n");
		style.append("th {background-color: black;color: #229922;border:solid #999999 2px; }\n");
		style.append("tr {background-color: black;color: #999999;border:solid #999999 2px; }\n");
		style.append("a {color: #229922;}\n");
		style.append(".fullName{font-weight: bold;white-space: nowrap;font-family:'Courier New', Courier, monospace}\n");
		style.append(".param {color: #229922;}\n");
		style.append(".return {color: #229922;}\n");
		style.append(".value {color: #999999;}\n");
		style.append(".warnSmall {color: #ff0000;font-size:small}\n");
		style.append(".comment {color: "+commentColor+";}\n");
		style.append(".originLinkURL {font-size:x-small;color: #999999;font-family:'Courier New', Courier, monospace}\n");
		
		return style.toString();
	}

}