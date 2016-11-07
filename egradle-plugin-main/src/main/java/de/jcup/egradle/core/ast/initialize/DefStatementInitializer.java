package de.jcup.egradle.core.ast.initialize;

import de.jcup.egradle.core.ast.ASTContext;
import de.jcup.egradle.core.ast.statement.DefStatement;
import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.core.token.TokenType;

public class DefStatementInitializer implements StatementInitializer<DefStatement> {

	@Override
	public void initialize(DefStatement statement, ASTContext context) {
		/* def variable = value */
		/* def method() {...}*/
		/* def method(params) {...}*/
		Token defToken = context.getLastToken();
		
		if (defToken.canGoForward()){
			Token variableOrMethodToken = defToken.goForward();
			if (variableOrMethodToken.canGoForward()){
				Token next = variableOrMethodToken.goForward();
				if (TokenType.BRACE_OPENING.equals(next.getType())){
					statement.setMethodName(variableOrMethodToken.getValue());
					if (next.canGoForward()){
						next = next.goForward();
						if (TokenType.BRACE_CLOSING.equals(next.getType())){
							statement.setCorrectBuild(true);
							context.setLastToken(next);
						}
						
					}
				}else if (TokenType.OPERATOR_EQUAL.equals(next.getType())){
					statement.setVariableName(variableOrMethodToken.getValue());
					if (next.canGoForward()){
						Token variableValue = next.goForward();
						statement.setVariableValue(variableValue.getValue());
						statement.setCorrectBuild(true);
						context.setLastToken(variableValue);
					}
				}
				
			}
		}
		
		
	}

}
