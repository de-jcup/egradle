/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
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
package de.jcup.egradle.eclipse.gradleeditor;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.jcup.egradle.codeassist.CodeCompletionRegistry;
import de.jcup.egradle.codeassist.dsl.ApiMappingImporter;
import de.jcup.egradle.codeassist.dsl.FilesystemFileLoader;
import de.jcup.egradle.codeassist.dsl.XMLPluginsImporter;
import de.jcup.egradle.codeassist.dsl.XMLTypeImporter;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLPluginLoader;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLTypeProvider;
import de.jcup.egradle.core.util.ErrorHandler;
import de.jcup.egradle.eclipse.util.ColorManager;
import de.jcup.egradle.eclipse.util.EclipseDevelopmentSettings;
import de.jcup.egradle.sdk.SDK;
import de.jcup.egradle.sdk.SDKManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class EditorActivator extends AbstractUIPlugin {

	// The plug-in COMMAND_ID
	public static final String PLUGIN_ID = "de.jcup.egradle.eclipse.plugin.editor.gradle"; //$NON-NLS-1$

	// The shared instance
	private static EditorActivator plugin;
	private ColorManager colorManager;

	private CodeCompletionRegistry codeCompletionRegistry;

	/**
	 * The constructor
	 */
	public EditorActivator() {
		colorManager = new ColorManager();
		codeCompletionRegistry = new CodeCompletionRegistry();
	}

	public ColorManager getColorManager() {
		return colorManager;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		long timeStart=System.currentTimeMillis();
		
		
		SDK sdk = SDKManager.get().getCurrentSDK();
		boolean sdkInstalled=false;
		if (! sdk.isInstalled()){
			try{
				sdk.install();
				sdkInstalled=true;
			}catch(IOException e){
				EditorUtil.logError("Was not able install SDK:"+sdk.getVersion(),e);
			}
		}
		
		GradleDSLTypeProvider gradleDslProvider = initTypeProvider(sdk);

		/* load project per default so show up time for tooltips faster */
		gradleDslProvider.getType("org.gradle.api.Project");
		if (EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_LOGGING){
			long timeEnd = System.currentTimeMillis();
			double seconds = (timeEnd-timeStart)/1000;
			getLog().log(new Status(IStatus.INFO, PLUGIN_ID,"Gradle editor startup in :"+seconds+" seconds"+", sdk installed:"+sdkInstalled));
		}

	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		colorManager.dispose();
		super.stop(context);
	}

	public CodeCompletionRegistry getCodeCompletionRegistry() {
		return codeCompletionRegistry;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static EditorActivator getDefault() {
		return plugin;
	}

	private GradleDSLTypeProvider initTypeProvider(SDK sdk) {
		File dslFolder = sdk.getSDKInstallationFolder();
		ErrorHandler errorHandler = EGradleErrorHandler.INSTANCE;
		codeCompletionRegistry.setErrorHandler(errorHandler);
		
		/*
		 * init code completion parts - when dsl folder not correctly set it
		 * will not work but it is robust will only do nothing
		 */
		XMLTypeImporter typeImporter = new XMLTypeImporter();
		XMLPluginsImporter pluginsImporter = new XMLPluginsImporter();
		ApiMappingImporter apiMappingImporter = new ApiMappingImporter();
		FilesystemFileLoader loader = new FilesystemFileLoader(typeImporter, pluginsImporter, apiMappingImporter);
		loader.setDSLFolder(dslFolder);
		
		GradleDSLTypeProvider gradleDslProvider = new GradleDSLTypeProvider(loader);
		GradleDSLPluginLoader gradleDslPluginLoader = new GradleDSLPluginLoader(loader);
		
		codeCompletionRegistry.registerService(GradleDSLTypeProvider.class, gradleDslProvider);
		codeCompletionRegistry.registerService(GradleDSLPluginLoader.class, gradleDslPluginLoader);
		
		codeCompletionRegistry.init();
		
		return gradleDslProvider;
	}

}
