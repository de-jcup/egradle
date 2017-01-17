package de.jcup.egradle.library.access;
import org.codehaus.groovy.ast.stmt.Statement;

public class EGradleBuildscriptResult {

	private Statement statement;
	
	private Exception exception;
	
	public EGradleBuildscriptResult(Exception exception) {
		this.exception = exception;
	}
	
	public EGradleBuildscriptResult(Statement statement) {
		this.statement = statement;
	}
	
	public Exception getException() {
		return exception;
	}
	
	public Statement getStatement() {
		return statement;
	}
}

