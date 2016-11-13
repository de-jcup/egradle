package de.jcup.egradle.core.outline.groovyantlr;

import java.util.ArrayList;
import java.util.List;

public class DefaultGradleASTFilterStrategy extends ASTFilterStrategy{

	private List<String> acceptedExpressionNames = new ArrayList<>();
	
	public DefaultGradleASTFilterStrategy(){
		acceptedExpressionNames.add("allprojects");
		acceptedExpressionNames.add("subprojects");
		acceptedExpressionNames.add("dependencies");
		acceptedExpressionNames.add("buildscript");
	}
	
	@Override
	public boolean isExpressionIgnored(String name) {
		if (true)return false;
		return ! acceptedExpressionNames.contains(name);
	}

}
