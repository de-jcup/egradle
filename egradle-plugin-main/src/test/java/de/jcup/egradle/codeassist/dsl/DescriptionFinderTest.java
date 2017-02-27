package de.jcup.egradle.codeassist.dsl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.test.integration.DescriptionFinderIntegrationTest;

public class DescriptionFinderTest {
	private DescriptionFinder finderToTest;

	@Before
	public void before() {
		finderToTest = new DescriptionFinder();
	}

	@Test
	public void find_with_null_returns_null(){
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

		/* create type1 and add method*/
		Type type1 = mock(Type.class);
		when(methodX.getParent()).thenReturn(type1);
		
		/* create interface with methods */
		Type interface1 = mock(Type.class);
		Method methodIX = mock(Method.class);
		when(methodIX.getName()).thenReturn("methodX");
		when(methodIX.getDescription()).thenReturn("Description from interface");
		when(interface1.getDefinedMethods()).thenReturn(Collections.singleton(methodIX));

		/* add interface as reference to type1*/
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
		assertEquals("Description from interface", found);
	}
	
	@Test
	public void methodX__in_typeA_has_description_itself_so_finder_returns_this_description() {
		/* ------- */
		/* prepare */
		/* ------- */
		Method methodX = mock(Method.class);
		when(methodX.getName()).thenReturn("methodX");
		when(methodX.getDescription()).thenReturn("self described");

		/* create type1 and add method*/
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

		/* create type1 and add property*/
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
