/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
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

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.ModifiableMethod;
import de.jcup.egradle.codeassist.dsl.ModifiableProperty;
import de.jcup.egradle.codeassist.dsl.ModifiableType;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;

public class RemoveWhitespacesAndStarsFromJavadocAction implements SDKBuilderAction {

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
	
	

	private String buildNewDescription(Type parentType, String description,SDKBuilderContext context) throws IOException {
		if (description==null){
			return null;
		}
		StringBuilder fullDescription = new StringBuilder();
		String[] lines = StringUtils.split(description, System.getProperty("line.separator"));
		for (String line: lines){
			String newLine = removeWhitespacesAndStars(line);
			fullDescription.append(newLine);
			fullDescription.append("\n");
		}
		return fullDescription.toString();
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