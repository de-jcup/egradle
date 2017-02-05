package de.jcup.egradle.other;

import java.awt.image.BufferedImageFilter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

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
	 * FIXME ATR, 31.01.2017: versioning- use version 0.1 and see it as
	 * EGradle-SDK.. the gradle version is done already inside the files in type
	 * element!
	 */

	private File gradleEGradleDSLRootFolder;
	private File gradleOriginPluginsFile;
	private File gradleOriginMappingFile;

	/**
	 * Only for tests
	 */
	SDKBuilder() {

	}

	public SDKBuilder(String pathToData) throws IOException {
		gradleEGradleDSLRootFolder = new File(pathToData, "/build/src-egradle/egradle-dsl");
		gradleOriginPluginsFile = new File(pathToData, "/src/docs/dsl/plugins.xml");
		gradleOriginMappingFile = new File(pathToData, "/build/generated-resources/main/api-mapping.txt");

		assertFileExists(gradleOriginPluginsFile);
		assertFileExists(gradleOriginMappingFile);
		assertDirectoryAndExists(gradleEGradleDSLRootFolder);
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

	private void startTransformToUserHome(String gradleVersion, String targetSDKVersion) throws IOException {
		File sourceParentDirectory = new File(gradleEGradleDSLRootFolder, gradleVersion);
		assertDirectoryAndExists(sourceParentDirectory);

		FileUtils.copyFile(gradleOriginPluginsFile, new File(sourceParentDirectory, gradleOriginPluginsFile.getName()));
		FileUtils.copyFile(gradleOriginMappingFile, new File(sourceParentDirectory, gradleOriginMappingFile.getName()));

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
		System.out.println("- inspect files and generate targets");
		/*
		 * create alternative api-mapping because e.g EclipseWTP is not listed
		 * in orgin mapping file!
		 */
		Map<String, String> alternativeApiMapping = new TreeMap<>();
		inspectFilesAdoptAndGenerateTarget(alternativeApiMapping, sourceParentDirectory, targetPathDirectory);
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

		System.out.println("DONE");
	}

	private void inspectFilesAdoptAndGenerateTarget(Map<String, String> alternativeApiMapping, File sourceDir,
			File targetDir) throws IOException {
		for (File newSourceFile : sourceDir.listFiles()) {
			String name = newSourceFile.getName();
			if (newSourceFile.isDirectory()) {
				File newTargetDir = new File(targetDir, name);
				inspectFilesAdoptAndGenerateTarget(alternativeApiMapping, newSourceFile, newTargetDir);
			} else if (newSourceFile.isFile()) {
				File newTargetFile = new File(targetDir, name);

				String changedSource = readAndAdopt(alternativeApiMapping, newSourceFile);
				write(changedSource, newTargetFile);
			}
		}
	}

	private String readAndAdopt(Map<String, String> alternativeApiMapping, File sourceFile) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
			String line = "";
			boolean foundType = false;
			while ((line = reader.readLine()) != null) {
				if (sb.length() != 0) {
					sb.append("\n");
				}
				if (!foundType) {
					if (line.trim().startsWith("<type")) {
						foundType = true;
						String name = StringUtils.substringBetween(line, "name=\"", "\"");
						if (name == null) {
							System.err.println("WARN:name=null for line:" + line);
						} else {
							/*
							 * we exclude gradle tooling here because of
							 * duplicates with api parts
							 */
							if (!name.startsWith("org.gradle.tooling")) {
								String shortName = FilenameUtils.getBaseName(sourceFile.getName());
								alternativeApiMapping.put(shortName, name);
							}
						}
					}
				}
				String adoptedLine = convertLine(line);
				sb.append(adoptedLine);

			}
		}
		return sb.toString();
	}

	String convertLine(String origin) {
		String line = removeWhitespacesAndStars(origin);
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
					String replaced = replacer.replace(curlyContent.toString());
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
				curlyContent.append(c);
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
	 * @param line
	 * @param javadocId
	 * @param replacer
	 * @return replaced string
	 */
	String replaceJavaDocTagAndTrailingParts(String line, String javadocId, ContentReplacer replacer){
		int index = line.indexOf(javadocId);
		if (index==-1){
			return line;
		}
		int length = line.length();
		StringBuilder content = new StringBuilder();
		String before = StringUtils.substring(line, 0, index);
		boolean leadingWhiteSpaces=true;
		int pos=index+javadocId.length();
		while (pos<length){
			char c = line.charAt(pos++);
			if (Character.isWhitespace(c)){
				if (leadingWhiteSpaces){
					continue;
				}
			}
			leadingWhiteSpaces=false;
			content.append(c);
		}
		String replaced = replacer.replace(content.toString());
		String result = before+replaced;
		return result;
	}

	/**
	 * Replace key value pair until whitespace or end. E.g. "@param name1 description " will replace "@param name1" only
	 * @param line
	 * @param javadocId
	 * @param replacer
	 * @param onlyEndTermination when true trailing whitespaces are not terminating
	 * @return replaced string
	 * @deprecated does not work correct currently
	 */
	String replaceJavaDocKeyValue(String line, String javadocId, ContentReplacer replacer, boolean onlyEndTermination){
		int index = line.indexOf(javadocId);
		if (index==-1){
			return line;
		}
		int length = line.length();
		StringBuilder content = new StringBuilder();
		String before = StringUtils.substring(line, 0, index);
		boolean leadingWhiteSpaces=true;
		int pos=index+javadocId.length();
		while (pos<length){
			char c = line.charAt(pos++);
			if (Character.isWhitespace(c)){
				if (onlyEndTermination || leadingWhiteSpaces){
					continue;
				}else{
					/* no leading but another whitespace so end reached */
					break;
				}
			}
			leadingWhiteSpaces=false;
			content.append(c);
		}
		String remaining="";
		if (pos<length){
			remaining=StringUtils.substring(line, pos);
		}
		String replaced = replacer.replace(content.toString());
		String result = before+replaced+remaining;
		return result;
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
				//return "<div class='code'>" + content + "</div>";
				return "<a href='type://" + content + "'>" + content + "</a>";
			}
		});
		return replaced;

	}

	private String replaceJavaDocLinks(String line) {
		String replaced = replaceJavaDocTagInCurls(line, "@link", new ContentReplacer() {

			@Override
			public String replace(String content) {
				return "<a href='type://" + content + "'>" + content + "</a>";
			}
		});
		return replaced;
	}

	private String removeWhitespacesAndStars(String line) {
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
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(newTargetFile))) {
			bw.write(changedSource);
		}

	}

}
