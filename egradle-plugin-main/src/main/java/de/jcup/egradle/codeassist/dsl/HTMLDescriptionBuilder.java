package de.jcup.egradle.codeassist.dsl;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.jcup.egradle.codeassist.dsl.gradle.LanguageElementMetaData;

public class HTMLDescriptionBuilder {
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
			descSb.append("<div class='fullName'>");
			appendLinkToParentType(element, descSb,true);
			String signature = MethodUtils.createSignature(method);
			descSb.append(signature);
			descSb.append("</div>");
			if (method.getDelegationTarget()!=null){
				descSb.append("Delegates to:");
				appendLinkToType(descSb, false, method.getDelegationTarget(), null);
				descSb.append("<br");
			}
			descSb.append(method.getDescription());
			appendLinkToGradleOriginDoc(method, descSb);
		} else if (element instanceof Property) {
			Property property = (Property) element;
			descSb.append("<div class='fullName'>");
			appendLinkToParentType(element, descSb,true);
			descSb.append(property.getName());
			descSb.append("</div>");
			descSb.append(property.getDescription());
			appendLinkToGradleOriginDoc(property, descSb);

		} else if (element instanceof Type) {
			Type type = (Type) element;
			appendTypeDescription(data, type, descSb);
		}else{
			/* do nothing*/
		}
		String title = null;
		if (element!=null){
			title=element.getName();
		}else{
			title="element is null";
		}
		String html =  createHTMLBody(fgColor, bgColor, commentColor, title, descSb);
		return html;
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

	private String createHTMLBody(String fgColor, String bgColor,String commentColor, String title, StringBuilder descSb) {
		
		String style = createStyles(fgColor, bgColor, commentColor);
		
		return "<html><head><title>"+title+"</title><style>"+style+"</style></head><body>" + descSb + "</body></html>";
	}

	private String createStyles(String fgColor, String bgColor, String commentColor) {
		StringBuilder style = new StringBuilder();
		style.append("body{");
		if (fgColor!=null ){
			style.append("color:").append(fgColor);
			style.append(";");
		}
		if (bgColor!=null ){
			style.append("background-color:").append(bgColor);
			style.append(";");
		}
		style.append("}");
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
		description.append("<a href='#appendix'>Go to appendix</a><br>");
		
		description.append(type.getDescription());
		
		appendLinkToGradleOriginDoc(type, description);

		appendAppendix(type, description);
	}

	private void appendAppendix(Type type, StringBuilder description) {
		description.append("<h4 id='appendix'>Appendix:</h4>");
		description.append("Contains list of <a href='#appendix_methods'>methods</a> and <a href='#appendix_properties'>properties</a> of "+type.getName());
		appendAppendixMethods(type, description);
		appendAppendixProperties(type, description);
	}

	private void appendAppendixMethods(Type type, StringBuilder description) {
		description.append("<h5 id='appendix_methods'>Methods:</h5>");
		Set<Method> methods = type.getMethods();
		SortedSet<String> sortedLinkReferences =new TreeSet<>();
		for (Method m: methods){
			String methodSignature = MethodUtils.createSignature(m);
			Type declaringType = m.getParent();
			StringBuilder referenceLink = new StringBuilder();
			referenceLink.append("<a href='type://").append(declaringType.getName()).append("#").append(methodSignature).append("'>");
			referenceLink.append(methodSignature);
			referenceLink.append("</a>");
			sortedLinkReferences.add(referenceLink.toString());
			
		}
		description.append("<ul>");
		for (String referenceLink: sortedLinkReferences){
			description.append("<li>");
			description.append(referenceLink);
		}
		description.append("</ul>");
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
			referenceLink.append("<a href='type://").append(declaringType.getName()).append("#").append(propertySignature).append("'>");
			referenceLink.append(propertySignature);
			referenceLink.append("</a>");
			sortedLinkReferences.add(referenceLink.toString());
			
		}
		description.append("<ul>");
		for (String referenceLink: sortedLinkReferences){
			description.append("<li>");
			description.append(referenceLink);
			description.append("</li>");
		}
		description.append("</ul>");
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
		if (! type.isDocumented()){
			/* not documented  - so no link available...*/
			return;
		}
		/* FIXME ATR, 02.02.2017: make the origin doc url parts configurable in prefs or in sdk! And write a dedicated class to create link*/
		String linkToGradleOriginDoc =
		"https://docs.gradle.org/"+type.getVersion()+"/dsl/"+type.getName()+".html";
		if (child!=null){
			String appendix = "#"+type.getName()+":"+child.getName();
			linkToGradleOriginDoc+=appendix;
		}
		descSb.append("<table style='background-color: black;color: #999999;border-collapse:separate;border:solid #999999 2px; border-radius:6px; -moz-border-radius:6px;'><tr><td><a href='");
		descSb.append(linkToGradleOriginDoc);
		descSb.append("''>Link to online gradle documentation</a>");
		descSb.append("</a><br><div class='originLinkURL'>[");
		descSb.append(linkToGradleOriginDoc);
		descSb.append("]</div></td></tr></table><br>");
	}
	
	
}
