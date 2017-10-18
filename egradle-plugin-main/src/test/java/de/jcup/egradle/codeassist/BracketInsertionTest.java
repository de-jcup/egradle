package de.jcup.egradle.codeassist;

import static org.junit.Assert.*;

import org.junit.Test;

import de.jcup.egradle.codeassist.BracketInsertion;

public class BracketInsertionTest {

	@Test
	public void curly_braces_are_multilined() {
		assertTrue(BracketInsertion.CURLY_BRACES.isMultiLine());
	}
	
	@Test
	public void curly_braces_have_curly_braces_as_start() {
		assertEquals('{', BracketInsertion.CURLY_BRACES.getStart());
	}
	
	@Test
	public void curly_braces_have_curly_braces_as_end() {
		assertEquals('}', BracketInsertion.CURLY_BRACES.getEnd());
	}
	
	@Test
	public void curly_braces_have_curly_braces_as_oneliner() {
		assertEquals("{  }", BracketInsertion.CURLY_BRACES.createOneLineTemplate());
	}
	
	@Test
	public void curly_braces_have_curly_braces_as_multiliner() {
		assertEquals("{\n    xxx\n}", BracketInsertion.CURLY_BRACES.createMultiLineTemplate("xxx"));
	}
	
	@Test
	public void edge_braces_have_curly_braces_as_multiliner() {
		assertEquals("[\n    xxx\n]", BracketInsertion.EDGED_BRACES.createMultiLineTemplate("xxx"));
	}
	
	@Test
	public void curly_braces_have_curly_braces_as_oneliner_offset_plus_1() {
		assertEquals(1, BracketInsertion.CURLY_BRACES.createOneLineNewOffset(0));
	}
	
	@Test
	public void edge_braces_have_edge_braces_as_oneliner_offset_plus_1() {
		assertEquals(1, BracketInsertion.EDGED_BRACES.createOneLineNewOffset(0));
	}
	
	@Test
	public void edge_braces_have_edge_braces_as_oneliner() {
		assertEquals("[  ]", BracketInsertion.EDGED_BRACES.createOneLineTemplate());
	}
	

	@Test
	public void curly_braces_value_of_start_identified() {
		assertEquals(BracketInsertion.CURLY_BRACES, BracketInsertion.valueOfStartChar('{'));
	}
	
	@Test
	public void edge_braces_value_of_start_identified() {
		assertEquals(BracketInsertion.EDGED_BRACES, BracketInsertion.valueOfStartChar('['));
	}

}
