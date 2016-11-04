package de.jcup.egradle.core.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TokenTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private Token tokenToTest;

	@Before
	public void before(){
		tokenToTest=new Token(0);
	}
	
	@Test
	public void new_token_has_type_UNKNOWN() {
		assertEquals(TokenType.UNKNOWN,tokenToTest.getType());
	}

	@Test
	public void new_token_with_type_set_to_null_has_type_UNKNOWN() {
		tokenToTest.setType(null);
		assertEquals(TokenType.UNKNOWN,tokenToTest.getType());
	}
	
	@Test
	public void new_token_with_type_set_to_TASK_has_type_TASK() {
		tokenToTest.setType(TokenType.TASK);
		assertEquals(TokenType.TASK,tokenToTest.getType());
	}
	
	@Test
	public void new_token_returns_not_null_for_tokens() {
		assertNotNull(tokenToTest.getChildren());
	}
	
	@Test
	public void list_returned_by_get_tokens_is_not_changeable() {
		expectedException.expect(UnsupportedOperationException.class);
		tokenToTest.getChildren().add(new Token(1));
	}
	
	@Test
	public void new_token_returns_false_for_has_children() {
		assertFalse(tokenToTest.hasChildren());
	}
	
	@Test
	public void new_token_returns_0_for_offset() {
		assertEquals(0, tokenToTest.getOffset());
	}
	
	@Test
	public void element_with_given_offset_12_returns_12_for_offset() {
		tokenToTest.setOffset(12);
		assertEquals(12, tokenToTest.getOffset());
	}
	
	@Test
	public void new_token_returns_length_0() {
		assertEquals(0, tokenToTest.getLength());
	}
	
	@Test
	public void element_with_name_123456_returns_length_6() {
		tokenToTest.setName("123456");
		assertEquals(6, tokenToTest.getLength());
	}
	
	@Test
	public void element_with_one_child_returns_true_for_has_children() {
		tokenToTest.addChild(new Token(1));
		assertTrue(tokenToTest.hasChildren());
	}
	@Test
	public void new_token_returns_empty_list_for_tokens() {
		assertEquals(new ArrayList<>(), tokenToTest.getChildren());
	}
	
	@Test
	public void new_token_returns_null_for_name() {
		assertNull(tokenToTest.getName());
	}
	
	@Test
	public void new_token_returns_negative1_for_linenumber() {
		assertEquals(-1, tokenToTest.getLineNumber());
	}
	
	@Test
	public void new_token_returns_null_for_parent() {
		assertNull(tokenToTest.getParent());
	}
	
	@Test
	public void element_with_parent_set_returns_parent() {
		Token parentElement = new Token(666);
		parentElement.addChild(tokenToTest);
		assertEquals(parentElement, tokenToTest.getParent());
	}
	
	@Test
	public void element_with_linenumber_set_returns_parent() {
		tokenToTest.setLineNumber(10);
		assertEquals(10, tokenToTest.getLineNumber());
	}
	
	@Test
	public void element_with_child_token_returns_list_with_childelement_inside() {
		Token child = new Token(1);
		tokenToTest.addChild(child);
		List<Token> elements = tokenToTest.getChildren();
		assertNotNull(elements);
		assertEquals(1, elements.size());
		assertTrue(elements.contains(child));
	
	}
}
