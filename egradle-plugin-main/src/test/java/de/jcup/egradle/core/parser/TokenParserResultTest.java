package de.jcup.egradle.core.parser;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
public class TokenParserResultTest {

	private TokenParserResult resultToTest;
	
	@Before
	public void before(){
		resultToTest = new TokenParserResult();
	}
	
	@Test
	public void no_filter_set__tokens_added_are_in_result() {
		/* prepare*/
		Token token1 = mock(Token.class);
		Token token2 = mock(Token.class);
		
		/* execute */
		resultToTest.add(token1);
		resultToTest.add(token2);
		
		/* test */
		List<Token> tokens = resultToTest.getTokens();
		assertTrue(tokens.contains(token1));
		assertTrue(tokens.contains(token2));
	}
	
	@Test
	public void filter_for_token_1_and_3_set__token1_2_3_added__only_token2_is_in_result() {
		/* prepare*/
		Token token1 = mock(Token.class);
		Token token2 = mock(Token.class);
		Token token3 = mock(Token.class);
		
		TokenFilter filter = mock(TokenFilter.class);
		
		when(filter.isFiltered(token1)).thenReturn(true);
		when(filter.isFiltered(token2)).thenReturn(false);
		when(filter.isFiltered(token3)).thenReturn(true);
		
		resultToTest.setTokenFilter(filter);
		
		/* execute */
		resultToTest.add(token1);
		resultToTest.add(token2);
		
		/* test */
		List<Token> tokens = resultToTest.getTokens();
		assertFalse(tokens.contains(token1));
		assertTrue(tokens.contains(token2));
		assertFalse(tokens.contains(token3));
	}

}
