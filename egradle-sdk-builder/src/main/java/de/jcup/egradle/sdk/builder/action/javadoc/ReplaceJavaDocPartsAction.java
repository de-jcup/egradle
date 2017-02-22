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
	
	@Override
	public void execute(SDKBuilderContext context) throws IOException {
		if (context.allTypes.isEmpty()){
			throw new IllegalStateException("all types is empty!");
		}
		for (String typeName : context.allTypes.keySet()){
			Type type = context.originGradleFilesProvider.getType(typeName);
			handleTypeAndContentInside(type,context);
		}
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
	
	private String buildNewDescription(Type type, String description, SDKBuilderContext context) {
		String replacedJavaDocParts = replaceJavaDocParts(description);
		replacedJavaDocParts = handleTypeLinksWithoutType(replacedJavaDocParts, type);
		return replacedJavaDocParts;
	}

	String handleTypeLinksWithoutType(String replacedJavaDocParts, Type parentType) {
		if (parentType == null) {
			throw new IllegalStateException("no parent type!");
		}
		if (replacedJavaDocParts.indexOf(TYPE_PREFIX_WITHOUT_TYPE) == -1) {
			return replacedJavaDocParts;
		}
		String typeName = parentType.getName();
		Matcher matcher = PATTERN_TYPE_PREFIX_WITHOUT_TYPE.matcher(replacedJavaDocParts);
		replacedJavaDocParts = matcher.replaceAll(DSLConstants.HYPERLINK_TYPE_PREFIX + typeName + "#");
		return replacedJavaDocParts;
	}

	

	String replaceJavaDocParts(String origin) {
		String line = origin;
		line = replaceJavaDocLinks(line);
		line = replaceJavaDocCode(line);
		line = replaceJavaDocParams(line);
		line = replaceJavaDocReturn(line);
		return line;
	}

	private abstract class ContentReplacer {

		public abstract String replace(String curlyContent);

	}

	private enum JavaDocState {
		JAVADOC_TAG_FOUND, CURLY_BRACKET_OPENED, CURLY_BRACKET_CLOSED, UNKNOWN
	}

	String replaceJavaDocTagInCurls(String line, String javaDocId, ContentReplacer replacer) {
		StringBuilder sb = new StringBuilder();
		JavaDocState state = JavaDocState.UNKNOWN;
		/* scan for first { found" */
		/*
		 * check if next is wanted javadoc, otherwise leafe state /* when in
		 * state - collect info until state }"
		 */
		StringBuilder curlyContent = new StringBuilder();
		StringBuilder curlyContentUnchanged = new StringBuilder();
		for (char c : line.toCharArray()) {
			if (c == '{') {
				curlyContent = new StringBuilder();
				curlyContentUnchanged = new StringBuilder();
				state = JavaDocState.CURLY_BRACKET_OPENED;
				curlyContentUnchanged.append(c);
				continue;

			} else if (c == '}') {
				curlyContentUnchanged.append(c);
				if (state == JavaDocState.JAVADOC_TAG_FOUND) {
					String replaced = replacer.replace(curlyContent.toString().trim());
					sb.append(replaced);
				} else {
					sb.append(curlyContentUnchanged);
				}
				curlyContent = new StringBuilder();
				curlyContentUnchanged = new StringBuilder();
				state = JavaDocState.CURLY_BRACKET_CLOSED;
				continue;
			}
			if (state == JavaDocState.CURLY_BRACKET_OPENED) {
				curlyContentUnchanged.append(c);
				if (curlyContent.length() == 0) {
					if (!Character.isWhitespace(c)) { // no leading whitespaces
														// after {
						curlyContent.append(c);
					}
				} else {
					curlyContent.append(c);
				}
			} else if (state == JavaDocState.JAVADOC_TAG_FOUND) {
				curlyContentUnchanged.append(c);
				if (Character.isWhitespace(c)) {
					if (curlyContent.length() == 0) {
						/* forget leading whitespaces */
						continue;
					}
				}
				curlyContent.append(c);
			}

			if (state == JavaDocState.CURLY_BRACKET_OPENED) {
				if (curlyContent.toString().equals(javaDocId)) {
					state = JavaDocState.JAVADOC_TAG_FOUND;
					curlyContent = new StringBuilder();
				}
			}
			if (state == JavaDocState.UNKNOWN || state == JavaDocState.CURLY_BRACKET_CLOSED) {
				sb.append(c);
			}

		}
		String result = sb.toString();
		return result;
	}

	/**
	 * Replace javadoc identifier tag and all rest of line
	 * 
	 * @param line
	 * @param javadocId
	 * @param replacer
	 * @return replaced string
	 */
	String replaceJavaDocTagAndTrailingParts(String xline, String javadocId, ContentReplacer replacer) {
		String result = xline;
		while (true) {
			int index = result.indexOf(javadocId);
			if (index == -1) {
				return result;
			}
			int length = result.length();
			StringBuilder content = new StringBuilder();
			String before = StringUtils.substring(result, 0, index);
			boolean leadingWhiteSpaces = true;
			int pos = index + javadocId.length();
			while (pos < length) {
				char c = result.charAt(pos++);
				if (Character.isWhitespace(c)) {
					if (leadingWhiteSpaces) {
						continue;
					}
				}
				leadingWhiteSpaces = false;
				content.append(c);
			}
			String replaced = replacer.replace(content.toString());
			result = before + replaced;
		}
	}
	/*
	 * FIXME ATR, 06.02.2017: @see xyz(xxx) should be converted to <a
	 * href="type://xyz(xxx)>!!!
	 */

	private String replaceJavaDocParams(String line) {
		// * @param msg asdfasfasf
		String replaced = replaceJavaDocTagAndTrailingParts(line, "@param", new ContentReplacer() {

			@Override
			public String replace(String content) {
				return "<br><b class='param'>param:</b>" + content;
			}
		});
		return replaced;

	}

	private String replaceJavaDocReturn(String line) {
		// * @return ...
		String replaced = replaceJavaDocTagAndTrailingParts(line, "@return", new ContentReplacer() {

			@Override
			public String replace(String content) {
				return "<br><br><b class='return'>returns:</b>" + content;
			}
		});
		return replaced;

	}

	private String replaceJavaDocCode(String line) {
		// * @return ...
		String replaced = replaceJavaDocTagInCurls(line, "@code", new ContentReplacer() {

			@Override
			public String replace(String content) {
				// return "<div class='code'>" + content + "</div>";
				return "<a href='" + HYPERLINK_TYPE_PREFIX + content + "'>" + content + "</a>";
			}
		});
		return replaced;

	}

	private String replaceJavaDocLinks(String line) {
		String replaced = replaceJavaDocTagInCurls(line, "@link", new ContentReplacer() {

			@Override
			public String replace(String content) {
				return "<a href='" + HYPERLINK_TYPE_PREFIX + content + "'>" + content + "</a>";
			}
		});
		return replaced;
	}

	String removeWhitespacesAndStars(String line) {
		StringBuilder sb = new StringBuilder();

		boolean firstNonWhitespaceWorked = false;
		for (char c : line.toCharArray()) {
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
}