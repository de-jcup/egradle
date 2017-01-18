package de.jcup.egradle.library.access;

import java.util.concurrent.Future;

import org.codehaus.groovy.ast.ASTNode;

public class GradleBuildScriptResultBuilderTestcase1 {
	public static void main(String[] args) throws Exception {
		String pathToGradle = System.getProperty("user.home")
				+ "/.gradle/wrapper/dists/gradle-3.1-bin/37qejo6a26ua35lyn7h1u9v2n/gradle-3.1";
		StringBuilder buildScript = new StringBuilder();

		buildScript.append("subProjects{\n");
		buildScript.append("	apply from: \"${rootProject.projectDir}/subX.gradle\"\n");
		buildScript.append("}");
		buildScript.append("allProjects{\n");
		buildScript.append("	apply from: \"${rootProject.projectDir}/allX.gradle\"\n");
		buildScript.append("}");

		Future<EGradleBuildscriptResult> futureStatement = new GradleBuildScriptResultBuilder()
				.build(buildScript.toString(), pathToGradle);
		EGradleBuildscriptResult buildscriptResult = futureStatement.get();
		ASTNode statement = buildscriptResult.getNode();
		if (buildscriptResult.getException()!=null){
			buildscriptResult.getException().printStackTrace(System.err);
			return;
		}
		String text = statement != null ? statement.getText() : "error:";
		System.out.println("script:" + text);
		GradleBuildScriptResultInspector inspector = new GradleBuildScriptResultInspector();
		inspector.inspect(statement);
	}
}
