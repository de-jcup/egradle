package de.jcup.egradle.core.ast.initialize;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jcup.egradle.core.ast.ASTContext;
import de.jcup.egradle.core.ast.statement.DefStatement;
import de.jcup.egradle.core.token.TokenImpl;
public class DefStatementInitializerTest {
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private DefStatementInitializer initializerToTest;
	private TokenImpl defToken;
	private TokenImpl varToken;
	private TokenImpl equalToken;
	private TokenImpl value1Token;
	private ASTContext context;
	
	@Before
	public void before(){
		initializerToTest = new DefStatementInitializer();
		
		defToken = new TokenImpl(1);
		defToken.setValue("def");
		
		varToken = new TokenImpl(2);
		varToken.setValue("variable1");
		
		equalToken = new TokenImpl(3);
		varToken.setValue("=");
		
		value1Token= new TokenImpl(4);
		value1Token.setValue("1");
		
		context = new ASTContext();
	}
	
	@Test
	public void def_variable1_equal_value1_works() {
		
		/* prepare*/
		defToken.chain(varToken).chain(equalToken).chain(value1Token);
		context.setLastToken(defToken);
		
		/* execute */
		DefStatement statement = new DefStatement();
		initializerToTest.initialize(statement,context);
		
		/* test */
		
		assertEquals("variable1",statement.getVariableName());
		assertEquals("1",statement.getVariableValue());
	}
}
