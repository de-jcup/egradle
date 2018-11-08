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
package de.jcup.egradle.codeassist.dsl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.integration.test.DescriptionFinderIntegrationTest;

public class DescriptionFinderTest {
	private DescriptionFinder finderToTest;

	@Before
	public void before() {
		finderToTest = new DescriptionFinder();
	}

	@Test
	public void find_with_null_returns_null() {
		assertNull(finderToTest.findDescription(null));
	}

	/**
	 * Simple test case for methods. More dedicated tests can be found in
	 * {@linkplain DescriptionFinderIntegrationTest}I
	 */
	@Test
	public void methodX__in_typeA_implements_interface_method() {
		/* ------- */
		/* prepare */
		/* ------- */
		Method methodX = mock(Method.class);
		when(methodX.getName()).thenReturn("methodX");
		when(methodX.getDescription()).thenReturn("@inheritDoc");

		/* create type1 and add method */
		Type type1 = mock(Type.class);
		when(methodX.getParent()).thenReturn(type1);

		/* create interface with methods */
		Type interface1 = mock(Type.class);
		Method methodIX = mock(Method.class);
		when(methodIX.getName()).thenReturn("methodX");
		when(methodIX.getDescription()).thenReturn("Description from interface");
		when(interface1.getDefinedMethods()).thenReturn(Collections.singleton(methodIX));

		/* add interface as reference to type1 */
		Set<TypeReference> interfaceSet = new LinkedHashSet<>();
		TypeReference ref = mock(TypeReference.class);
		interfaceSet.add(ref);
		when(type1.getInterfaces()).thenReturn(interfaceSet);
		when(ref.getType()).thenReturn(interface1);

		/* ------- */
		/* execute */
		/* ------- */
		String found = finderToTest.findDescription(methodX);

		/* ---- */
		/* test */
		/* ---- */
		assertTrue(found.contains("Description from interface"));
	}

	@Test
	public void methodX__in_typeA_has_description_itself_so_finder_returns_this_description() {
		/* ------- */
		/* prepare */
		/* ------- */
		Method methodX = mock(Method.class);
		when(methodX.getName()).thenReturn("methodX");
		when(methodX.getDescription()).thenReturn("self described");

		/* create type1 and add method */
		Type type1 = mock(Type.class);
		when(methodX.getParent()).thenReturn(type1);

		/* ------- */
		/* execute */
		/* ------- */
		String found = finderToTest.findDescription(methodX);

		/* ---- */
		/* test */
		/* ---- */
		assertEquals("self described", found);
	}

	@Test
	public void propertyX__in_typeA_has_description_itself_so_finder_returns_this_description() {
		/* ------- */
		/* prepare */
		/* ------- */
		Property propertyX = mock(Property.class);
		when(propertyX.getName()).thenReturn("propertyX");
		when(propertyX.getDescription()).thenReturn("self described");

		/* create type1 and add property */
		Type type1 = mock(Type.class);
		when(propertyX.getParent()).thenReturn(type1);

		/* ------- */
		/* execute */
		/* ------- */
		String found = finderToTest.findDescription(propertyX);

		/* ---- */
		/* test */
		/* ---- */
		assertEquals("self described", found);
	}

}
