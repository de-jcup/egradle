package de.jcup.egradle.core.ast;

import java.util.ArrayList;
import java.util.List;

import de.jcup.egradle.core.ast.statement.Statement;

/**
 * Abstract syntax tree - only a few parts of gradle/groovy syntax is implemented! 
 * @author Albert Tregnaghi
 *
 */
public class AST implements Statement{

	private List<Statement> statements = new ArrayList<>();

	public List<Statement> getStatements(){
		return statements;
	}
}
