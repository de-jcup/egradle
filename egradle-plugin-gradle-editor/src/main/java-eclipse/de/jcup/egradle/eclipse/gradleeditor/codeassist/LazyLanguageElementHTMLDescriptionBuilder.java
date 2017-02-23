package de.jcup.egradle.eclipse.gradleeditor.codeassist;

import de.jcup.egradle.codeassist.dsl.HTMLDescriptionBuilder;
import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.gradle.LanguageElementMetaData;

public class LazyLanguageElementHTMLDescriptionBuilder {

	private LanguageElement element;
	private HTMLDescriptionBuilder builder;
	private String fgColor;
	private String bgColor;
	private LanguageElementMetaData metaData;
	private String commentColorWeb;
	
	public LazyLanguageElementHTMLDescriptionBuilder(String fgColor, String bgColor, String commentColor, LanguageElement element, LanguageElementMetaData metaData,  HTMLDescriptionBuilder builder){
		this.element=element;
		this.metaData=metaData;
		this.builder=builder;
		this.fgColor = fgColor;
		this.bgColor = bgColor;
		this.commentColorWeb=commentColor;
	}
	public String getFgColor() {
		return fgColor;
	}
	
	public HTMLDescriptionBuilder getBuilder() {
		return builder;
	}
	
	public String getBgColor() {
		return bgColor;
	}

	public String getCommentColor() {
		return commentColorWeb;
	}
	
	public String createHTML(){
		if (element==null){
			return "";
		}
		if (builder==null){
			return "";
		}
		return builder.buildHTMLDescription(fgColor, bgColor,commentColorWeb, metaData, element,null);
	}
}
