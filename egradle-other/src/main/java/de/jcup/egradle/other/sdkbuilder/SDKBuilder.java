package de.jcup.egradle.other.sdkbuilder;

import static de.jcup.egradle.codeassist.dsl.DSLConstants.*;
import static java.nio.charset.StandardCharsets.*;

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
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.ApiMappingImporter;
import de.jcup.egradle.codeassist.dsl.DSLConstants;
import de.jcup.egradle.codeassist.dsl.FilesystemFileLoader;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Plugin;
import de.jcup.egradle.codeassist.dsl.Task;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.XMLMethod;
import de.jcup.egradle.codeassist.dsl.XMLPlugin;
import de.jcup.egradle.codeassist.dsl.XMLPlugins;
import de.jcup.egradle.codeassist.dsl.XMLPluginsExporter;
import de.jcup.egradle.codeassist.dsl.XMLPluginsImporter;
import de.jcup.egradle.codeassist.dsl.XMLTask;
import de.jcup.egradle.codeassist.dsl.XMLTasks;
import de.jcup.egradle.codeassist.dsl.XMLTasksExporter;
import de.jcup.egradle.codeassist.dsl.XMLType;
import de.jcup.egradle.codeassist.dsl.XMLTypeExporter;
import de.jcup.egradle.codeassist.dsl.XMLTypeImporter;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLTypeProvider;
import de.jcup.egradle.sdk.SDKInfo;

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

	public static File PARENT_OF_RES = new File("egradle-other/src/main/res/");
	static {
		if (!PARENT_OF_RES.exists()) {
			/*
			 * fall back - so sdk builder could be run from gradle root project as well
			 * via gradle from root project.
			 */
			PARENT_OF_RES = new File("src/main/res/");
		}
	}
	public static void main(String[] args) throws IOException {
		SDKBuilder builder = new SDKBuilder("./../../gradle/subprojects/docs");
		File srcMainResTarget = new File("./../egradle-plugin-sdk/src/main/res/");
		builder.buildSDK(srcMainResTarget, "3.0");
	}

	public static File createUserHomeTargetRoot() {
		String userHome = System.getProperty("user.home");
		File targetRootDirectory = new File(userHome, ".egradle");
		return targetRootDirectory;
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

	private XMLTypeImporter typeImporter = new XMLTypeImporter();
	private XMLTypeExporter typeExporter = new XMLTypeExporter();

	XMLPluginsImporter pluginsImporter = new XMLPluginsImporter();
	XMLPluginsExporter pluginsExporter = new XMLPluginsExporter();

	public SDKBuilder(String pathToData) throws IOException {
		gradleEGradleDSLRootFolder = new File(pathToData, "/build/src-egradle/egradle-dsl");
		gradleOriginPluginsFile = new File(pathToData, "/src/docs/dsl/plugins.xml");
		gradleOriginMappingFile = new File(pathToData, "/build/generated-resources/main/api-mapping.txt");

		assertFileExists(gradleOriginPluginsFile);
		assertFileExists(gradleOriginMappingFile);
		assertDirectoryAndExists(gradleEGradleDSLRootFolder);
	}

	public SDKBuilderContext buildSDK(File targetRootDirectory, String gradleVersion) throws IOException {
		SDKBuilderContext sDKBuilderContext = createBuilderContext(targetRootDirectory, gradleVersion);


		handleApiMappingAndTargetEstimation(sDKBuilderContext);
		handlePlugins(sDKBuilderContext);

		startTaskDataEstimation(sDKBuilderContext);

		writeTasksFile(sDKBuilderContext);

		System.out.println("- info:" + sDKBuilderContext.getInfo());
		System.out.println("generated into:" + sDKBuilderContext.targetPathDirectory.getCanonicalPath());
		
		
		sDKBuilderContext.writeSDKInfo();
		System.out.println("DONE");
		return sDKBuilderContext;
	}

	private SDKBuilderContext createBuilderContext(File targetRootDirectory, String gradleVersion) throws IOException {
		SDKBuilderContext sDKBuilderContext = new SDKBuilderContext();
		
		sDKBuilderContext.sdkInfo.setCreationDate(new Date());
		sDKBuilderContext.sdkInfo.setGradleVersion(gradleVersion);
		
		File sourceParentDirectory = new File(gradleEGradleDSLRootFolder, gradleVersion);
		assertDirectoryAndExists(sourceParentDirectory);

		/* healthy check: */
		File healthCheck = new File(sourceParentDirectory, "org/gradle/api/Project.xml");
		if (!healthCheck.exists()) {
			throw new FileNotFoundException("The generated source for org.gradle.api.Project is not found at:\n"
					+ healthCheck.getCanonicalPath()
					+ "\nEither your path or version is incorrect or you forgot to generate...");
		}
		File targetPathDirectory = sDKBuilderContext.createTargetFile(targetRootDirectory);
		if (targetPathDirectory.exists()) {
			System.out.println(
					"Target directory exists - will be deleted before:" + targetPathDirectory.getCanonicalPath());
			FileUtils.deleteDirectory(targetPathDirectory);
		}
		targetPathDirectory.mkdirs();
		sDKBuilderContext.sourceParentDirectory = sourceParentDirectory;
		sDKBuilderContext.targetPathDirectory = targetPathDirectory;
		System.out.println("start generation into:" + sDKBuilderContext.targetPathDirectory.getCanonicalPath());
		
		sDKBuilderContext.sdkInfoFile=new File(targetPathDirectory,SDKInfo.FILENAME);
		return sDKBuilderContext;
	}

	

	private SDKBuilderContext handleApiMappingAndTargetEstimation(SDKBuilderContext sDKBuilderContext) throws IOException {
		System.out.println("- copy api mappings");
		File sourceParentDirectory = sDKBuilderContext.sourceParentDirectory;
		File targetPathDirectory = sDKBuilderContext.targetPathDirectory;
		FileUtils.copyFile(gradleOriginMappingFile, new File(targetPathDirectory, gradleOriginMappingFile.getName()));

		System.out.println("- inspect files and generate targets");
		/*
		 * create alternative api-mapping because e.g EclipseWTP is not listed
		 * in orgin mapping file!
		 */
		Map<String, String> alternativeApiMapping = new TreeMap<>();
		inspectFilesAdoptAndGenerateTarget(alternativeApiMapping, sourceParentDirectory, targetPathDirectory,
				sDKBuilderContext);
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
		return sDKBuilderContext;
	}

	private void handlePlugins(SDKBuilderContext sDKBuilderContext) throws IOException, FileNotFoundException {
		System.out.println("- adopt plugins.xml");
		File targetXMLPluginsFile = new File(sDKBuilderContext.targetPathDirectory, gradleOriginPluginsFile.getName());
		XMLPlugins xmlPlugins = null;
		try (FileInputStream fis = new FileInputStream(gradleOriginPluginsFile)	) {
			xmlPlugins = pluginsImporter.importPlugins(fis);
		}
		
		Set<Plugin> standardPlugins = xmlPlugins.getPlugins();
		for (Plugin standardPlugin: standardPlugins){
			String standardId = standardPlugin.getId();
			if (standardId==null){
				/* TODO ATR,16.02.2017: use a schema and make id mandatory instead of this!*/
				throw new IllegalStateException("found standard plugin with id NULL");
			}
		}
		
		XMLPlugins alternativeXMLPugins = null;
		File alternativePluginsFile = new File(PARENT_OF_RES,"sdkbuilder/override/gradle/"+sDKBuilderContext.sdkInfo.getGradleVersion()+"/alternative-plugins.xml");
		if (!alternativePluginsFile.exists()){
			System.err.println("- WARN::alternative plugins file does not exists:"+alternativePluginsFile);
		}else{
			try (FileInputStream fis = new FileInputStream(alternativePluginsFile)	) {
				alternativeXMLPugins = pluginsImporter.importPlugins(fis);
			}
			Set<Plugin> alternativePlugins = alternativeXMLPugins.getPlugins();
			
			for (Plugin alternativePlugin: alternativePlugins){
				String alternativeId = alternativePlugin.getId();
				if (alternativeId==null){
					/* TODO ATR,16.02.2017: use a schema and make id mandatory instead of this!*/
					throw new IllegalStateException("found alternative plugin with id NULL");
				}
				XMLPlugin alternativeXmlPlugin = (XMLPlugin) alternativePlugin;
				String description = alternativeXmlPlugin.getDescription();
				if (description==null){
					description+="";
				}
				alternativeXmlPlugin.setDescription(description+"(alternative)");
				standardPlugins.add(alternativePlugin);
			}
			
		}
		
		try (FileOutputStream outputStream = new FileOutputStream(targetXMLPluginsFile)) {
			pluginsExporter.exportPlugins(xmlPlugins, outputStream);
		}
		System.out.println("- written:" + targetXMLPluginsFile);
	}

	private void writeTasksFile(SDKBuilderContext sDKBuilderContext) throws IOException {
		XMLTasksExporter exporter = new XMLTasksExporter();
		File outputFile = new File(sDKBuilderContext.targetPathDirectory, "tasks.xml");
		XMLTasks tasks = new XMLTasks();
		Set<Task> xmlTasks = tasks.getTasks();
		for (String key : sDKBuilderContext.tasks.keySet()) {
			if (key.indexOf("org.gradle") != -1) {
				/* we only show tasks from API */
				if (key.indexOf("org.gradle.api") == -1) {
					continue;
				}
			}
			if (key.indexOf("Abstract") != -1) {
				/* we ignore abstract tasks */
				continue;
			}
			Type taskType = sDKBuilderContext.tasks.get(key);

			XMLTask task = new XMLTask();
			task.setType(taskType);
			task.setTypeAsString(taskType.getName());
			task.setName(taskType.getShortName().toLowerCase());

			xmlTasks.add(task);
		}

		try (FileOutputStream fos = new FileOutputStream(outputFile)) {
			exporter.exportTasks(tasks, fos);
		}

	}

	private void startTaskDataEstimation(SDKBuilderContext sDKBuilderContext) {
		/* now load the xml files as type data - and inspect all descriptions */
		System.out.println("- start task data estimation");
		XMLTypeImporter typeImporter = new XMLTypeImporter();
		XMLPluginsImporter pluginsImporter = new XMLPluginsImporter();
		ApiMappingImporter apiMappingImporter = new ApiMappingImporter();
		FilesystemFileLoader loader = new FilesystemFileLoader(typeImporter, pluginsImporter, apiMappingImporter);
		loader.setDSLFolder(sDKBuilderContext.targetPathDirectory);
		GradleDSLTypeProvider provider = new GradleDSLTypeProvider(loader);
		for (String typeName : sDKBuilderContext.allTypes) {
			tryToResolveTask(sDKBuilderContext, provider, typeName);
		}
	}

	/**
	 * Currently tasks.xml is only for information. but coulde be used in future for task
	 * type resolving. 
	 * @param sDKBuilderContext
	 * @param provider
	 * @param typeName
	 */
	private void tryToResolveTask(SDKBuilderContext sDKBuilderContext, GradleDSLTypeProvider provider, String typeName) {

		Type type = provider.getType(typeName);
		if (type == null) {
			throw new IllegalArgumentException("typeAsString:" + typeName + ", type:" + type + " is null!!?");
		}
		String typeAsString = type.getName();

		/*
		 * FIXME ATR, 12.02.2017: problematic, not all data avialbel - e.g.
		 * AbstractTAsk not inside, no interfaces like org.gradle.api.Task
		 * available etc.
		 */
		boolean isTask = false;
		isTask = type.isDescendantOf("org.gradle.api.DefaultTask)");
		// isTask=isTask ||
		// type.isDescendantOf("org.gradle.api.tasks.AbstractCopyTask");
		// isTask=isTask ||
		// type.isDescendantOf("org.gradle.api.tasks.AbstractExecTask");
		/*
		 * FIXME ATR, 13.02.2017: change this!!! this is ultra ugly implemented
		 * Either use ASM or do it by reflection on runtime, but every task
		 * (everything implementing the interface )must be fetched!
		 */
		// isTask=isTask
		// type.isDescendantOf("org.gradle.api.tasks.AbstractExecTask");
		if (!isTask) {
			if (typeAsString.startsWith("org.gradle") && typeAsString.endsWith("Task")) {
				isTask = true;
			}
		}
		if (!isTask) {
			for (String acceptedAsTask : sDKBuilderContext.tasks.keySet()) {
				if (type.isDescendantOf(acceptedAsTask)) {
					isTask = true;
					break;
				}
			}
		}
		if (isTask) {
			/*
			 * FIXME ATR, 12.02.2017: determine reason for type - means plugin.
			 * necessary for future
			 */
			sDKBuilderContext.tasks.put(type.getName(), type);
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

	private void inspectFilesAdoptAndGenerateTarget(Map<String, String> alternativeApiMapping, File sourceDir,
			File targetDir, SDKBuilderContext sDKBuilderContext) throws IOException {
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
				inspectFilesAdoptAndGenerateTarget(alternativeApiMapping, newSourceFile, newTargetDir, sDKBuilderContext);
			} else if (newSourceFile.isFile()) {
				File newTargetFile = new File(targetDir, name);

				String changedSource = readAndAdopt(alternativeApiMapping, newSourceFile);
				write(changedSource, newTargetFile);

				/* new TargetFile is now written */

				boolean ignore = false;
				String targetFileName = newTargetFile.getName();
				ignore = ignore | targetFileName.endsWith("plugins.xml");

				if (!ignore) {
					startDelegateTargetEstimation(newTargetFile, sDKBuilderContext);
				}
			}
		}
	}

	private void startDelegateTargetEstimation(File newTargetFile, SDKBuilderContext sDKBuilderContext)
			throws IOException, FileNotFoundException {
		try {
			XMLType type = null;

			try (FileInputStream inputStream = new FileInputStream(newTargetFile)) {
				type = typeImporter.importType(inputStream);

			}
			if (type == null) {
				throw new IllegalStateException("was not able to read type:" + newTargetFile);
			}
			estimateDelegateTargets(type, sDKBuilderContext);
			sDKBuilderContext.allTypes.add(type.getName());
			try (FileOutputStream outputStream = new FileOutputStream(newTargetFile)) {
				typeExporter.exportType(type, outputStream);
			}
		} catch (IOException e) {
			throw new IOException("Problems with file:" + newTargetFile.getAbsolutePath(), e);
		}
	}

	void estimateDelegateTargets(Type type) {
		estimateDelegateTargets(type, new SDKBuilderContext());
	}

	void estimateDelegateTargets(Type type, SDKBuilderContext sDKBuilderContext) {
		int problemCount = 0;
		StringBuilder problems = new StringBuilder();
		for (Method m : type.getMethods()) {
			if (!(m instanceof XMLMethod)) {
				continue;
			}
			sDKBuilderContext.methodAllCount++;
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
//			System.out.println("- WARN: type:" + type.getName()
//					+ " has following method without descriptions: has no description " + problems.toString());
			sDKBuilderContext.methodWithOutDescriptionCount += problemCount;
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
		String replacedJavaDocParts = replaceJavaDocParts(fullDescription.toString());
		replacedJavaDocParts = handleTypeLinksWithoutType(replacedJavaDocParts, sourceFile);
		return replacedJavaDocParts;
	}

	private static final String TYPE_PREFIX_WITHOUT_TYPE = DSLConstants.HYPERLINK_TYPE_PREFIX + "#";
	private static final Pattern PATTERN_TYPE_PREFIX_WITHOUT_TYPE = Pattern.compile(TYPE_PREFIX_WITHOUT_TYPE);

	String handleTypeLinksWithoutType(String replacedJavaDocParts, File sourceFile) {
		if (sourceFile == null) {
			return replacedJavaDocParts;
		}
		if (replacedJavaDocParts.indexOf(TYPE_PREFIX_WITHOUT_TYPE) == -1) {
			return replacedJavaDocParts;
		}
		String typeName = FilenameUtils.getBaseName(sourceFile.getName());
		Matcher matcher = PATTERN_TYPE_PREFIX_WITHOUT_TYPE.matcher(replacedJavaDocParts);
		replacedJavaDocParts = matcher.replaceAll(DSLConstants.HYPERLINK_TYPE_PREFIX + typeName + "#");
		return replacedJavaDocParts;
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

	String replaceJavaDocParts(String origin) {
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
		while (true) {
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
	/*
	 * FIXME ATR, 06.02.2017: @see xyz(xxx) should be converted to <a
	 * href="type://xyz(xxx)>!!!
	 */

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
				return "<br><br><b class='return'>returns:</b>" + content;
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
