package de.jcup.egradle.sdk.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import de.jcup.egradle.codeassist.dsl.ApiMappingImporter;
import de.jcup.egradle.codeassist.dsl.FilesystemFileLoader;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.XMLPlugins;
import de.jcup.egradle.codeassist.dsl.XMLPluginsExporter;
import de.jcup.egradle.codeassist.dsl.XMLPluginsImporter;
import de.jcup.egradle.codeassist.dsl.XMLTypeExporter;
import de.jcup.egradle.codeassist.dsl.XMLTypeImporter;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLTypeProvider;
import de.jcup.egradle.sdk.SDKInfo;
import de.jcup.egradle.sdk.builder.model.OriginXMLDSlTypeInfoImporter;
import de.jcup.egradle.sdk.internal.XMLSDKInfo;
import de.jcup.egradle.sdk.internal.XMLSDKInfoExporter;

public class SDKBuilderContext {
	
	public File PARENT_OF_RES = new File("egradle-other/src/main/res/");
		
	public Map<String, String> alternativeApiMapping = new TreeMap<>();
	public File sdkInfoFile;
	public XMLSDKInfo sdkInfo = new XMLSDKInfo();
	
	public File gradleEGradleDSLRootFolder;
	public File gradleOriginPluginsFile;
	public File gradleOriginMappingFile;
	public File gradleProjectFolder;
	public File gradleSubProjectDocsFolder;
	
	/**
	 * Source directory where 
	 */
	public File sourceParentDirectory;
	public File targetPathDirectory;
	public Map<String, Type> tasks = new TreeMap<>();
	public int methodWithOutDescriptionCount;
	public int methodAllCount;

	public Map<String,File> allTypes = new TreeMap<>();
	public File alternativeAPiMappingFile;

	public XMLTypeImporter typeImporter = new XMLTypeImporter();
	public XMLTypeExporter typeExporter = new XMLTypeExporter();

	public XMLPluginsImporter pluginsImporter = new XMLPluginsImporter();
	public XMLPluginsExporter pluginsExporter = new XMLPluginsExporter();
	public ApiMappingImporter apiMappingImporter = new ApiMappingImporter();

	public OriginXMLDSlTypeInfoImporter originDslTypeInfoImporter = new OriginXMLDSlTypeInfoImporter();

	
	public XMLPlugins xmlPlugins;

	public XMLPlugins alternativeXMLPugins;

	public FilesystemFileLoader beforeGenerationLoader;

	public GradleDSLTypeProvider originGradleFilesProvider;
	
	/* only for test */
	public SDKBuilderContext(){
		if (!PARENT_OF_RES.exists()) {
			/*
			 * fall back - so sdk builder could be run from gradle root project
			 * as well via gradle from root project.
			 */
			PARENT_OF_RES = new File("src/main/res/");
		}
		
		beforeGenerationLoader = new FilesystemFileLoader(typeImporter, pluginsImporter, apiMappingImporter);
		originGradleFilesProvider = new GradleDSLTypeProvider(beforeGenerationLoader);

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
		
		beforeGenerationLoader.setDSLFolder(sourceParentDirectory);
		
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