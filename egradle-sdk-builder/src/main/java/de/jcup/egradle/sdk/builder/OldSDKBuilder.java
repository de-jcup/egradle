package de.jcup.egradle.sdk.builder;

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
import java.util.Iterator;
import java.util.List;
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
import de.jcup.egradle.codeassist.dsl.MethodUtils;
import de.jcup.egradle.codeassist.dsl.ModifiableMethod;
import de.jcup.egradle.codeassist.dsl.ModifiableProperty;
import de.jcup.egradle.codeassist.dsl.Parameter;
import de.jcup.egradle.codeassist.dsl.Plugin;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Task;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.TypeReference;
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
import de.jcup.egradle.sdk.builder.model.OriginXMLDSlTypeInfoImporter;
import de.jcup.egradle.sdk.builder.model.XMLDSLMethodInfo;
import de.jcup.egradle.sdk.builder.model.XMLDSLPropertyInfo;
import de.jcup.egradle.sdk.builder.model.XMLDSLTypeDocumentation;
import de.jcup.egradle.sdk.builder.model.XMLDSLTypeOverrides;
import de.jcup.egradle.sdk.builder.model.XMLDSLTypeOverridesImporter;
import de.jcup.egradle.sdk.builder.util.LineResolver;
import de.jcup.egradle.sdk.internal.XMLSDKInfoExporter;

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
public class OldSDKBuilder {

	public static File PARENT_OF_RES = new File("egradle-other/src/main/res/");
	static {
		if (!PARENT_OF_RES.exists()) {
			/*
			 * fall back - so sdk builder could be run from gradle root project
			 * as well via gradle from root project.
			 */
			PARENT_OF_RES = new File("src/main/res/");
		}
	}

	public static void main(String[] args) throws IOException {
		OldSDKBuilder builder = new OldSDKBuilder("./../../gradle");
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

	/**
	 * Only for tests
	 */
	OldSDKBuilder() {

	}
	OriginXMLDSlTypeInfoImporter originDslTypeInfoImporter = new OriginXMLDSlTypeInfoImporter();

	XMLTypeImporter typeImporter = new XMLTypeImporter();
	XMLTypeExporter typeExporter = new XMLTypeExporter();

	XMLPluginsImporter pluginsImporter = new XMLPluginsImporter();
	XMLPluginsExporter pluginsExporter = new XMLPluginsExporter();
	ApiMappingImporter apiMappingImporter = new ApiMappingImporter();
	
	private String pathTorGradleRootProjectFolder;

	public OldSDKBuilder(String pathTorGradleRootProjectFolder) {
		this.pathTorGradleRootProjectFolder = pathTorGradleRootProjectFolder;
	}

	public SDKBuilderContext buildSDK(File targetRootDirectory, String gradleVersion) throws IOException {
		SDKBuilderContext context = new SDKBuilderContext(pathTorGradleRootProjectFolder, targetRootDirectory,
				gradleVersion);

		prepareSDKFolder(context);
		
		/* be aware: at this moment the alternative mappings are not created and the source is not SDK but gradle */
		FilesystemFileLoader beforeGenerationLoader = new FilesystemFileLoader(typeImporter, pluginsImporter, apiMappingImporter);
		beforeGenerationLoader.setDSLFolder(context.sourceParentDirectory);
		GradleDSLTypeProvider originGradleFilesProvider = new GradleDSLTypeProvider(beforeGenerationLoader);
		
		XMLDSLTypeOverridesImporter overridesImporter = new XMLDSLTypeOverridesImporter();
		File alternativeOverriesFile = new File(PARENT_OF_RES,
				"sdkbuilder/override/gradle/" + context.sdkInfo.getGradleVersion() + "/alternative-delegatesTo.xml");
		
		XMLDSLTypeOverrides overrides = null;
		try(FileInputStream fis = new FileInputStream(alternativeOverriesFile)){
			overrides = overridesImporter.importOverrides(fis);
		}
		inspectAndMapFilesAndAdopt(originGradleFilesProvider,overrides, context);
		handlePlugins(context);
		
		/* generation is done now*/
		
		FilesystemFileLoader afterGenerationLoader = new FilesystemFileLoader(typeImporter, pluginsImporter, apiMappingImporter);
		afterGenerationLoader.setDSLFolder(context.targetPathDirectory);
		GradleDSLTypeProvider afterGenerationProvider = new GradleDSLTypeProvider(afterGenerationLoader);
		
		startTaskDataEstimation(afterGenerationProvider, context);
		estimateStillMissingDelegationTargets_byJavaDoc(afterGenerationProvider,context);
		
		exportAllTypesAgain(context, afterGenerationProvider);
		
		writeTasksFile(context);

		System.out.println("- info:" + context.getInfo());
		System.out.println("generated into:" + context.targetPathDirectory.getCanonicalPath());

		
		writeSDKInfo(context);
		System.out.println("DONE");
		return context;
	}
	
	public void writeSDKInfo(SDKBuilderContext context) throws IOException{
		
		try(FileOutputStream stream = new FileOutputStream(context.sdkInfoFile)){

			XMLSDKInfoExporter exporter = new XMLSDKInfoExporter();
			exporter.exportSDKInfo(context.sdkInfo, stream);
			System.out.println("- written sdk info file:"+context.sdkInfoFile);
		}
	}

	private void prepareSDKFolder(SDKBuilderContext context) throws IOException {
		/* delete old sdk */
		if (context.targetPathDirectory.exists()) {
			System.out.println(
					"Target directory exists - will be deleted before:" + context.targetPathDirectory.getCanonicalPath());
			FileUtils.deleteDirectory(context.targetPathDirectory);
		}
		context.targetPathDirectory.mkdirs();
	}

	private void exportAllTypesAgain(SDKBuilderContext context, GradleDSLTypeProvider provider)
			throws IOException, FileNotFoundException {
		for (String typeName : context.originTypeNameToOriginFileMapping.keySet()) {
			Type type=provider.getType(typeName);
			try(FileOutputStream fos = new FileOutputStream(context.originTypeNameToOriginFileMapping.get(typeName))){
				typeExporter.exportType((XMLType) type, fos);
			}
		}
	}

	


	private SDKBuilderContext inspectAndMapFilesAndAdopt(GradleDSLTypeProvider originGradleFilesProvider, XMLDSLTypeOverrides overrides, SDKBuilderContext context) throws IOException {
		System.out.println("- copy api mappings");
		File sourceParentDirectory = context.sourceParentDirectory;
		File targetPathDirectory = context.targetPathDirectory;
		FileUtils.copyFile(context.gradleOriginMappingFile,
				new File(targetPathDirectory, context.gradleOriginMappingFile.getName()));

		System.out.println("- inspect files and generate targets");
		/*
		 * create alternative api-mapping because e.g EclipseWTP is not listed
		 * in orgin mapping file!
		 */
		Map<String, String> alternativeApiMapping = new TreeMap<>();
		inspectFilesAndAdopt(overrides, originGradleFilesProvider,alternativeApiMapping, sourceParentDirectory, targetPathDirectory, context);
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
		File alternativeApiMappingFile = context.alternativeAPiMappingFile;
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(alternativeApiMappingFile))) {
			bw.write(sb.toString());
		}
		return context;
	}

	private void handlePlugins(SDKBuilderContext context) throws IOException, FileNotFoundException {
		System.out.println("- adopt plugins.xml");
		File targetXMLPluginsFile = new File(context.targetPathDirectory, context.gradleOriginPluginsFile.getName());
		XMLPlugins xmlPlugins = null;
		try (FileInputStream fis = new FileInputStream(context.gradleOriginPluginsFile)) {
			xmlPlugins = pluginsImporter.importPlugins(fis);
		}

		Set<Plugin> standardPlugins = xmlPlugins.getPlugins();
		for (Plugin standardPlugin : standardPlugins) {
			String standardId = standardPlugin.getId();
			if (standardId == null) {
				/*
				 * TODO ATR,16.02.2017: use a schema and make id mandatory
				 * instead of this!
				 */
				throw new IllegalStateException("found standard plugin with id NULL");
			}
		}

		XMLPlugins alternativeXMLPugins = null;
		File alternativePluginsFile = new File(PARENT_OF_RES,
				"sdkbuilder/override/gradle/" + context.sdkInfo.getGradleVersion() + "/alternative-plugins.xml");
		if (!alternativePluginsFile.exists()) {
			System.err.println("- WARN::alternative plugins file does not exists:" + alternativePluginsFile);
		} else {
			try (FileInputStream fis = new FileInputStream(alternativePluginsFile)) {
				alternativeXMLPugins = pluginsImporter.importPlugins(fis);
			}
			Set<Plugin> alternativePlugins = alternativeXMLPugins.getPlugins();

			for (Plugin alternativePlugin : alternativePlugins) {
				String alternativeId = alternativePlugin.getId();
				if (alternativeId == null) {
					/*
					 * TODO ATR,16.02.2017: use a schema and make id mandatory
					 * instead of this!
					 */
					throw new IllegalStateException("found alternative plugin with id NULL");
				}
				XMLPlugin alternativeXmlPlugin = (XMLPlugin) alternativePlugin;
				String description = alternativeXmlPlugin.getDescription();
				if (description == null) {
					description += "";
				}
				alternativeXmlPlugin.setDescription(description + "(alternative)");
				standardPlugins.add(alternativePlugin);
			}

		}

		try (FileOutputStream outputStream = new FileOutputStream(targetXMLPluginsFile)) {
			pluginsExporter.exportPlugins(xmlPlugins, outputStream);
		}
		System.out.println("- written:" + targetXMLPluginsFile);
	}

	private void writeTasksFile(SDKBuilderContext context) throws IOException {
		XMLTasksExporter exporter = new XMLTasksExporter();
		File outputFile = new File(context.targetPathDirectory, "tasks.xml");
		XMLTasks tasks = new XMLTasks();
		Set<Task> xmlTasks = tasks.getTasks();
		for (String key : context.tasks.keySet()) {
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
			Type taskType = context.tasks.get(key);

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

	private void startTaskDataEstimation(GradleDSLTypeProvider provider, SDKBuilderContext context) throws IOException{
		/* now load the xml files as type data - and inspect all descriptions */
		System.out.println("- start task data estimation");
		
		for (String typeName : context.originTypeNameToOriginFileMapping.keySet()) {
			tryToResolveTask(context, provider, typeName);
		}
		
	}
	private void estimateStillMissingDelegationTargets_byJavaDoc(GradleDSLTypeProvider provider, SDKBuilderContext context) throws IOException{
		/* now load the xml files as type data - and inspect all descriptions */
		System.out.println("- estimate still missing estimation targets my javadoc ");
		
		for (String typeName : context.originTypeNameToOriginFileMapping.keySet()) {
			Type type=provider.getType(typeName);
			estimateDelegateTargets_by_javdoc(type,context);
		}
		
	}

	/**
	 * Currently tasks.xml is only for information. but coulde be used in future
	 * for task type resolving.
	 * 
	 * @param context
	 * @param provider
	 * @param typeName
	 */
	private void tryToResolveTask(SDKBuilderContext context, GradleDSLTypeProvider provider, String typeName) {

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
			for (String acceptedAsTask : context.tasks.keySet()) {
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
			context.tasks.put(type.getName(), type);
		}
	}

	
	private void inspectFilesAndAdopt(XMLDSLTypeOverrides overrides, GradleDSLTypeProvider originGradleFilesProvider, Map<String, String> alternativeApiMapping, File sourceDir,
			File targetDir, SDKBuilderContext context) throws IOException {
		File[] listFiles = sourceDir.listFiles(new FileFilter() {

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
				});
		
		
		
		for (File newSourceFile : listFiles) {
			
			String name = newSourceFile.getName();
			if (newSourceFile.isDirectory()) {
				File newTargetDir = new File(targetDir, name);
				inspectFilesAndAdopt(overrides, originGradleFilesProvider, alternativeApiMapping, newSourceFile, newTargetDir, context);
			} else if (newSourceFile.isFile()) {
				File newTargetFile = new File(targetDir, name);

				String changedSource = readAndAdopt(alternativeApiMapping, newSourceFile);
				write(changedSource, newTargetFile);

				/* new TargetFile is now written */

				boolean ignore = false;
				String targetFileName = newTargetFile.getName();
				ignore = ignore | targetFileName.endsWith("plugins.xml");

				if (!ignore) {
					handleMetaInformation(overrides, originGradleFilesProvider,newTargetFile, context);
				}
			}
		}
	}

	/**
	 * Start estimatinos:
	 * <ul>
	 * 	<li>delegate targets</li>
	 * <li>documented or not identifications/merge</li>
	 * </ul>
	 * @param overrides 
	 * @param provider 
	 * @param newTargetFile
	 * @param context
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void handleMetaInformation(XMLDSLTypeOverrides overrides, GradleDSLTypeProvider provider, File newTargetFile, SDKBuilderContext context)
			throws IOException, FileNotFoundException {
		try {
			XMLType type2 = null;

			try (FileInputStream inputStream = new FileInputStream(newTargetFile)) {
				type2 = typeImporter.importType(inputStream);

			}
			
			if (type2 == null) {
				throw new IllegalStateException("was not able to read type:" + newTargetFile);
			}
			
			
			String typeName= type2.getName();
			context.originTypeNameToOriginFileMapping.put(type2.getName(),newTargetFile);
			/* why so complicated - the provider has data already combined (so super class etc. is available). and typ2 is necessary, because 
			 * I am too lazy to do name resolving again
			 */
			XMLType type = (XMLType) provider.getType(typeName);
			
			
			
			handleOverrides(type,overrides);
			/* FIXME ATR, 21.02.2017: this is still a problem: the next stepp will try to calculate missing delegates -when interface 
			 * calculation shall be done and override is first, all parts have to be overriden BEFORE all!  */
			/*
			 * calculate missing delegate targets 
			 */
			calculateStillMissingDelegateTargets(type, context);
			
			/* FIXME ATR, 21.02.2017: refactor this. this is mess!! */
			
			try (FileOutputStream outputStream = new FileOutputStream(newTargetFile)) {
				String name = type.getName();
				File dslXML = new File(context.gradleSubProjectDocsFolder, "src/docs/dsl/"+name+".xml");
				if (dslXML.exists()){
					type.setDocumented(true);
					XMLDSLTypeDocumentation dslInfo = originDslTypeInfoImporter.collectDSL(dslXML);
					markGradleDSLMethods(type, dslInfo);
					markGradleDSLPropertiess(type, dslInfo);
				}else{
					type.setDocumented(false);
				}
				typeExporter.exportType(type, outputStream);
			}
		} catch (IOException e) {
			throw new IOException("Problems with file:" + newTargetFile.getAbsolutePath(), e);
		}
	}

	void handleOverrides(XMLType type, XMLDSLTypeOverrides overrides) {
		for (XMLType overriden: overrides.getOverrideTypes()){
			if (overriden.getName().equals(type.getName())){
				handleOverrideDelegationTarget(type,overriden);
			}
		}
		
	}

	private void handleOverrideDelegationTarget(XMLType type, XMLType overriden) {
		Set<Method> overridenMethods = overriden.getMethods();
		for (Method overridenMethod: overridenMethods){
			for (Method method: type.getDefinedMethods()){
				if (!(method instanceof ModifiableMethod)) {
					continue;
				}
				if (MethodUtils.haveSameSignatures(method, overridenMethod)){
					ModifiableMethod xmlMethod = (ModifiableMethod) method;
					xmlMethod.setDelegationTargetAsString(overridenMethod.getDelegationTargetAsString());
					
				}
			}
		}
	}

	private void markGradleDSLMethods(XMLType type, XMLDSLTypeDocumentation dslInfo) {
		Set<XMLDSLMethodInfo> methodInfos = dslInfo.getMethods();
		
		Set<Method> methods = type.getMethods();
		for (Method method: methods){
			if (!(method instanceof ModifiableMethod)){
				continue;
			}
			ModifiableMethod modifiableMethod = (ModifiableMethod) method;
			Iterator<XMLDSLMethodInfo> methodInfoIterator = methodInfos.iterator();
			String methodName = modifiableMethod.getName();
			while (methodInfoIterator.hasNext()){
				XMLDSLMethodInfo methodInfo = methodInfoIterator.next();
				String methodInfoName = methodInfo.getName();
				if (methodName.equals(methodInfoName)){
					modifiableMethod.setDocumented(true);
				}
			}
		}
	}
	private void markGradleDSLPropertiess(XMLType type, XMLDSLTypeDocumentation dslInfo) {
		Set<XMLDSLPropertyInfo> propertyInfos = dslInfo.getProperties();
		
		Set<Property> properties = type.getProperties();
		for (Property method: properties){
			if (!(method instanceof ModifiableProperty)){
				continue;
			}
			ModifiableProperty modifiableProperty = (ModifiableProperty) method;
			Iterator<XMLDSLPropertyInfo> propertyInfoIterator = propertyInfos.iterator();
			String propertyName = modifiableProperty.getName();
			while (propertyInfoIterator.hasNext()){
				XMLDSLPropertyInfo methodInfo = propertyInfoIterator.next();
				String propertyInfoName = methodInfo.getName();
				if (propertyName.equals(propertyInfoName)){
					modifiableProperty.setDocumented(true);
				}
			}
		}
	}
	

	/**
	 * Very interesting how gradle internal works:
	 * <pre>
	 * 
	 * private EclipseJdt jdt;
	 * 
	 * private EclipseJdt getJdt();
	 * private void setJdt(EclipseJdt jdt);
	 * 
	 * private void jdt(Closure closure);
	 * </pre>
	 * So the closure type is simply always the property type!
	 * <pre>
	 * eclipse{
	 * 	jdt{
	 * 		// do something with jdt
	 *  }
	 * }
	 * </pre>
	 * Is pretty much like:
	 * <pre>
	 * 	project.callClosureWithDelegateTarget(getEclipse()).callClosureWithDelegateTarget(getJdt())...
	 * </pre>
	 * 
	 * Normally typical delegatino targets are done by EGradleAssembleDslTask in gradle fork. But there are some special parts
	 * which where now handled here.
	 * 
	 * @param type
	 * @param context
	 */
	void calculateStillMissingDelegateTargets(Type type, SDKBuilderContext context) {
		int problemCount = 0;
		StringBuilder problems = new StringBuilder();
		for (Method m : type.getMethods()) {
			if (!(m instanceof XMLMethod)) {
				continue;
			}
			context.methodAllCount++;
			XMLMethod method = (XMLMethod) m;
			String delegationTarget = method.getDelegationTargetAsString();
			if (!StringUtils.isBlank(delegationTarget)) {
				continue;// already set
			}
			String targetType = null;
			List<Parameter> parameters = method.getParameters();
			if (parameters.size()!=1){
				continue;
			}
			Parameter firstParam = parameters.iterator().next();
			if (! firstParam.getTypeAsString().equals("groovy.lang.Closure")){
				continue;
			}
			String methodName = m.getName();
			targetType= scanProperties(type, methodName);
			
			if (targetType != null) {
				method.setDelegationTargetAsString(targetType);
			}
		}
		if (problemCount > 0) {
			// System.out.println("- WARN: type:" + type.getName()
			// + " has following method without descriptions: has no description
			// " + problems.toString());
			context.methodWithOutDescriptionCount += problemCount;
		}

	}
	/**
	 * Estimates delegation targets by javadoc. will be only done for method having a closure inside!
	 * @param type
	 * @param context
	 */
	void estimateDelegateTargets_by_javdoc(Type type, SDKBuilderContext context) {
		estimateDelegateTargets_by_javdoc(type, context,true);
	}
	/**
	 * Estimates delegation targets by javadoc. 
	 * @param type
	 * @param context
	 * @param checkOnlyClosures when <code>true</code> only methods with containing closures inside will be estimated
	 */
	void estimateDelegateTargets_by_javdoc(Type type, SDKBuilderContext context, boolean checkOnlyClosures) {
		int problemCount = 0;
		StringBuilder problems = new StringBuilder();
		for (Method m : type.getDefinedMethods()) {
			if (!(m instanceof XMLMethod)) {
				continue;
			}
			context.methodAllCount++;
			XMLMethod method = (XMLMethod) m;
			String delegationTarget = m.getDelegationTargetAsString();
			if (!StringUtils.isBlank(delegationTarget)) {
				continue;// already set
			}
			if (checkOnlyClosures && !MethodUtils.hasGroovyClosureAsParameter(method)){
				continue;
			}
			String description = method.getDescription();
			if (description == null) {
				/* not resolvable */
				problemCount++;
				problems.append(method.getName());
				problems.append(" ");
				continue;
			}
			if (description.indexOf(HYPERLINK_TYPE_PREFIX)==-1){
				/* fetch interfaces */
				description = fetchDescriptionFromInterfaces(type, method);
			}
			String targetType = inspectTargetTypeByDescription(description);
			if (targetType != null) {
				method.setDelegationTargetAsString(targetType);
			}
		}
		if (problemCount > 0) {
			// System.out.println("- WARN: type:" + type.getName()
			// + " has following method without descriptions: has no description
			// " + problems.toString());
			context.methodWithOutDescriptionCount += problemCount;
		}

	}

	private String inspectTargetTypeByDescription(String description) {
		if (description==null){
			return null;
		}
		String targetType =null;
		int index = 0;
		while (targetType == null && index != -1) {
			int from = index + 1;
			index = StringUtils.indexOf(description, HYPERLINK_TYPE_PREFIX, from);
			if (index != -1) {
				targetType = inspect(index, description);
			}
		}
		return targetType;
	}

	private String fetchDescriptionFromInterfaces(Type type, XMLMethod method) {
		Set<TypeReference> interfaces = type.getInterfaces();
		for (TypeReference interfaceReference: interfaces){
			Type _interface = interfaceReference.getType();
			if (_interface==null){
				/* can happen - e.g. for java.util.Set...*/
				continue;
			}
			for (Method m2 :_interface.getDefinedMethods()){
				if (!MethodUtils.haveSameSignatures(m2,method)){
					continue;
				}
				String description = m2.getDescription();
				if (description == null) {
					continue;
				}
				if (description.indexOf("@inheritDoc")!=-1){
					continue;
				}
				if (description.indexOf(HYPERLINK_TYPE_PREFIX)==-1){
					continue;
				}
				/* description found - use it!*/
				return description;
			}
		}
		return null;
	}

	private String scanProperties(Type type, String methodName) {
		/* try to find a property - in this class or subclass or interfaces etc..*/
		Set<Property> allProperties = type.getProperties();
		for (Property p : allProperties){
			String propName = p.getName();
			if (propName.equals(methodName)){
				String typeAsString = p.getTypeAsString();
				if (typeAsString!=null && !typeAsString.isEmpty()){
					return typeAsString;
				}
			}
		}
		return null;
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
