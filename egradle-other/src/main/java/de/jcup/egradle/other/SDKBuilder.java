package de.jcup.egradle.other;

import static de.jcup.egradle.codeassist.dsl.DSLConstants.HYPERLINK_TYPE_PREFIX;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.XMLDSLTypeImporter;
import de.jcup.egradle.codeassist.dsl.XMLMethod;
import de.jcup.egradle.codeassist.dsl.XMLType;
import static java.nio.charset.StandardCharsets.*;

/**
 * The egradle <a href="https://github.com/de-jcup/gradle">gradle fork</a> has
 * special task called "dslEgradle".<br>
 * <br>
 * It is used to build DSL files usable by egradle code completion. But... there
 * is still work to do:
 * <ol>
 * <li>Descriptions are still javadoc like and must be changed</li>
 * <li>Each closure method parameter is problematic and must be replaced by
 * corre</li>
 * <li></li>
 * </ol>
 * 
 * @author Albert Tregnaghi
 *
 */
public class SDKBuilder {

	/*
	 * FIXME ATR, 20.01.2017: docbook does know the target!
	 * /gradle/buildSrc/src/main/groovy/org/gradle/build/docs/dsl/docbook/
	 * BlockDetailRenderer.java
	 */
	public static void main(String[] args) throws IOException {
		new SDKBuilder("./../../gradle/subprojects/docs").startTransformToUserHome("3.0", "1.0.0");
		;
	}

	/*
	 * FIXME ATR, 06.02.2017: sdk builder MUST set fix links like <a
	 * href="#method(x.x..)"> to <a href="#method(x.x..)"
	 */
	private File gradleEGradleDSLRootFolder;
	private File gradleOriginPluginsFile;
	private File gradleOriginMappingFile;

	/**
	 * Only for tests
	 */
	SDKBuilder() {

	}

	private XMLDSLTypeImporter importer = new XMLDSLTypeImporter();
	private XMLDSLTypeExporter exporter = new XMLDSLTypeExporter();

	public SDKBuilder(String pathToData) throws IOException {
		gradleEGradleDSLRootFolder = new File(pathToData, "/build/src-egradle/egradle-dsl");
		gradleOriginPluginsFile = new File(pathToData, "/src/docs/dsl/plugins.xml");
		gradleOriginMappingFile = new File(pathToData, "/build/generated-resources/main/api-mapping.txt");

		assertFileExists(gradleOriginPluginsFile);
		assertFileExists(gradleOriginMappingFile);
		assertDirectoryAndExists(gradleEGradleDSLRootFolder);
	}

	public void startTransformToUserHome(String gradleVersion, String targetSDKVersion) throws IOException {
		File sourceParentDirectory = new File(gradleEGradleDSLRootFolder, gradleVersion);
		assertDirectoryAndExists(sourceParentDirectory);

		/* healthy check: */
		File healthCheck = new File(sourceParentDirectory, "org/gradle/api/Project.xml");
		if (!healthCheck.exists()) {
			throw new FileNotFoundException("The generated source for org.gradle.api.Project is not found at:\n"
					+ healthCheck.getCanonicalPath()
					+ "\nEither your path or version is incorrect or you forgot to generate...");
		}
		String userHome = System.getProperty("user.home");
		File targetPathDirectory = new File(userHome, ".egradle/sdk/" + targetSDKVersion + "/gradle/");
		if (targetPathDirectory.exists()) {
			System.out.println(
					"Target directory exists - will be deleted before:" + targetPathDirectory.getCanonicalPath());
			FileUtils.deleteDirectory(targetPathDirectory);
		}
		System.out.println("start generation into:" + targetPathDirectory.getCanonicalPath());

		System.out.println("- copy plugins.xml and origin mapping file");
		FileUtils.copyFile(gradleOriginPluginsFile, new File(targetPathDirectory, gradleOriginPluginsFile.getName()));
		FileUtils.copyFile(gradleOriginMappingFile, new File(targetPathDirectory, gradleOriginMappingFile.getName()));

		System.out.println("- inspect files and generate targets");
		/*
		 * create alternative api-mapping because e.g EclipseWTP is not listed
		 * in orgin mapping file!
		 */
		BuilderContext builderContext = new BuilderContext();
		Map<String, String> alternativeApiMapping = new TreeMap<>();
		inspectFilesAdoptAndGenerateTarget(alternativeApiMapping, sourceParentDirectory, targetPathDirectory,
				builderContext);
		System.out.println("- generate alternative api mapping file");
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String shortName : alternativeApiMapping.keySet()) {
			if (first) {
				first = false;
			} else {
				sb.append("\n");
			}
			sb.append(shortName);
			sb.append(':');
			sb.append(alternativeApiMapping.get(shortName));
			sb.append(';');
		}
		File alternativeApiMappingFile = new File(targetPathDirectory, "alternative-api-mapping.txt");
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(alternativeApiMappingFile))) {
			bw.write(sb.toString());
		}
		System.out.println("- normal file generation done, type:// is now set in hyperlinks");
		System.out.println("- start delegatesTo estimation");
		/* now load the xml files as type data - and inspect all descriptions */
		System.out.println("- info:" + builderContext.getInfo());
		System.out.println("DONE");
	}

	private void assertDirectoryAndExists(File folder) throws IOException {
		if (!folder.exists()) {
			throw new FileNotFoundException(folder.getCanonicalPath() + " does not exist!");
		}

		if (!folder.isDirectory()) {
			throw new FileNotFoundException(folder.getCanonicalPath() + " ist not a directory!");
		}
	}

	private void assertFileExists(File file) throws FileNotFoundException, IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(file.getCanonicalPath() + " does not exist!");
		}
		if (!file.isFile()) {
			throw new FileNotFoundException(file.getCanonicalPath() + " ist not a file!");
		}
	}

	private void inspectFilesAdoptAndGenerateTarget(Map<String, String> alternativeApiMapping, File sourceDir,
			File targetDir, BuilderContext builderContext) throws IOException {
		for (File newSourceFile : sourceDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				if (file == null) {
					return false;
				}
				if (file.isDirectory()) {
					return true;
				}
				return file.getName().endsWith(".xml");
			}
		})) {
			String name = newSourceFile.getName();
			if (newSourceFile.isDirectory()) {
				File newTargetDir = new File(targetDir, name);
				inspectFilesAdoptAndGenerateTarget(alternativeApiMapping, newSourceFile, newTargetDir, builderContext);
			} else if (newSourceFile.isFile()) {
				File newTargetFile = new File(targetDir, name);

				String changedSource = readAndAdopt(alternativeApiMapping, newSourceFile);
				write(changedSource, newTargetFile);

				/* new TargetFile is now written */

				boolean ignore = false;
				String targetFileName = newTargetFile.getName();
				ignore = ignore | targetFileName.endsWith("plugins.xml");

				if (!ignore) {
					startDelegateTargetEstimation(newTargetFile, builderContext);
				}
			}
		}
	}

	private void startDelegateTargetEstimation(File newTargetFile, BuilderContext builderContext)
			throws IOException, FileNotFoundException {
		try {
			XMLType type = null;

			try (FileInputStream inputStream = new FileInputStream(newTargetFile)) {
				type = importer.importType(inputStream);

			}
			if (type == null) {
				throw new IllegalStateException("was not able to read type:" + newTargetFile);
			}
			estimateDelegateTargets(type, builderContext);
			try (FileOutputStream outputStream = new FileOutputStream(newTargetFile)) {
				exporter.exportType(type, outputStream);
			}
		} catch (IOException e) {
			throw new IOException("Problems with file:" + newTargetFile.getAbsolutePath(), e);
		}
	}

	private class BuilderContext {
		int methodWithOutDescriptionCount;
		int methodAllCount;

		public String getInfo() {
			double missingDescriptionPercent = 0;
			if (methodWithOutDescriptionCount != 0 && methodAllCount != 0) {
				double onePercent = methodAllCount / 100;
				missingDescriptionPercent = methodWithOutDescriptionCount / onePercent;
			}
			return "Methods all:" + methodAllCount + " - missing descriptions:" + methodWithOutDescriptionCount + " ="
					+ missingDescriptionPercent + "%";
		}
	}

	void estimateDelegateTargets(Type type) {
		estimateDelegateTargets(type, new BuilderContext());
	}

	void estimateDelegateTargets(Type type, BuilderContext builderContext) {
		int problemCount = 0;
		StringBuilder problems = new StringBuilder();
		for (Method m : type.getMethods()) {
			if (!(m instanceof XMLMethod)) {
				continue;
			}
			builderContext.methodAllCount++;
			XMLMethod method = (XMLMethod) m;
			String delegationTarget = method.getDelegationTargetAsString();
			if (!StringUtils.isBlank(delegationTarget)) {
				continue;// already set - maybe in future
			}
			String description = method.getDescription();
			if (description == null) {
				problemCount++;
				problems.append(method.getName());
				problems.append(" ");
				continue;
			}
			String targetType = null;
			int index = 0;
			while (targetType == null && index != -1) {
				int from = index + 1;
				index = StringUtils.indexOf(description, HYPERLINK_TYPE_PREFIX, from);
				if (index != -1) {
					targetType = inspect(index, description);
				}
			}

			if (targetType != null) {
				method.setDelegationTargetAsString(targetType);
			}
		}
		if (problemCount > 0) {
			System.out.println("- WARN: type:" + type.getName()
					+ " has following method without descriptions: has no description " + problems.toString());
			builderContext.methodWithOutDescriptionCount += problemCount;
		}

	}

	private String inspect(int index, String description) {
		StringBuilder sb = new StringBuilder();
		index = index + HYPERLINK_TYPE_PREFIX.length();
		int length = description.length();
		for (int i = index; i < length; i++) {
			char c = description.charAt(i);
			if (Character.isLetterOrDigit(c) || c == '.') {
				sb.append(c);
			} else {
				if (c == '#') {
					return null;
				}
				break;
			}
		}
		String targetType = sb.toString();
		return targetType;

	}

	private String readAndAdopt(Map<String, String> alternativeApiMapping, File sourceFile) throws IOException {
		StringBuilder fullDescription = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(sourceFile), UTF_8))) {
			readLines(alternativeApiMapping, sourceFile, fullDescription, new LineResolver() {

				public String getNextLine() throws IOException {
					return reader.readLine();
				}
			});
		}
		return convertString(fullDescription.toString());
	}

	void readLines(Map<String, String> alternativeApiMapping, File sourceFile, StringBuilder fullDescription,
			LineResolver lineResolver) throws IOException {
		String line = "";
		boolean foundType = false;
		while ((line = lineResolver.getNextLine()) != null) {
			if (fullDescription.length() != 0) {
				fullDescription.append("\n");
			}
			if (!foundType) {
				if (line.trim().startsWith("<type")) {
					foundType = true;
					String name = StringUtils.substringBetween(line, "name=\"", "\"");
					if (name == null) {
						System.err.println("WARN:name=null for line:" + line);
					} else {
						/*
						 * we exclude gradle tooling here because of duplicates
						 * with api parts
						 */
						if (!name.startsWith("org.gradle.tooling")) {
							String shortName = FilenameUtils.getBaseName(sourceFile.getName());
							alternativeApiMapping.put(shortName, name);
						}
					}
				}
			}
			line = removeWhitespacesAndStars(line);
			fullDescription.append(line);
			fullDescription.append(' ');

		}
	}

	String convertString(String origin) {
		String line = origin;
		line = replaceJavaDocLinks(line);
		line = replaceJavaDocCode(line);
		line = replaceJavaDocParams(line);
		line = replaceJavaDocReturn(line);
		return line;
	}

	private abstract class ContentReplacer {

		public abstract String replace(String curlyContent);

	}

	private enum JavaDocState {
		JAVADOC_TAG_FOUND, CURLY_BRACKET_OPENED, CURLY_BRACKET_CLOSED, UNKNOWN
	}

	String replaceJavaDocTagInCurls(String line, String javaDocId, ContentReplacer replacer) {
		StringBuilder sb = new StringBuilder();
		JavaDocState state = JavaDocState.UNKNOWN;
		/* scan for first { found" */
		/*
		 * check if next is wanted javadoc, otherwise leafe state /* when in
		 * state - collect info until state }"
		 */
		StringBuilder curlyContent = new StringBuilder();
		StringBuilder curlyContentUnchanged = new StringBuilder();
		for (char c : line.toCharArray()) {
			if (c == '{') {
				curlyContent = new StringBuilder();
				curlyContentUnchanged = new StringBuilder();
				state = JavaDocState.CURLY_BRACKET_OPENED;
				curlyContentUnchanged.append(c);
				continue;

			} else if (c == '}') {
				curlyContentUnchanged.append(c);
				if (state == JavaDocState.JAVADOC_TAG_FOUND) {
					String replaced = replacer.replace(curlyContent.toString().trim());
					sb.append(replaced);
				} else {
					sb.append(curlyContentUnchanged);
				}
				curlyContent = new StringBuilder();
				curlyContentUnchanged = new StringBuilder();
				state = JavaDocState.CURLY_BRACKET_CLOSED;
				continue;
			}
			if (state == JavaDocState.CURLY_BRACKET_OPENED) {
				curlyContentUnchanged.append(c);
				if (curlyContent.length() == 0) {
					if (!Character.isWhitespace(c)) { // no leading whitespaces
														// after {
						curlyContent.append(c);
					}
				} else {
					curlyContent.append(c);
				}
			} else if (state == JavaDocState.JAVADOC_TAG_FOUND) {
				curlyContentUnchanged.append(c);
				if (Character.isWhitespace(c)) {
					if (curlyContent.length() == 0) {
						/* forget leading whitespaces */
						continue;
					}
				}
				curlyContent.append(c);
			}

			if (state == JavaDocState.CURLY_BRACKET_OPENED) {
				if (curlyContent.toString().equals(javaDocId)) {
					state = JavaDocState.JAVADOC_TAG_FOUND;
					curlyContent = new StringBuilder();
				}
			}
			if (state == JavaDocState.UNKNOWN || state == JavaDocState.CURLY_BRACKET_CLOSED) {
				sb.append(c);
			}

		}
		String result = sb.toString();
		return result;
	}

	/**
	 * Replace javadoc identifier tag and all rest of line
	 * 
	 * @param line
	 * @param javadocId
	 * @param replacer
	 * @return replaced string
	 */
	String replaceJavaDocTagAndTrailingParts(String xline, String javadocId, ContentReplacer replacer) {
		String result = xline;
		while(true){
			int index = result.indexOf(javadocId);
			if (index == -1) {
				return result;
			}
			int length = result.length();
			StringBuilder content = new StringBuilder();
			String before = StringUtils.substring(result, 0, index);
			boolean leadingWhiteSpaces = true;
			int pos = index + javadocId.length();
			while (pos < length) {
				char c = result.charAt(pos++);
				if (Character.isWhitespace(c)) {
					if (leadingWhiteSpaces) {
						continue;
					}
				}
				leadingWhiteSpaces = false;
				content.append(c);
			}
			String replaced = replacer.replace(content.toString());
			result = before + replaced;
		}
	}

	private String replaceJavaDocParams(String line) {
		// * @param msg asdfasfasf
		String replaced = replaceJavaDocTagAndTrailingParts(line, "@param", new ContentReplacer() {

			@Override
			public String replace(String content) {
				return "<br><b class='param'>param:</b>" + content;
			}
		});
		return replaced;

	}

	private String replaceJavaDocReturn(String line) {
		// * @return ...
		String replaced = replaceJavaDocTagAndTrailingParts(line, "@return", new ContentReplacer() {

			@Override
			public String replace(String content) {
				return "<br><b class='return'>returns:</b>" + content;
			}
		});
		return replaced;

	}

	private String replaceJavaDocCode(String line) {
		// * @return ...
		String replaced = replaceJavaDocTagInCurls(line, "@code", new ContentReplacer() {

			@Override
			public String replace(String content) {
				// return "<div class='code'>" + content + "</div>";
				return "<a href='" + HYPERLINK_TYPE_PREFIX + content + "'>" + content + "</a>";
			}
		});
		return replaced;

	}

	private String replaceJavaDocLinks(String line) {
		String replaced = replaceJavaDocTagInCurls(line, "@link", new ContentReplacer() {

			@Override
			public String replace(String content) {
				return "<a href='" + HYPERLINK_TYPE_PREFIX + content + "'>" + content + "</a>";
			}
		});
		return replaced;
	}

	String removeWhitespacesAndStars(String line) {
		StringBuilder sb = new StringBuilder();

		boolean firstNonWhitespaceWorked = false;
		for (char c : line.toCharArray()) {
			if (!firstNonWhitespaceWorked) {
				if (Character.isWhitespace(c)) {
					continue;
				}
				firstNonWhitespaceWorked = true;
				if (c == '*') {
					continue;
				}
				/* other first char will be appended */
			}
			sb.append(c);
		}
		String result = sb.toString();
		return result;
	}

	private void write(String changedSource, File newTargetFile) throws IOException {
		newTargetFile.getParentFile().mkdirs();
		try (BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(newTargetFile), UTF_8))) {
			bw.write(changedSource);
		}

	}

}
