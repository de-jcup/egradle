package de.jcup.egradle.core.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AbstractGradleTokenTest {

	private AbstractGradleToken elementToTest;
	
	@Rule
	public ExpectedException expectedException=ExpectedException.none();

	@Before
	public void before(){
		elementToTest = new TestToken();
	}
	
	@Test
	public void new_element_returns_not_null_for_elements() {
		assertNotNull(elementToTest.getElements());
	}
	
	@Test
	public void list_returned_by_get_elements_is_not_changeable() {
		expectedException.expect(UnsupportedOperationException.class);
		elementToTest.getElements().add(new TestToken());
	}
	@Test
	public void new_element_returns_false_for_has_children() {
		assertFalse(elementToTest.hasChildren());
	}
	
	@Test
	public void new_element_returns_0_for_offset() {
		assertEquals(0, elementToTest.getOffset());
	}
	
	@Test
	public void element_with_given_offset_12_returns_12_for_offset() {
		elementToTest.offset=12;
		assertEquals(12, elementToTest.getOffset());
	}
	
	@Test
	public void new_element_returns_length_0() {
		assertEquals(0, elementToTest.getLength());
	}
	
	@Test
	public void element_with_name_123456_returns_length_6() {
		elementToTest.name="123456";
		assertEquals(6, elementToTest.getLength());
	}
	
	@Test
	public void element_with_one_child_returns_true_for_has_children() {
		elementToTest.elements.add(new TestToken());
		assertTrue(elementToTest.hasChildren());
	}
	@Test
	public void new_element_returns_empty_list_for_elements() {
		assertEquals(new ArrayList<>(), elementToTest.getElements());
	}
	
	@Test
	public void new_element_returns_null_for_name() {
		assertNull(elementToTest.getName());
	}
	
	@Test
	public void new_element_returns_negative1_for_linenumber() {
		assertEquals(-1, elementToTest.getLineNumber());
	}
	
	@Test
	public void new_element_returns_null_for_parent() {
		assertNull(elementToTest.getParent());
	}
	
	@Test
	public void element_with_parent_set_returns_parent() {
		AbstractGradleToken parentElement = new TestToken();
		elementToTest.parent=parentElement;
		assertEquals(parentElement, elementToTest.getParent());
	}
	
	@Test
	public void element_with_linenumber_set_returns_parent() {
		elementToTest.lineNumber=10;
		assertEquals(10, elementToTest.getLineNumber());
	}
	
	@Test
	public void element_with_child_element_returns_list_with_childelement_inside() {
		AbstractGradleToken child = new TestToken();
		elementToTest.elements.add(child);
		List<AbstractGradleToken> elements = elementToTest.getElements();
		assertNotNull(elements);
		assertEquals(1, elements.size());
		assertTrue(elements.contains(child));
	
	}

	private class TestToken extends AbstractGradleToken{
		
	}
}
