package de.jcup.egradle.core.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.core.token.TokenImpl;

public class OutlineModelTest {

	private TokenOutlineModel modelToTest;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void before() {
		modelToTest = new TokenOutlineModel();
	}

	@Test
	public void empty_model_returns_not_null_on_getRoot() {
		assertNotNull(modelToTest.getRoot());
	}

	@Test
	public void empty_model_returns_not_null_getRoot_getChildrenUnmodifable() {
		assertNotNull(modelToTest.getRoot().getChildren());
	}

	@Test
	public void item_created_with_null_as_token_throws_illegalargument() {
		expectedException.expect(IllegalArgumentException.class);

		/* execute + test */
		modelToTest.createItem(null);
	}

	@Test
	public void add_null_to_item_throws_illegal_argument() {
		expectedException.expect(IllegalArgumentException.class);

		/* execute + test */
		modelToTest.getRoot().add(null);
	}

	@Test
	public void token_with_offset_10_added__can_be_found_in_model_by_getItemAt_10() {
		Token mockedTocken = mock(TokenImpl.class);
		when(mockedTocken.getOffset()).thenReturn(10);
		when(mockedTocken.getLength()).thenReturn(2);

		Item item = modelToTest.createItem(mockedTocken);

		/* execute */
		modelToTest.getRoot().add(item);

		/* test */
		Item foundItem = modelToTest.getItemAt(10);
		assertNotNull(foundItem);
		assertEquals(item, foundItem);

	}

	@Test
	public void token_with_offset_10_added__can_be_found_in_model_by_getItemAt_9() {
		Token mockedToken = mock(TokenImpl.class);
		when(mockedToken.getOffset()).thenReturn(10);
		when(mockedToken.getLength()).thenReturn(2);

		Item item = modelToTest.createItem(mockedToken);

		/* execute */
		modelToTest.getRoot().add(item);

		/* test */
		Item foundItem = modelToTest.getItemAt(9);
		assertNotNull(foundItem);
		assertEquals(item, foundItem);

	}

	@Test
	public void token1_with_offset_10_added_and_token2_with_offset9__token2_will_be_found_in_model_by_getItemAt_9() {
		Token mockedToken1 = mock(TokenImpl.class);
		when(mockedToken1.getOffset()).thenReturn(10);
		when(mockedToken1.getLength()).thenReturn(2);

		Token mockedToken2 = mock(TokenImpl.class);
		when(mockedToken2.getOffset()).thenReturn(9);
		when(mockedToken2.getLength()).thenReturn(2);

		Item item1 = modelToTest.createItem(mockedToken1);
		Item item2 = modelToTest.createItem(mockedToken2);

		/* execute */
		modelToTest.getRoot().add(item1);
		modelToTest.getRoot().add(item2);

		/* test */
		Item foundItem = modelToTest.getItemAt(9);
		assertNotNull(foundItem);
		assertEquals(item2, foundItem);

	}

	@Test
	public void token_with_offset_10_added__can_NOT_be_found_in_model_by_getItemAt_11() {
		Token mockedToken = mock(TokenImpl.class);
		when(mockedToken.getOffset()).thenReturn(10);
		when(mockedToken.getLength()).thenReturn(2);

		Item item = modelToTest.createItem(mockedToken);

		/* execute */
		modelToTest.getRoot().add(item);

		/* test */
		Item foundItem = modelToTest.getItemAt(11);
		assertNull(foundItem);

	}
	
	@Test
	public void tokens_offset_and_length__used_for_create_item_is_inside_item(){
		Token mockedToken = mock(TokenImpl.class);
		when(mockedToken.getOffset()).thenReturn(10);
		when(mockedToken.getLength()).thenReturn(2);

		/* prepare*/
		Item item = modelToTest.createItem(mockedToken);
		
		/* execute */
		assertEquals(mockedToken.getOffset(),item.getOffset());
		assertEquals(mockedToken.getLength(),item.getLength());
	}

	@Test
	public void item1_added_to_parentItem__parentItem_getChildrenUnmodifable_is_unmodifiable() {
		assertNotNull(modelToTest.getRoot().getChildren());
	}
	
	@Test
	public void item1_added_to_rootItem_knows_root_item_is_its_parent(){
		Token mockedToken = mock(TokenImpl.class);

		/* prepare*/
		Item item1 = modelToTest.createItem(mockedToken);
		modelToTest.getRoot().add(item1);
		
		assertEquals(modelToTest.getRoot(), item1.getParent());
	
	}
	
	@Test
	public void item2_added_to_item1_added_to_rootItem_knows_item1_is_its_parent(){
		Token mockedToken = mock(TokenImpl.class);
		Token mockedToken2 = mock(TokenImpl.class);

		/* prepare*/
		Item item1 = modelToTest.createItem(mockedToken);
		modelToTest.getRoot().add(item1);
		
		Item item2 = modelToTest.createItem(mockedToken2);
		item1.add(item2);
		
		assertEquals(item1, item2.getParent());
	
	}
}
