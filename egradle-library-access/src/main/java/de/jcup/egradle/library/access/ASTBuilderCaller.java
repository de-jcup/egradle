package de.jcup.egradle.library.access;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;

public class ASTBuilderCaller {

	public List<ASTNode> build(String code){
		AstBuilder builder = new AstBuilder();
		List<ASTNode> nodes = builder.buildFromString(code);
		return nodes;
	}
}
