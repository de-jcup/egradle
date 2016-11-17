package de.jcup.egradle.other;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;

import org.apache.commons.io.FileUtils;

/**
 * Collects groovy sources and copies anlr parts necessary for EGradles "gradle
 * model builder" into egradle source folders<br/>
 * <br/>
 * Prerequisites:
 * <ol>
 * <li>git clone https://github.com/apache/groovy</li>
 * <li>gradlew assemble</li>
 * </ol>
 * 
 * @author Albert Tregnaghi
 *
 */
public class GroovyParserSourceCollector {

	private static final Pattern DOT = Pattern.compile("\\.");

	private static final Pattern IMPORT = Pattern.compile("import ");

	private static final Pattern SEMICOLON = Pattern.compile(";");

	public static void main(String[] args) throws IOException {
		new GroovyParserSourceCollector().start(args);
	}

	protected void execute(File sourceRootDir, File targetDir) throws IOException {
		System.out.println("delete:" + targetDir);
		FileUtils.deleteDirectory(targetDir);
		targetDir.mkdirs();
		/* copy directories + subdirectories */
		List<String> fullPackages = new ArrayList<>();
		// fullPackages.add("org/codehaus/groovy/antlr");
		// fullPackages.add("org/codehaus/groovy/ast");
		// fullPackages.add("org/codehaus/groovy/syntax");
		// fullPackages.add("org/codehaus/groovy/control");
		for (String i : fullPackages) {
			copyDirFull(sourceRootDir, targetDir, i);
		}
		/* remove some directories */
		List<String> fullPackagesToDrop = new ArrayList<>();
		fullPackagesToDrop.add("org/codehaus/groovy/antlr/java");
		for (String i : fullPackagesToDrop) {
			System.out.println("Delete:" + i);
			FileUtils.deleteDirectory(new File(targetDir, i));
		}
		/* import dedicated parts */
		List<String> imports = new ArrayList<>();
		// @formatter:off
		imports.add(imported("import org.codehaus.groovy.antlr.LineColumn;"));
		imports.add(imported("import org.codehaus.groovy.antlr.SourceBuffer;"));
		imports.add(imported("import org.codehaus.groovy.GroovyBugError;"));
		imports.add(imported("org.codehaus.groovy.antlr.SourceInfo"));
		imports.add(imported("org.codehaus.groovy.antlr.GroovySourceToken"));
		imports.add(imported("org.codehaus.groovy.antlr.java.JavaLexer"));
		imports.add(imported("org.codehaus.groovy.antlr.java.JavaTokenTypes"));
		imports.add(imported("org.codehaus.groovy.antlr.java.JavaRecognizer"));
		imports.add(imported("import org.codehaus.groovy.antlr.GroovySourceAST;"));
		imports.add(imported("import org.codehaus.groovy.antlr.UnicodeEscapingReader;"));
		imports.add(imported("import org.codehaus.groovy.antlr.parser.GroovyLexer;"));
		imports.add(imported("import org.codehaus.groovy.antlr.parser.GroovyRecognizer;"));
		imports.add(imported("org.codehaus.groovy.antlr.parser.GroovyTokenTypes"));
		// @formatter:on

		for (String i : imports) {
			copyFile(sourceRootDir, targetDir, i);
		}

	}

	private void assertDirectoryExists(File directory) {
		if (!directory.exists()) {
			System.err.println("does not exist:" + directory);
			System.exit(2);
		}
		if (!directory.isDirectory()) {
			System.err.println("not a directory:" + directory);
			System.exit(2);
		}
	}

	private void copyDirFull(File sourceDir, File targetDir, String subPath) throws IOException {
		File antlrPartsSource = new File(sourceDir, subPath);
		File antlrPartsTarget = new File(targetDir, subPath);
		antlrPartsTarget.getParentFile().mkdirs();
		System.out.println("copy dir:" + subPath);
		FileUtils.copyDirectory(antlrPartsSource, antlrPartsTarget);
	}

	private void copyFile(File sourceDir, File targetDir, String subPath) throws IOException {
		File antlrPartsSource = new File(sourceDir, subPath);
		File antlrPartsTarget = new File(targetDir, subPath);
		antlrPartsTarget.getParentFile().mkdirs();
		System.out.println("copy file:" + subPath);
		FileUtils.copyFile(antlrPartsSource, antlrPartsTarget);
	}

	private String imported(String string) {
		// import org.codehaus.groovy.antlr.GroovySourceAST;
		// import org.codehaus.groovy.antlr.UnicodeEscapingReader;
		string = IMPORT.matcher(string).replaceAll("");
		string = DOT.matcher(string).replaceAll("/");
		string = SEMICOLON.matcher(string).replaceAll("");
		string = "/" + string.trim() + ".java";
		return string;
	}

	private void start(String[] args) throws IOException {
		File groovyProjectDirectory;
		if (args.length == 0) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.showOpenDialog(null);
			groovyProjectDirectory = fc.getSelectedFile();
		} else {
			groovyProjectDirectory = new File(args[0]);
		}
		if (groovyProjectDirectory == null) {
			System.err.println("canceled");
			System.exit(1);
		}
		System.out.println("use groovy sources at:" + groovyProjectDirectory.getAbsolutePath());

		assertDirectoryExists(groovyProjectDirectory);
		File groovyTempDirectory = new File(groovyProjectDirectory, "/target/tmp/groovydoc");
		File target = new File("./..//egradle-plugin-main/src/main/java-groovy");
		assertDirectoryExists(target);
		execute(groovyTempDirectory, target);
		System.out.println("DONE");
	}
}
