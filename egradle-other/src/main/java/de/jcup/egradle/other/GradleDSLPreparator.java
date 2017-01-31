package de.jcup.egradle.other;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 * The egradle <a href="https://github.com/de-jcup/gradle">gradle fork</a> has 
 * special task called "dslEgradle".<br><br>
 * It is used to build DSL files usable by egradle code completion. But... there
 * is still work to do:
 * <ol>
 * <li>Descriptions are still javadoc like and must be changed</li>
 * <li>Each closure method parameter is problematic and must be replaced by corre</li>
 * <li></li>
 * </ol>
 * @author Albert Tregnaghi
 *
 */
public class GradleDSLPreparator {

	/* FIXME ATR, 20.01.2017: docbook does know the target! /gradle/buildSrc/src/main/groovy/org/gradle/build/docs/dsl/docbook/BlockDetailRenderer.java */
	public static void main(String[] args) throws IOException {
		new GradleDSLPreparator("./../../gradle/subprojects/docs").startTransformToUserHome("3.0");;
	}
	/* FIXME ATR, 31.01.2017: versioning- use version 0.1 and see it as EGradle-SDK.. the gradle version is done already inside the files in type element!
	 */

	private File gradleEGradleDSLRootFolder;
	private File gradleOriginPluginsFile;

	/**
	 * Only for tests
	 */
	GradleDSLPreparator() {
		
	}
	
	public GradleDSLPreparator(String pathToData) throws IOException {
		gradleEGradleDSLRootFolder = new File(pathToData,"/build/src-egradle/egradle-dsl");
		gradleOriginPluginsFile=new File(pathToData,"/src/docs/dsl/plugins.xml");
		
		assertFileExists(gradleOriginPluginsFile);
		assertDirectoryAndExists(gradleEGradleDSLRootFolder);
	}


	private void assertDirectoryAndExists(File folder) throws IOException {
		if (! folder.exists()){
			throw new FileNotFoundException(folder.getCanonicalPath()+ " does not exist!");
		}
		
		if (! folder.isDirectory()){
			throw new FileNotFoundException(folder.getCanonicalPath()+ " ist not a directory!");
		}
	}

	private void assertFileExists(File file) throws FileNotFoundException, IOException {
		if (! file.exists()){
			throw new FileNotFoundException(file.getCanonicalPath()+ " does not exist!");
		}
		if (! file.isFile()){
			throw new FileNotFoundException(file.getCanonicalPath()+ " ist not a file!");
		}
	}


	private void startTransformToUserHome(String version) throws IOException{
		File sourceParentDirectory = new File(gradleEGradleDSLRootFolder, version);
		FileUtils.copyFile(gradleOriginPluginsFile, new File(sourceParentDirectory,gradleOriginPluginsFile.getName()));
		assertDirectoryAndExists(sourceParentDirectory);
		
		/* healthy check: */
		File healthCheck = new File(sourceParentDirectory,"org/gradle/api/Project.xml");
		if (! healthCheck.exists()){
			throw new FileNotFoundException(
						"The generated source for org.gradle.api.Project is not found at:\n"+
						healthCheck.getCanonicalPath()+
						"\nEither your path or version is incorrect or you forgot to generate...");
		}
		String userHome = System.getProperty("user.home");
		File targetPathDirectory = new File(userHome,".egradle/dsl/gradle/"+version);
		if ( targetPathDirectory.exists()){
			System.out.println("Target directory exists - will be deleted before:"+targetPathDirectory.getCanonicalPath());
			FileUtils.deleteDirectory(targetPathDirectory);
		}
		System.out.println("start generation into:"+targetPathDirectory.getCanonicalPath());
		inspectFilesAdoptAndGenerateTarget(sourceParentDirectory,targetPathDirectory);
		System.out.println("DONE");
	}


	private void inspectFilesAdoptAndGenerateTarget(File sourceDir, File targetDir) throws IOException{
		for (File newSourceFile : sourceDir.listFiles()){
			String name = newSourceFile.getName();
			if (newSourceFile.isDirectory()){
				File newTargetDir = new File(targetDir,name);
				inspectFilesAdoptAndGenerateTarget(newSourceFile,newTargetDir);
			}else if (newSourceFile.isFile()){
				File newTargetFile = new File(targetDir,name);

				String changedSource = readAndAdopt(newSourceFile);
				write(changedSource,newTargetFile);
			}
		}
	}

	private String readAndAdopt(File sourceFile) throws IOException{
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))){
			String line = "";
			while ((line=reader.readLine())!=null){
				if (sb.length()!=0){
					sb.append("\n");
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

	private static final Pattern PATTERN_JAVADOC_LINK = Pattern.compile("(.*)\\{@link\\s*([0-9|a-z|A-Z|\\.]*)\\s*\\}");
	
	
	private String replaceJavaDocLinksWithHTMLLink(String line) {
		Matcher matcher = PATTERN_JAVADOC_LINK.matcher(line);
		if (matcher.matches()){
			String result =  matcher.replaceAll("$1<a href='type://$2'>$2</a>");
			return result;
		}else{
			return line;
		}
	}

	private String removeWhitespacesAndStars(String line) {
		StringBuilder sb = new StringBuilder();

		boolean firstNonWhitespaceWorked=false;
		for (char c: line.toCharArray()){
			if (!firstNonWhitespaceWorked){
				if (Character.isWhitespace(c)){
					continue;
				}
				firstNonWhitespaceWorked=true;
				if (c=='*'){
					continue;
				}
				/* other first char will be appended*/
			}
			sb.append(c);
		}
		String result = sb.toString();
		return result;
	}


	private void write(String changedSource, File newTargetFile) throws IOException{
		newTargetFile.getParentFile().mkdirs();
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(newTargetFile))){
			bw.write(changedSource);
		}
		
	}

}
