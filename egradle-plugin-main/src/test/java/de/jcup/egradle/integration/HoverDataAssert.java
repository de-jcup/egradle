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
 package de.jcup.egradle.integration;

import static org.junit.Assert.*;

import java.text.MessageFormat;
import java.util.List;

import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Parameter;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.TypeChild;
import de.jcup.egradle.codeassist.dsl.gradle.estimation.GradleLanguageElementEstimater.EstimationResult;
import de.jcup.egradle.codeassist.hover.HoverData;

public class HoverDataAssert {

	public static HoverDataAssert assertThat(HoverData data){
		if (data==null){
			fail("hover data is null!");
		}
		return new HoverDataAssert(data);
	}

	private HoverData data;
	
	private HoverDataAssert(HoverData data){
		this.data=data;
	}
	
	public HoverDataAssert isForElementType(String name){
		Type type = ensuredElementType();
		assertEquals(name,type.getName());
		return this;
	}
	
	public HoverDataAssert hasElementName(String name){
		LanguageElement element = ensuredElement();
		assertEquals("Element has not wanted name!", name,element.getName());
		return this;
	}
	
	/**
	 * Assert element at hover data is a method with defined parameters
	 * @param fullMethodName - e.g. "org.groovy.Project.allproject"
	 * @param expectedParamTypes
	 * @return assert object
	 */
	public HoverDataAssert isForMethod(String fullMethodName, String ... expectedParamTypes) {
		/* check name correct */
		LanguageElement element = ensuredElement();
		String foundFullName = buildFullName(element);
		
		assertEquals("Method has not wanted name!", fullMethodName, foundFullName);
		assertTrue(element instanceof Method);
		
		Method method = (Method) element;
		List<Parameter> methodParams = method.getParameters();
		
		assertEquals("Method "+method.getName()+" found, but parameter size differs!", expectedParamTypes.length, methodParams.size());
		if (expectedParamTypes.length>0){
			int pos =0;
			for (String expectedParamType: expectedParamTypes){
				Parameter methodParam = methodParams.get(pos);
				String methodParamTypeAsString = methodParam.getTypeAsString();
				if (!expectedParamType.equals(methodParamTypeAsString)){
					String message = MessageFormat.format("Method parameter {0} is of type:{1}, but expected:{2}", pos, methodParamTypeAsString, expectedParamType);
					fail(message);
				}
				pos++;
			}
		}
		return this;
	}
	
	public HoverDataAssert isForExtension(String extensionName, String typeNameAsString) {
		/* check name correct */
		if (!ensuredResult().isTypeFromExtensionConfigurationPoint()){
			fail("This is not an extension:"+ensuredResult().getElement());
		}
		String foundExtensionName = ensuredResult().getExtensionName();
		assertEquals("Name of extension differs!", extensionName, foundExtensionName);
		LanguageElement element = ensuredElement();
		if (!(element instanceof Type)){
			fail("Element is not a type:"+element);
		}
		Type type = (Type) element;
		String foundFullName = buildFullName(type);
		
		assertEquals("Extension has not wanted type:", typeNameAsString, foundFullName);
		
		return this;
	}
	
	private String buildFullName(LanguageElement element) {
		StringBuilder sb = new StringBuilder();
		if (element instanceof TypeChild){
			TypeChild child  = (TypeChild)element;
			Type parent = child.getParent();
			assertNotNull("parent may not be null", parent);
			sb.append(parent.getName());
			sb.append('.');
		}
		sb.append(element.getName());
		return sb.toString();
	}

	private LanguageElement ensuredElement() {
		EstimationResult result = ensuredResult();
		LanguageElement element = result.getElement();
		assertNotNull("element is null !", element);
		return element;
	}

	private Type ensuredElementType() {
		EstimationResult result = ensuredResult();
		Type type = result.getElementType();
		assertNotNull("element type is null!", type);
		return type;
	}

	private EstimationResult ensuredResult() {
		EstimationResult result = data.getResult();
		assertNotNull("estimation result in data is null!!", result);
		return result;
	}
	
}
