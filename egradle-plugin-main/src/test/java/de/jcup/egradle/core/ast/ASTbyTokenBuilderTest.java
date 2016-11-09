package de.jcup.egradle.core.ast;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jcup.egradle.core.ast.initialize.DefStatementInitializer;
import de.jcup.egradle.core.token.TokenImpl;
import de.jcup.egradle.core.token.TokenType;

public class ASTbyTokenBuilderTest {
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private ASTbyTokenBuilder builderToTest;
	private TokenImpl rootToken;
	private TokenImpl defToken;
	private TokenImpl varToken;
	private TokenImpl equalToken;
	private TokenImpl value1Token;
	private DefStatementInitializer mockedDefStatementInitializer;
	
	@Before
	public void before(){
		builderToTest = new ASTbyTokenBuilder();
		mockedDefStatementInitializer = mock(DefStatementInitializer.class);
		builderToTest.defStatementInitializer=mockedDefStatementInitializer;
		
		rootToken = new TokenImpl(0);
		rootToken.setType(TokenType.ROOT);
		
		defToken = new TokenImpl(1);
		defToken.setValue("def");
		
		varToken = new TokenImpl(2);
		varToken.setValue("variable1");
		
		equalToken = new TokenImpl(3);
		equalToken.setValue("=");
		
		value1Token= new TokenImpl(4);
		value1Token.setValue("1");
	}
	
	@Test
	public void build_with_null_token_throws_illegal_argument_exception() {
		expectedException.expect(IllegalArgumentException.class);
		
		builderToTest.build(null);
	}
	
	
	@Test
	public void build_with_empty_root_token_returns_not_null() {
		
		AST ast = builderToTest.build(rootToken);
		assertNotNull(ast);
	}

	@Test
	public void build_with_root_token_calls_defstatement_initializer() {
		
		/* prepare*/
		defToken.chain(varToken).chain(equalToken).chain(value1Token);
		rootToken.addChild(defToken);
		
		/* execute */
		builderToTest.build(rootToken);
		
		/* test */
		verify(mockedDefStatementInitializer).initialize(any(), any());
	}
	
	@Test
	@Ignore // - test AST of groovy.jar before continue implementing
	public void fail_because_missing_() {
		fail("its absolute necessary that created statements are somwewhere added . ast or parent statement. but currently not implemented!");
	}
	
}
