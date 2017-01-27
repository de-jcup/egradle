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
package de.jcup.egradle.eclipse.gradleeditor;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.jcup.egradle.codecompletion.CodeCompletionRegistry;
import de.jcup.egradle.codecompletion.dsl.AbstractDSLTypeProvider;
import de.jcup.egradle.codecompletion.dsl.FilesystemFileLoader;
import de.jcup.egradle.codecompletion.dsl.XMLDSLPluginsImporter;
import de.jcup.egradle.codecompletion.dsl.XMLDSLTypeImporter;
import de.jcup.egradle.codecompletion.dsl.gradle.GradleDSLTypeProvider;
import de.jcup.egradle.core.api.FileHelper;
import de.jcup.egradle.eclipse.api.ColorManager;
import de.jcup.egradle.eclipse.api.EGradleErrorHandler;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in COMMAND_ID
	public static final String PLUGIN_ID = "de.jcup.egradle.eclipse.plugin.editor.gradle"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	private ColorManager colorManager;

	private CodeCompletionRegistry codeCompletionRegistry;

	/**
	 * The constructor
	 */
	public Activator() {
		colorManager=new ColorManager();
		codeCompletionRegistry=new CodeCompletionRegistry();
	}


	public ColorManager getColorManager() {
		return colorManager;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		codeCompletionRegistry.setErrorHandler(EGradleErrorHandler.INSTANCE);
	
		/* init code completion parts */
		XMLDSLTypeImporter typeImporter = new XMLDSLTypeImporter();
		XMLDSLPluginsImporter pluginsImporter = new XMLDSLPluginsImporter();
		/* FIXME ATR, 19.01.2017: make version changeable... Maybe codeCompletionRegistry.get(GradleDSLTypeProvider.changeVersion...*/
		FilesystemFileLoader loader = new FilesystemFileLoader(typeImporter,pluginsImporter);
		loader.setDSLFolder(FileHelper.DEFAULT.getEGradleUserHomeFolder("dsl/gradle/3.0"));
		GradleDSLTypeProvider gradleDslProvider = new GradleDSLTypeProvider(loader);
		/* install dsl type provider as service, so it must be definitely used shared...*/
		codeCompletionRegistry.registerService(GradleDSLTypeProvider.class, gradleDslProvider);
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		colorManager.dispose();
		super.stop(context);
	}

	public CodeCompletionRegistry getCodeCompletionRegistry(){
		return codeCompletionRegistry;
	}

	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
