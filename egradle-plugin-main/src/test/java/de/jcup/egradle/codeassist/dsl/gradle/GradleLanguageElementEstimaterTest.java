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
 package de.jcup.egradle.codeassist.dsl.gradle;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.TypeProvider;
import de.jcup.egradle.codeassist.dsl.gradle.estimation.GradleLanguageElementEstimater;
import de.jcup.egradle.codeassist.dsl.gradle.estimation.GradleLanguageElementEstimater.EstimationResult;
import de.jcup.egradle.core.model.Item;
public class GradleLanguageElementEstimaterTest {

	private GradleLanguageElementEstimater estimatorToTest;
	private TypeProvider mockedTypeProvider;

	@Before
	public void before(){
		
		mockedTypeProvider = mock(TypeProvider.class);
		estimatorToTest = new GradleLanguageElementEstimater(mockedTypeProvider);
	}
	
	@Test
	public void estimateFromGradleProjectAsRoot__when_parent_is_root__happy_item() {
		/* prepare */
		Type happyType = mock(Type.class); 
		Type projectType = mock(Type.class); 
		when(mockedTypeProvider.getType("org.gradle.api.Project")).thenReturn(projectType);
		when(mockedTypeProvider.getType("test.something.HappyType")).thenReturn(happyType);
		when(happyType.getName()).thenReturn("test.something.HappyType");
		
		Set<Method> projectMethods = new LinkedHashSet<>();
		Method happyMethod = mock(Method.class);
		projectMethods.add(happyMethod);
		when(happyMethod.getName()).thenReturn("happy");
		when(happyMethod.getReturnType()).thenReturn(happyType);
		when(projectType.getMethods()).thenReturn(projectMethods);
		
		Item item1 = mock(Item.class);
		Item root = mock(Item.class);
		when(item1.getParent()).thenReturn(root);
		when(item1.getIdentifier()).thenReturn("happy");

		when(root.getParent()).thenReturn(null);
		
		
		/* execute */
		EstimationResult result = estimatorToTest.estimate(item1,GradleFileType.GRADLE_BUILD_SCRIPT);
		
		/* test */
		assertEquals(happyMethod,result.getElement());
	}
	
	@Test
	public void estimateFromGradleProjectAsRoot__when_parent_is_happy_extension() {
		/* prepare */
		Type happyType = mock(Type.class); 
		Type projectType = mock(Type.class); 
		Map<String, Type> extensions = new HashMap<>();
		extensions.put("happy", happyType);
		
		when(projectType.getExtensions()).thenReturn(extensions);
		
		when(mockedTypeProvider.getType("org.gradle.api.Project")).thenReturn(projectType);
		when(mockedTypeProvider.getType("test.something.HappyType")).thenReturn(happyType);
		when(happyType.getName()).thenReturn("test.something.HappyType");
		
		
		Item item1 = mock(Item.class);
		Item root = mock(Item.class);
		when(item1.isClosureBlock()).thenReturn(true);
		when(item1.getParent()).thenReturn(root);
		when(item1.getIdentifier()).thenReturn("happy");
		
		
		when(root.getParent()).thenReturn(null);
		
		
		/* execute */
		EstimationResult result = estimatorToTest.estimate(item1,GradleFileType.GRADLE_BUILD_SCRIPT);
		
		/* test */
		assertEquals(happyType,result.getElement());
	}

}
