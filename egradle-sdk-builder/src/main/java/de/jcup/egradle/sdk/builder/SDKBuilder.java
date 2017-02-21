package de.jcup.egradle.sdk.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	public static void main(String[] args) throws IOException {
		SDKBuilder builder = new SDKBuilder("./../../gradle");
		File srcMainResTarget = new File("./../egradle-plugin-sdk/src/main/res/");
		builder.buildSDK(srcMainResTarget, "3.0");
	}

	private String pathTorGradleRootProjectFolder;

	public SDKBuilder(String pathTorGradleRootProjectFolder) {
		this.pathTorGradleRootProjectFolder = pathTorGradleRootProjectFolder;
	}

	public void buildSDK(File targetRootDirectory, String gradleVersion) throws IOException {
		SDKBuilderContext context = new SDKBuilderContext(pathTorGradleRootProjectFolder, targetRootDirectory,
				gradleVersion);
		
		/* create actions and add in wanted ordering */
		List<SDKBuilderAction> actions = new ArrayList<>();
		actions.add(new InitSDKTargetFolderAction());
		actions.add(new CopyApiMappingsAction());
		actions.add(new ImportTypesAction());
		
		actions.add(new ImportPluginsAction());
		actions.add(new ApplyOverridesToPluginsAction());
		actions.add(new SavePluginsToSDKTargetFolder());

		
		actions.add(new ApplyOverridesToTypesAction());
		actions.add(new TransformJavadocDescriptionsToNormalHTMLAction());

		actions.add(new CalculateDelegationTargetsAction());
		actions.add(new EstimateDelegationTargetsByJavadocAction());
		
		actions.add(new CreateAlternativeMappingFileAction());
		actions.add(new MarkDocumentedLanguageElementsAction());

		
		actions.add(new CreateTasksSDKFileAction());
		actions.add(new SaveTypesToSDKTargetFolder());
		
		/* execute sdk builder actions:*/
		for (SDKBuilderAction action: actions){
			action.execute(context);
		}
		
		System.out.println("DONE");
	}
	
}
