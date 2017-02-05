package de.jcup.egradle.codeassist.dsl;

import de.jcup.egradle.codeassist.dsl.gradle.LanguageElementMetaData;

public class HTMLDescriptionBuilder {
	
	public String buildHTMLDescription(String fgColor, String bgColor, LanguageElementMetaData data, LanguageElement element) {
		StringBuilder descSb = new StringBuilder();
		if (element instanceof Method) {
			Method method = (Method) element;
			descSb.append("<h4>Mehod:");
			String signature = MethodUtils.createSignature(method);
			descSb.append(signature);
			descSb.append("</h4>");
			descSb.append(method.getDescription());
			appendLinkToGradleOriginDoc(method, descSb);
		} else if (element instanceof Property) {
			Property property = (Property) element;
//			Type propertyType = property.getType();
			descSb.append("<h4>Property:");
			descSb.append(property.getName());
			descSb.append("</h4>");
			descSb.append(property.getDescription());
			appendLinkToGradleOriginDoc(property, descSb);
//			descSb.append("<br>");
//			if (propertyType == null) {
//				descSb.append("Type:");
//				if (property instanceof XMLProperty) {
//					XMLProperty xmlProp = (XMLProperty) property;
//					descSb.append(" (raw) ");
//					descSb.append(xmlProp.getTypeAsString());
//				} else {
//					descSb.append("unknown");
//				}
//			} else {
//				appendTypeDescription(null, propertyType, descSb);
//			}

		} else if (element instanceof Type) {
			Type type = (Type) element;
			appendTypeDescription(data, type, descSb);
		}else{
			
		}
		String title = null;
		if (element!=null){
			title=element.getName();
		}else{
			title="element is null";
		}
		String html =  createHTMLBody(fgColor, bgColor, title, descSb);
		return html;
	}

	private String createHTMLBody(String fgColor, String bgColor,String title, StringBuilder descSb) {
		
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
		style.append("pre {background-color: black;color: #999999;border-collapse:separate;border:solid #999999 2px; border-radius:6px; -moz-border-radius:6px;}");
		style.append("a {color: #229922;}");
		style.append(".param {color: #229922;}");
		style.append(".return {color: #229922;}");
		style.append(".originLinkURL {font-size:x-small;color: #999999;font-family:'Courier New', Courier, monospace}");
		
		return "<html><head><title>"+title+"</title><style>"+style.toString()+"</style></head><body>" + descSb + "</body></html>";
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
				description.append("<h4>Extension:" + data.getExtensionName());
				description.append("</h4>");
			}
		}
		description.append("<h4>Type:");
		description.append(type.getName());
		description.append("</h4>");
		
		description.append(type.getDescription());
		appendLinkToGradleOriginDoc(type, description);
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
