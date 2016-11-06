package de.jcup.egradle.core.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TokenChainerTest {
	
	private TokenChainer chainerToTest;

	@Before
	public void before(){
		chainerToTest = new TokenChainer();
	}

	@Test
	public void root_contains_token1_and_token2__after_chain__token_chain_is_fullfilled() {
		/* prepare */
		Token token1 = new Token(1);
		Token token2 = new Token(2);
		
		Token tokenRoot = new Token(0);
		tokenRoot.addChild(token1);
		tokenRoot.addChild(token2);
		
		/* execute */
		chainerToTest.chain(tokenRoot);
		
		/* test */
		assertFalse(tokenRoot.canGoBackward());
		assertFalse(tokenRoot.canGoForward());
		
		assertFalse(token1.canGoBackward());
		assertTrue(token1.canGoForward());
		assertEquals(token2,token1.goForward());
		
		assertTrue(token2.canGoBackward());
		assertEquals(token1,token2.goBackward());
		assertFalse(token2.canGoForward());
	}
	
	@Test
	public void root_contains_token1_and_token2__token_1_has_token3_and_token4_and_token5_as_children__after_chain__token_chain_is_fullfilled() {
		/* prepare */
		Token token1 = new Token(1);
		Token token2 = new Token(2);
		
		Token token3 = new Token(3);
		Token token4 = new Token(4);
		Token token5 = new Token(5);
		
		token1.addChild(token3);
		token1.addChild(token4);
		token1.addChild(token5);
		
		Token tokenRoot = new Token(0);
		tokenRoot.addChild(token1);
		tokenRoot.addChild(token2);
		
		/* execute */
		chainerToTest.chain(tokenRoot);
		
		/* test */
		assertFalse(tokenRoot.canGoBackward());
		assertFalse(tokenRoot.canGoForward());
		
		assertFalse(token1.canGoBackward());
		assertTrue(token1.canGoForward());
		assertEquals(token2,token1.goForward());
		
		assertTrue(token2.canGoBackward());
		assertEquals(token1,token2.goBackward());
		assertFalse(token2.canGoForward());
		
		
		/* children of token1*/
		assertFalse(token3.canGoBackward());
		assertEquals(token4,token3.goForward());
		assertTrue(token3.canGoForward());
		
		assertTrue(token4.canGoBackward());
		assertEquals(token3,token4.goBackward());
		assertTrue(token4.canGoForward());
		assertEquals(token5,token4.goForward());
		
		assertTrue(token5.canGoBackward());
		assertEquals(token4,token5.goBackward());
		assertFalse(token5.canGoForward());
	}

}
