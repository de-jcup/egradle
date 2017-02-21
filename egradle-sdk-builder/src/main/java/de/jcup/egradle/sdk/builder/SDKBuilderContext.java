package de.jcup.egradle.sdk.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.sdk.SDKInfo;
import de.jcup.egradle.sdk.internal.XMLSDKInfo;
import de.jcup.egradle.sdk.internal.XMLSDKInfoExporter;

public class SDKBuilderContext {
	File sdkInfoFile;
	XMLSDKInfo sdkInfo = new XMLSDKInfo();
	
	File gradleEGradleDSLRootFolder;
	File gradleOriginPluginsFile;
	File gradleOriginMappingFile;
	File gradleProjectFolder;
	File gradleSubProjectDocsFolder;
	
	/**
	 * Source directory where 
	 */
	File sourceParentDirectory;
	File targetPathDirectory;
	Map<String, Type> tasks = new TreeMap<>();
	int methodWithOutDescriptionCount;
	int methodAllCount;

	Map<String,File> allTypes = new TreeMap<>();
	File alternativeAPiMappingFile;

	/* only for test */
	SDKBuilderContext(){
	}
	
	public SDKBuilderContext(String pathToradleProjectFolder, File targetRootDirectory, String gradleVersion) throws IOException {
		gradleProjectFolder = new File(pathToradleProjectFolder);
		targetPathDirectory =createTargetFile(targetRootDirectory);
		
		if (! this.gradleProjectFolder.exists()){
			throw new IllegalArgumentException("gradle project folder does not exist:"+gradleProjectFolder);
		}
		if (! this.gradleProjectFolder.isDirectory()){
			throw new IllegalArgumentException("gradle project folder is not a directory ?!?!?:"+gradleProjectFolder);
		}
		gradleSubProjectDocsFolder= new File(gradleProjectFolder,"subprojects/docs");
		
		gradleEGradleDSLRootFolder = new File(gradleSubProjectDocsFolder, "/build/src-egradle/egradle-dsl");
		gradleOriginPluginsFile = new File(gradleSubProjectDocsFolder, "/src/docs/dsl/plugins.xml");
		gradleOriginMappingFile = new File(gradleSubProjectDocsFolder, "/build/generated-resources/main/api-mapping.txt");

		assertFileExists(gradleOriginPluginsFile);
		assertFileExists(gradleOriginMappingFile);
		assertDirectoryAndExists(gradleEGradleDSLRootFolder);
		
		sdkInfo.setCreationDate(new Date());
		sdkInfo.setGradleVersion(gradleVersion);
		
		sourceParentDirectory = new File(gradleEGradleDSLRootFolder, gradleVersion);
		assertDirectoryAndExists(sourceParentDirectory);

		/* healthy check: */
		File healthCheck = new File(sourceParentDirectory, "org/gradle/api/Project.xml");
		if (!healthCheck.exists()) {
			throw new FileNotFoundException("The generated source for org.gradle.api.Project is not found at:\n"
					+ healthCheck.getCanonicalPath()
					+ "\nEither your path or version is incorrect or you forgot to generate...");
		}
		
		
		System.out.println("start generation into:" + targetPathDirectory.getCanonicalPath());
		
		sdkInfoFile=new File(targetPathDirectory,SDKInfo.FILENAME);
		alternativeAPiMappingFile=new File(targetPathDirectory, "alternative-api-mapping.txt");
	}

	public String getInfo() {
		double missingDescriptionPercent = 0;
		if (methodWithOutDescriptionCount != 0 && methodAllCount != 0) {
			double onePercent = methodAllCount / 100;
			missingDescriptionPercent = methodWithOutDescriptionCount / onePercent;
		}
		return "Methods all:" + methodAllCount + " - missing descriptions:" + methodWithOutDescriptionCount + " ="
				+ missingDescriptionPercent + "%";
	}
	
	private File createTargetFile(File targetRootDirectory) {
		return new File(targetRootDirectory, "sdk/");
	}
	
	public void writeSDKInfo() throws IOException{
		
		try(FileOutputStream stream = new FileOutputStream(sdkInfoFile)){

			XMLSDKInfoExporter exporter = new XMLSDKInfoExporter();
			exporter.exportSDKInfo(sdkInfo, stream);
			System.out.println("- written sdk info file:"+sdkInfoFile);
		}
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
}