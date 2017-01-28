package de.jcup.egradle.library.access;
import org.codehaus.groovy.ast.ASTNode;

public class EGradleBuildscriptResult {

	private ASTNode node;
	
	private Exception exception;
	
	public EGradleBuildscriptResult(Exception exception) {
		this.exception = exception;
	}
	
	public EGradleBuildscriptResult(ASTNode node) {
		this.node = node;
	}
	
	public Exception getException() {
		return exception;
	}
	
	public ASTNode getNode() {
		return node;
	}
}

