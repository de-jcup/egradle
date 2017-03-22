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
 package de.jcup.egradle.sdk.builder.action.javadoc;

import static de.jcup.egradle.codeassist.dsl.DSLConstants.*;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.DSLConstants;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.ModifiableMethod;
import de.jcup.egradle.codeassist.dsl.ModifiableProperty;
import de.jcup.egradle.codeassist.dsl.ModifiableType;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;

public class ReplaceJavaDocPartsAction implements SDKBuilderAction {

	private static final String TYPE_PREFIX_WITHOUT_TYPE = DSLConstants.HYPERLINK_TYPE_PREFIX + "#";
	private static final Pattern PATTERN_TYPE_PREFIX_WITHOUT_TYPE = Pattern.compile(TYPE_PREFIX_WITHOUT_TYPE);
	private static final Pattern PATTERN_WRONG_ANKERS = Pattern.compile("<a\\s*name\\s*=\\s*\".*\"\\s*/>");
	
	private CodeContentReplacer codeContentReplacer = new CodeContentReplacer();
	private LinkContentReplacer linkContentReplacer= new LinkContentReplacer();
	private ParamContentReplacer paramContentReplacer=new ParamContentReplacer();
	private ReturnContentReplacer returnContentReplacer=new ReturnContentReplacer();

	private SeeContentReplacer seeContentReplacer=new SeeContentReplacer();
	private SinceContentReplacer sinceContentReplacer = new SinceContentReplacer();
	private ValueContentReplacer valueContentReplacer = new ValueContentReplacer();
	
	@Override
	public void execute(SDKBuilderContext context) throws IOException {
		if (context.originTypeNameToOriginFileMapping.isEmpty()){
			throw new IllegalStateException("all types is empty!");
		}
		for (String typeName : context.originTypeNameToOriginFileMapping.keySet()){
			Type type = context.originGradleFilesProvider.getType(typeName);
			handleTypeAndContentInside(type,context);
		}
	}

	String handleTypeLinksWithoutType(String replacedJavaDocParts, Type parentType) {
		if (parentType == null) {
			throw new IllegalStateException("no parent type!");
		}
		if (replacedJavaDocParts==null){
			return null;
		}
		if (replacedJavaDocParts.indexOf(TYPE_PREFIX_WITHOUT_TYPE) == -1) {
			return replacedJavaDocParts;
		}
		String typeName = parentType.getName();
		Matcher matcher = PATTERN_TYPE_PREFIX_WITHOUT_TYPE.matcher(replacedJavaDocParts);
		replacedJavaDocParts = matcher.replaceAll(DSLConstants.HYPERLINK_TYPE_PREFIX + typeName + "#");
		return replacedJavaDocParts;
	}

	

	String removeWhitespacesAndStars(String text) {
		StringBuilder sb = new StringBuilder();

		boolean firstNonWhitespaceWorked = false;
		for (char c : text.toCharArray()) {
			if (!firstNonWhitespaceWorked) {
				if (Character.isWhitespace(c)) {
					continue;
				}
				firstNonWhitespaceWorked = true;
				if (c == '*') {
					continue;
				}
				/* other first char will be appended */
			}
			sb.append(c);
		}
		String result = sb.toString();
		return result;
	}

	String replaceJavaDocParts(String origin) {
		if (origin==null){
			return null;
		}
		String description = origin;
		description = PATTERN_WRONG_ANKERS.matcher(description).replaceAll("");
		description = replaceJavaDocLinks(description);
		description = replaceJavaDocValue(description);
		description = replaceJavaDocSee(description);
		description = replaceJavaDocCode(description);
		description = replaceJavaDocSince(description);
		description = replaceJavaDocParams(description);
		description = replaceJavaDocReturn(description);
		description = replaceDoubleSlashToEndWithCommentTag(description);
		return description;
	}

	
	/**
	 * Replace javadoc identifier tag and trailing parts: "@param name xyz" will be transformed by "${replacement} xyz"
	 * 
	 * @param text
	 * @param javadocId
	 * @param replacer
	 * @param fetchFullLineAsContent when <code>true</code> the full line is used as content on replacement:<br><br>
	 * <pre>"@myTag bla xyz\n" will be replaced by "$replacedContentFor(bla) xyz\n"</pre>
	 * When <code>false</code>  
	 * <pre>"@myTag bla xyz\n" will be replaced by "$replacedContentFor(bla xyz)\n"</pre>
	 * @return replaced string
	 */
	String replaceJavaDocTagAndTrailingParts(String text, String javadocId, ContentReplacer replacer, boolean fetchFullLineAsContent) {
		String result = text;
		while (true) {
			int index = result.indexOf(javadocId);
			if (index == -1) {
				return result;
			}
			
			String before = StringUtils.substring(result, 0, index);

			int length = result.length();
			StringBuilder content = new StringBuilder();
			boolean leadingWhiteSpaces = true;
			int pos = index + javadocId.length();

			while (pos < length) {
				char c = result.charAt(pos++);
				if (Character.isWhitespace(c)) {
					if (leadingWhiteSpaces) {
						continue;
					}
					if (fetchFullLineAsContent){
						/* we only break when line ends*/
						if (c=='\n'){
							pos--; // go back one step because after needs this maybe
							break;
						}else{
							/* do nothing, simply add  the whitespace itself too*/
						}
						
					}else {
						/* default way: every whitespace after leading ones will break*/
						pos--;
						break;
					}
				}
				leadingWhiteSpaces = false;
				content.append(c);
			}
			String after = StringUtils.substring(result, pos);
			String replaced = replacer.replace(content.toString());
			
			result = before + replaced+after;
		}
	}

	String replaceJavaDocTagInCurls(String description, String javaDocId, ContentReplacer replacer) {
		StringBuilder sb = new StringBuilder();
		JavaDocState state = JavaDocState.UNKNOWN;
		/* scan for first { found" */
		/*
		 * check if next is wanted javadoc, otherwise leafe state /* when in
		 * state - collect info until state }"
		 */
		StringBuilder content = new StringBuilder();
		StringBuilder contentUnchanged = new StringBuilder();
		for (char c : description.toCharArray()) {
			if (c == '{') {
				if (state==JavaDocState.CURLY_BRACKET_OPENED){
					sb.append(contentUnchanged);
				}
				content = new StringBuilder();
				contentUnchanged = new StringBuilder();
				state = JavaDocState.CURLY_BRACKET_OPENED;
				contentUnchanged.append(c);
				continue;

			} else if (c == '}') {
				contentUnchanged.append(c);
				if (state == JavaDocState.JAVADOC_TAG_FOUND) {
					String replaced = replacer.replace(content.toString().trim());
					sb.append(replaced);
				} else {
					sb.append(contentUnchanged);
				}
				content = new StringBuilder();
				contentUnchanged = new StringBuilder();
				state = JavaDocState.CURLY_BRACKET_CLOSED;
				continue;
			}
			if (state == JavaDocState.CURLY_BRACKET_OPENED) {
				contentUnchanged.append(c);
				if (content.length() == 0) {
					if (!Character.isWhitespace(c)) { // no leading whitespaces
														// after {
						content.append(c);
					}
				} else {
					content.append(c);
				}
			} else if (state == JavaDocState.JAVADOC_TAG_FOUND) {
				contentUnchanged.append(c);
				if (Character.isWhitespace(c)) {
					if (content.length() == 0) {
						/* forget leading whitespaces */
						continue;
					}
				}
				content.append(c);
			}

			if (state == JavaDocState.CURLY_BRACKET_OPENED) {
				if (content.toString().equals(javaDocId)) {
					state = JavaDocState.JAVADOC_TAG_FOUND;
					content = new StringBuilder();
				}
			}
			if (state == JavaDocState.UNKNOWN || state == JavaDocState.CURLY_BRACKET_CLOSED) {
				sb.append(c);
			}

		}
		if (state==JavaDocState.CURLY_BRACKET_OPENED){
			/* when last curly bracket was opened but not closed, we use complete content*/
			sb.append(contentUnchanged);
		}
		String result = sb.toString();
		return result;
	}

	private String buildNewDescription(Type type, String description, SDKBuilderContext context) {
		String replacedJavaDocParts = replaceJavaDocParts(description);
		replacedJavaDocParts = handleTypeLinksWithoutType(replacedJavaDocParts, type);
		return replacedJavaDocParts;
	}

	private void handleTypeAndContentInside(Type type,SDKBuilderContext context) throws IOException{
		if (type instanceof ModifiableType) {
			ModifiableType modifiableType = (ModifiableType) type;
			String description = buildNewDescription(type,type.getDescription(),context);
			modifiableType.setDescription(description);
		}
		
		for (Method method: type.getDefinedMethods()){
			if (method instanceof ModifiableMethod) {
				ModifiableMethod modifiableMethod = (ModifiableMethod) method;
				String description = buildNewDescription(type, method.getDescription(), context);
				modifiableMethod.setDescription(description);
			}
		}
		
		for (Property property: type.getDefinedProperties()){
			if (property instanceof ModifiableProperty) {
				ModifiableProperty modifiableProperty= (ModifiableProperty) property;
				String description = buildNewDescription(type, property.getDescription(), context);
				modifiableProperty.setDescription(description);
			}
		}
	}

	private String replaceCommentInLine(String text) {
		if (text.indexOf("//")==-1){
			return text;
		}
		String before = StringUtils.substringBefore(text, "//");
		int lastindexOpening=before.lastIndexOf('<');
		int lastindexClosing=before.lastIndexOf('>');
		if (lastindexOpening>lastindexClosing){
			/* e.g. a "< // comment" or a "<><//comment" which will be ignored*/
			return text;
		}
		
		String after = StringUtils.substringAfter(text,"//");
		StringBuilder sb = new StringBuilder();
		sb.append(before);
		sb.append("<em class='comment'>//");
		if (after.endsWith("\n")){
			sb.append(StringUtils.substringBeforeLast(after, "\n"));	
			sb.append("</em>\n");
			
		}else{
			sb.append(after);	
			sb.append("</em>");
		}
		return sb.toString();
	}
	
	private String replaceDoubleSlashToEndWithCommentTag(String text) {
		if (text.indexOf("//")==-1){
			return text;
		}
		String[] splitted = StringUtils.splitByWholeSeparatorPreserveAllTokens(text,"\n");
		StringBuilder sb = new StringBuilder();
		int pos=0;
		for (String stext: splitted){
			String replaced = replaceCommentInLine(stext);
			sb.append(replaced);
			pos++;
			if (pos<splitted.length){
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private String replaceJavaDocCode(String text) {
		// * @return ...
		String replaced = replaceJavaDocTagInCurls(text, "@code", codeContentReplacer);
		return replaced;
	}
	
	private String replaceJavaDocLinks(String description) {
		String replaced = replaceJavaDocTagInCurls(description, "@link", linkContentReplacer);
		return replaced;
	}
	
	private String replaceJavaDocValue(String description) {
		String replaced = replaceJavaDocTagInCurls(description, "@value", valueContentReplacer);
		return replaced;
	}
	
	private String replaceJavaDocParams(String text) {
		// * @param msg asdfasfasf
		String replaced = replaceJavaDocTagAndTrailingParts(text, "@param", paramContentReplacer, false);
		return replaced;
	}
	
	private String replaceJavaDocSince(String text) {
		// * @since 3.0
		String replaced = replaceJavaDocTagAndTrailingParts(text, "@since", sinceContentReplacer, true);
		return replaced;
	}

	private String replaceJavaDocReturn(String text) {
		// * @return ...
		String replaced = replaceJavaDocTagAndTrailingParts(text, "@return", returnContentReplacer,true);
		return replaced;

	}

	private String replaceJavaDocSee(String text) {
		// * @return ...
		String replaced = replaceJavaDocTagAndTrailingParts(text, "@see",seeContentReplacer,true);
		return replaced;

	}

	private enum JavaDocState {
		CURLY_BRACKET_CLOSED, CURLY_BRACKET_OPENED, JAVADOC_TAG_FOUND, UNKNOWN
	}
	
	private abstract class ContentReplacer {
		
		public abstract String replace(String content);
	
	}

	private final class CodeContentReplacer extends ContentReplacer {
		
		@Override
		public String replace(String content) {
			// return "<div class='code'>" + content + "</div>";
			return "<a href='" + HYPERLINK_TYPE_PREFIX + content + "'>" + content + "</a>";
		}
	}

	private final class LinkContentReplacer extends ContentReplacer {
		@Override
		public String replace(String content) {
			if (content.indexOf("<a href=")!=-1){
				/* we do not link when alreadly a link */
				return content;
			}
			String[] splitted = StringUtils.split(content);
			String text ="";
			String target = "";
			if (splitted.length<2){
				text=content;
				target=content;
			}else{
				target=splitted[0];
				for (int i=1;i<splitted.length;i++){
					text=text+splitted[i];
					if (i<splitted.length-1){
						text+=" ";
					}
				}
				
			}
			String result ="<a href='" + HYPERLINK_TYPE_PREFIX + target + "'>" + text + "</a>"; 
			return result;
		}
	}

	private final class ParamContentReplacer extends ContentReplacer {
		@Override
		public String replace(String content) {
			return "<br><b class='param'>param:</b>" + content;
		}
	}
	
	private final class SinceContentReplacer extends ContentReplacer {
		@Override
		public String replace(String content) {
			return "<br>(since " + content + ")";
		}
	}
	
	private final class ValueContentReplacer extends ContentReplacer {
		@Override
		public String replace(String content) {
			return "<em class='value'>" + content + "</em>";
		}
	}

	private final class ReturnContentReplacer extends ContentReplacer {
		@Override
		public String replace(String content) {
			return "<br><br><b class='return'>returns:</b>" + content;
		}
	}

	private final class SeeContentReplacer extends ContentReplacer {
		@Override
		public String replace(String content) {
			String link = linkContentReplacer.replace(content);
			StringBuilder sb = new StringBuilder();
			sb.append("(see ");
			sb.append(link);
			sb.append(')');
			return sb.toString();
		}
	}
}