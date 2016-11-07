package de.jcup.egradle.core.ast.initialize;

import de.jcup.egradle.core.ast.ASTContext;
import de.jcup.egradle.core.ast.statement.Statement;

public interface StatementInitializer<T extends Statement> {

	/**
	 * Initializes given new statement. Context token is still on start token at
	 * this time!
	 * 
	 * @param statement
	 *            new created statement
	 * @param context
	 *            context to use
	 */
	public void initialize(T statement, ASTContext context);
}
