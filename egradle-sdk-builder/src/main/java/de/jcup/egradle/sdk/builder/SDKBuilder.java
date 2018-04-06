/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.egradle.sdk.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;
import de.jcup.egradle.sdk.builder.action.delegationtarget.CalculateDelegationTargetsAction;
import de.jcup.egradle.sdk.builder.action.delegationtarget.EstimateDelegationTargetsByJavadocAction;
import de.jcup.egradle.sdk.builder.action.delegationtarget.InheritDelegationTargetsAction;
import de.jcup.egradle.sdk.builder.action.init.InitSDKInfoAction;
import de.jcup.egradle.sdk.builder.action.init.InitSDKTargetFolderAction;
import de.jcup.egradle.sdk.builder.action.javadoc.RemoveWhitespacesAndStarsFromJavadocAction;
import de.jcup.egradle.sdk.builder.action.javadoc.ReplaceJavaDocPartsAction;
import de.jcup.egradle.sdk.builder.action.mapping.CopyApiMappingsAction;
import de.jcup.egradle.sdk.builder.action.mapping.CreateAlternativeMappingFileAction;
import de.jcup.egradle.sdk.builder.action.plugin.ApplyOverridesToPluginsAction;
import de.jcup.egradle.sdk.builder.action.plugin.ImportPluginsAction;
import de.jcup.egradle.sdk.builder.action.plugin.SavePluginsToSDKTargetFolder;
import de.jcup.egradle.sdk.builder.action.task.CreateTasksSDKFileAction;
import de.jcup.egradle.sdk.builder.action.type.ApplyOverridesToTypesAction;
import de.jcup.egradle.sdk.builder.action.type.ImportTypesAction;
import de.jcup.egradle.sdk.builder.action.type.MarkDocumentedLanguageElementsAction;
import de.jcup.egradle.sdk.builder.action.type.SaveTypesToSDKTargetFolder;

/**
 * The egradle <a href="https://github.com/de-jcup/gradle">gradle fork</a> has
 * special task called "dslEgradle". You must call this task and also
 * "defaultImports" task before you execute SDKBuilder!<br>
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
		/* prepare */
		actions.add(new InitSDKTargetFolderAction());
		actions.add(new InitSDKInfoAction());

		actions.add(new CopyApiMappingsAction());
		actions.add(new ImportTypesAction());
		actions.add(new RemoveWhitespacesAndStarsFromJavadocAction());

		actions.add(new ImportPluginsAction());
		actions.add(new ApplyOverridesToPluginsAction());
		actions.add(new SavePluginsToSDKTargetFolder());

		actions.add(new ApplyOverridesToTypesAction());
		actions.add(new InheritDelegationTargetsAction()); // after apply
															// overrides!
		actions.add(new MarkDocumentedLanguageElementsAction());

		actions.add(new ReplaceJavaDocPartsAction());

		actions.add(new CalculateDelegationTargetsAction());
		actions.add(new EstimateDelegationTargetsByJavadocAction());

		/* persist */
		actions.add(new CreateAlternativeMappingFileAction());
		actions.add(new CreateTasksSDKFileAction());
		actions.add(new SaveTypesToSDKTargetFolder());

		/* execute sdk builder actions: */
		for (SDKBuilderAction action : actions) {
			info("executing:" + action.getClass().getSimpleName());
			action.execute(context);
		}

		info("DONE");
	}

	private void info(String message) {
		System.out.println(message);
	}

}
