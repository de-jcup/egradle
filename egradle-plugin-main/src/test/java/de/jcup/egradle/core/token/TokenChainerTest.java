package de.jcup.egradle.core.token;

import static org.junit.Assert.*;

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
		TokenImpl token1 = new TokenImpl(1);
		TokenImpl token2 = new TokenImpl(2);
		
		TokenImpl tokenRoot = new TokenImpl(0);
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
		TokenImpl token1 = new TokenImpl(1);
		TokenImpl token2 = new TokenImpl(2);
		
		TokenImpl token3 = new TokenImpl(3);
		TokenImpl token4 = new TokenImpl(4);
		TokenImpl token5 = new TokenImpl(5);
		
		token1.addChild(token3);
		token1.addChild(token4);
		token1.addChild(token5);
		
		TokenImpl tokenRoot = new TokenImpl(0);
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
