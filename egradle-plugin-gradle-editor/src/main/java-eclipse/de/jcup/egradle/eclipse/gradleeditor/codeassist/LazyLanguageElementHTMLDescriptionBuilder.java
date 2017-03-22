/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
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
