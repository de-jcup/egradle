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
	
	public LazyLanguageElementHTMLDescriptionBuilder(String fgColor, String bgColor, LanguageElement element, LanguageElementMetaData metaData,  HTMLDescriptionBuilder builder){
		this.element=element;
		this.metaData=metaData;
		this.builder=builder;
		this.fgColor = fgColor;
		this.bgColor = bgColor;
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
	
	public String createHTML(){
		if (element==null){
			return "";
		}
		if (builder==null){
			return "";
		}
		return builder.buildHTMLDescription(fgColor, bgColor, metaData, element);
	}
}
