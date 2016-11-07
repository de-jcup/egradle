package de.jcup.egradle.core.ast;

import de.jcup.egradle.core.ast.statement.Statement;
import de.jcup.egradle.core.token.Token;

public class ASTContext {
	private Token lastToken;
	private AST ast = new AST();
	private Statement currentStatement;
	
	public Token getLastToken() {
		return lastToken;
	}
	
	public void setLastToken(Token token) {
		this.lastToken = token;
	}
	
	public Statement getCurrentStatement() {
		return currentStatement;
	}
	
	public AST getAst() {
		return ast;
	}

	public boolean hasNoStatement() {
		return currentStatement == null;
	}
}