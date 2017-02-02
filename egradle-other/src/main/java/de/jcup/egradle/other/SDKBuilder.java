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
		File targetPathDirectory = new File(userHome, ".egradle/sdk/"+targetSDKVersion+"/gradle/");
		if (targetPathDirectory.exists()) {
			System.out.println(
					"Target directory exists - will be deleted before:" + targetPathDirectory.getCanonicalPath());
			FileUtils.deleteDirectory(targetPathDirectory);
		}
		System.out.println("start generation into:" + targetPathDirectory.getCanonicalPath());
		/* create alternative api-mapping because e.g EclipseWTP is not listed in orgin mapping file!*/
		Map<String,String> alternativeApiMapping = new TreeMap<>();
		inspectFilesAdoptAndGenerateTarget(alternativeApiMapping, sourceParentDirectory, targetPathDirectory);
		System.out.println("- generate alternative api mapping file");
		StringBuilder sb = new StringBuilder();
		boolean first=true;
		for (String shortName: alternativeApiMapping.keySet()){
			if(first){
				first=false;
			}else{
				sb.append("\n");
			}
			sb.append(shortName);
			sb.append(':');
			sb.append(alternativeApiMapping.get(shortName));
			sb.append(';');
		}
		File alternativeApiMappingFile = new File(targetPathDirectory,"alternative-api-mapping.txt");
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(alternativeApiMappingFile))){
			bw.write(sb.toString());
		}
		
		System.out.println("DONE");
	}

	private void inspectFilesAdoptAndGenerateTarget(Map<String,String> alternativeApiMapping, File sourceDir, File targetDir) throws IOException {
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
			boolean foundType=false;
			while ((line = reader.readLine()) != null) {
				if (sb.length() != 0) {
					sb.append("\n");
				}
				if (!foundType){
					if (line.trim().startsWith("<type")){
						foundType=true;
						String name = StringUtils.substringBetween(line, "name=\"", "\"");
						if (name==null){
							System.err.println("WARN:name=null for line:"+line);
						}else{
							String shortName = FilenameUtils.getBaseName(sourceFile.getName());
							alternativeApiMapping.put(shortName, name);
						}
					}
				}
				String adoptedLine = convertLine(line);
				sb.append(adoptedLine);

			}
		}
		return sb.toString();
	}

	String convertLine(String line) {
		String result = removeWhitespacesAndStars(line);
		result = replaceJavaDocLinksWithHTMLLink(result);
		return result;
	}


	private abstract class ContentReplacer {

		public abstract String replace(String curlyContent);

	}

	private enum JavaDocState {
		JAVADOC_TAG_FOUND, CURLY_BRACKET_OPENED, CURLY_BRACKET_CLOSED, UNKNOWN
	}

	private String replaceJavaDoc(String line, String javaDocId, ContentReplacer replacer) {
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
			if (state == JavaDocState.CURLY_BRACKET_OPENED){
				curlyContentUnchanged.append(c);
				curlyContent.append(c);
			}else if (state == JavaDocState.JAVADOC_TAG_FOUND){
				curlyContentUnchanged.append(c);
				if (Character.isWhitespace(c)){
					if (curlyContent.length()==0){
						/* forget leading whitespaces*/
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
			if (state == JavaDocState.UNKNOWN || state==JavaDocState.CURLY_BRACKET_CLOSED){
				sb.append(c);
			}
				

		}
		String result = sb.toString();
		return result;
	}

	private String replaceJavaDocLinksWithHTMLLink(String line) {
		String replaced = replaceJavaDoc(line, "@link", new ContentReplacer() {

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
