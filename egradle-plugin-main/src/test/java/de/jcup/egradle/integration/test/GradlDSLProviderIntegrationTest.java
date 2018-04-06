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

import static de.jcup.egradle.integration.TypeAssert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLTypeProvider;
import de.jcup.egradle.integration.IntegrationTestComponents;

public class GradlDSLProviderIntegrationTest {

	@Rule
	public IntegrationTestComponents components = IntegrationTestComponents.initialize();
	private GradleDSLTypeProvider dslProvider;

	@Before
	public void before() {
		dslProvider = components.getGradleDslProvider();
	}

	@Test
	public void copytask__is_implementing_org_gradle_api_task() {
		Type copy = dslProvider.getType("org.gradle.api.tasks.Copy");
		/* @formatter:off*/
		assertType(copy).
			hasInterface("org.gradle.api.Task"); 
		/* @formatter:on*/
	}

	@Test
	public void org_gradle_project__is_interface__and__extends_PluginAware_and_Extension_Aware() {
		Type project = dslProvider.getType("org.gradle.api.Project");
		/* @formatter:off*/
		assertType(project).
			isInterface().// is itself an interface 
			hasInterface("org.gradle.api.plugins.PluginAware"). // is extending interface plugin aware and 
			hasInterface("org.gradle.api.plugins.ExtensionAware");// extension aware
		/* @formatter:on*/
	}

	@Test
	public void org_gradle_copy_task__is_no_interface__and__has_extends_PluginAware_and_Extension_Aware() {
		Type project = dslProvider.getType("org.gradle.api.tasks.Copy");
		/* @formatter:off*/
		assertType(project).
			isNotInterface().// is itself not an interface
			isDecendantOf("org.gradle.api.tasks.AbstractCopyTask");
		/* @formatter:on*/
	}

}
