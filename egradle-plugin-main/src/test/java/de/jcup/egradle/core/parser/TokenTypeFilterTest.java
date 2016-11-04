package de.jcup.egradle.core.parser;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TokenTypeFilterTest {

	private TokenTypeFilter filterToTest;


	@Before
	public void before(){
		filterToTest = new TokenTypeFilter();
	}
	
	@Test
	public void nothing_added__null_is_filtered() {
		assertTrue(filterToTest.isFiltered(null));
	}
	
	@Test
	public void nothing_added__new_token_is_NOT_filtered() {
		assertFalse(filterToTest.isFiltered(new Token(1)));
	}
	
	@Test
	public void adding_tokentype_task__filters_token_with_type_task__but_not_token_with_type_unknown() {
		/* prepare */
		filterToTest.addIgnored(TokenType.TASK);
		
		Token taskToken = new Token(1).setType(TokenType.TASK);
		Token unknownToken = new Token(2).setType(TokenType.UNKNOWN);
		
		/* execute +test */
		assertTrue(filterToTest.isFiltered(taskToken));
		assertFalse(filterToTest.isFiltered(unknownToken));
	}

}
