package de.jcup.egradle.library.access;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;

public class GradleBuildScriptResultBuilder {
	public static void main(String[] args) throws Exception {
		String pathToGradle = System.getProperty("user.home")
				+ "/.gradle/wrapper/dists/gradle-3.1-bin/37qejo6a26ua35lyn7h1u9v2n/gradle-3.1";
		StringBuilder buildScript = new StringBuilder();

		buildScript.append("subProjects{\napply from: \"${rootProject.projectDir}/libraries.gradle\"\n}");

		Future<EGradleBuildscriptResult> futureStatement = new GradleBuildScriptResultBuilder().build(buildScript.toString(), pathToGradle);
		EGradleBuildscriptResult buildscriptResult = futureStatement.get();
		Statement statement = buildscriptResult.getStatement();
		String text = statement!=null ? statement.getText():"error";
		System.out.println("script:"+text);
	}

	public Future<EGradleBuildscriptResult> build(String buildScript, String pathToGradle) {
		CompletableFuture<EGradleBuildscriptResult> future = new CompletableFuture<>();

		new Thread() {
			@Override
			public void run() {
				try {
					new GradleBuildScriptResultBuilder().start(future, buildScript, pathToGradle);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		return future;
	}

	protected void start(CompletableFuture<EGradleBuildscriptResult> future, String buildScript, String pathToGradle)
			throws EGradleBuildScriptException {
		Exception error = null;
		File dir = new File(pathToGradle, "lib");
		List<URL> list = new ArrayList<>();
		File[] jarFiles = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file != null && file.isFile() && file.getName().endsWith(".jar");
			}
		});
		for (File file : jarFiles) {
			try {
				addFileToList(list, file);
			} catch (MalformedURLException e) {
				throw new EGradleBuildScriptException(
						"Was not able to parse buildscript because of missing library: " + file, e);
			}
		}
		URL[] urls = (URL[]) list.toArray(new URL[list.size()]);
		URLClassLoader child = new URLClassLoader(urls);
		Thread.currentThread().setContextClassLoader(child);

		StringBuilder buildscriptWrapper = new StringBuilder();
//		buildscriptWrapper.append("import org.gradle.api.internal.project.*\n");
		buildscriptWrapper.append("org.gradle.api.internal.project.ProjectScript buildscript= {\n");
		buildscriptWrapper.append(buildScript);
		buildscriptWrapper.append("\n}");

		try {
			Class<?> classToLoad;
			classToLoad = Class.forName("de.jcup.egradle.library.access.ASTBuilderCaller", true, child);
			Method method = classToLoad.getDeclaredMethod("build", String.class);
			Object instance = classToLoad.newInstance();

			@SuppressWarnings("unchecked")
			List<ASTNode> result = (List<ASTNode>) method.invoke(instance, buildscriptWrapper.toString());

			for (ASTNode node : result) {
				BlockStatement bs = (BlockStatement) node;
				for (org.codehaus.groovy.ast.stmt.Statement st : bs.getStatements()) {
					if (st instanceof ReturnStatement) {
						ReturnStatement rs = (ReturnStatement) st;
						Expression expression = rs.getExpression();
						if (expression instanceof DeclarationExpression) {
							DeclarationExpression de = (DeclarationExpression) expression;
							Expression right = de.getRightExpression();
							if (right instanceof ClosureExpression) {
								ClosureExpression ce = (ClosureExpression) right;
								ClassNode dc = ce.getDeclaringClass();
								if (dc != null) {
									List<MethodNode> methods = dc.getAbstractMethods();
									System.out.println("methods:" + methods);
								}
								Statement buildCode = ce.getCode();
								future.complete(new EGradleBuildscriptResult(buildCode));
								return;
							}
						}
					}
				}
			}
		} catch (InvocationTargetException | ClassNotFoundException | NoSuchMethodException | InstantiationException
				| IllegalAccessException e) {
			error=new EGradleBuildScriptException("Reflection problems", e);
		} catch (SecurityException e) {
			error= new EGradleBuildScriptException("Security problems", e);
		}
		if (error!=null){
			future.complete(new EGradleBuildscriptResult(error));
		}

	}


	private void addFileToList(List<URL> list, File file) throws MalformedURLException {
		if (!file.exists()) {
			throw new IllegalStateException("file does not exist");
		}
		URL url = file.toURI().toURL();
		System.out.println("add url to classpath:" + url);
		list.add(url);
	}
}
