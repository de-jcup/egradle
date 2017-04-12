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
 package de.jcup.egradle.integration.test;

import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.FilesystemFileLoader;
import de.jcup.egradle.codeassist.dsl.Plugin;
import de.jcup.egradle.integration.IntegrationTestComponents;
import static de.jcup.egradle.integration.PluginsAssert.*;

public class FileLoaderIntegrationTest {

	@Rule
	public IntegrationTestComponents components = IntegrationTestComponents.initialize();
	private FilesystemFileLoader fileLoader;

	@Before
	public void before(){
		fileLoader = components.getFileLoader();
	}
	
	@Test
	public void fileLoader_war_plugin__has_mixin_type_war_convention() throws Exception{
		/* execute */
		Set<Plugin> plugins = fileLoader.loadPlugins();
		/* test */
		/* @formatter:off*/
		assertPlugins(plugins).
			hasPlugin("war").
			withMixinType("org.gradle.api.Project", "org.gradle.api.plugins.WarPluginConvention");
		/* @formatter:on*/
	}
	
	@Test
	public void fileLoader_ear_plugin__has_mixin_type_ear_convention_and_extension() throws Exception{
		/* execute */
		Set<Plugin> plugins = fileLoader.loadPlugins();
		/* test */
		/* @formatter:off*/
		assertPlugins(plugins).
			hasPlugin("ear").
			withMixinType("org.gradle.api.Project", "org.gradle.plugins.ear.EarPluginConvention").
			withExtensionType("org.gradle.api.Project", "org.gradle.plugins.ear.Ear");
		/* @formatter:on*/
	}

	@Test
	public void fileLoader_signing_plugin__has_extension_type_signing_extension() throws Exception{
		/* execute */
		Set<Plugin> plugins = fileLoader.loadPlugins();
		/* test */
		assertPlugins(plugins).hasPlugin("signing").withExtensionType("org.gradle.api.Project", "org.gradle.plugins.signing.SigningExtension");
	}
	
	

}
