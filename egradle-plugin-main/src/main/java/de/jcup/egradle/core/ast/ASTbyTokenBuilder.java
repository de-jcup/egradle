package de.jcup.egradle.core.ast;

import de.jcup.egradle.core.ast.initialize.DefStatementInitializer;
import de.jcup.egradle.core.ast.statement.DefStatement;
import de.jcup.egradle.core.token.Token;

public class ASTbyTokenBuilder {
	
	
	DefStatementInitializer defStatementInitializer = new DefStatementInitializer();

	public AST build(Token rootToken) {
		if (rootToken == null) {
			throw new IllegalArgumentException("root token may not be null!");
		}
		ASTContext context = new ASTContext();

		if (rootToken.hasChildren()) {
			context.setLastToken(rootToken.getFirstChild());
			scan(context);
		}
		return context.getAst();
	}

	private void scan(ASTContext context) {
		createNewStatement(context);
		Token token = context.getLastToken();
		if (token.canGoForward()) {
			context.setLastToken(token.goForward());
			scan(context);
		}
	}

	private void createNewStatement(ASTContext context) {
		Token token = context.getLastToken();
		if ("def".equals(token.getValue())) {
			defStatementInitializer.initialize(new DefStatement(),context);
		}
		
	}
	

}
