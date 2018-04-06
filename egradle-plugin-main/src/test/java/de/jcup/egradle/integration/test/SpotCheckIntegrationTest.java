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

import org.junit.Rule;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.integration.IntegrationTestComponents;

/**
 * Does some spot check to data
 * 
 * @author Albert Tregnaghi
 *
 */
public class SpotCheckIntegrationTest {

	@Rule
	public IntegrationTestComponents components = IntegrationTestComponents.initialize();

	@Test
	public void jar_has_interface_copy_spec() {
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.api.tasks.bundling.Jar");

		/* test */
		assertType(jarType).hasInterface("org.gradle.api.file.CopySpec");
	}

	@Test
	public void sourceset_fullname_has_mixin_parts_from_scala() {
		/* execute */
		Type sourceSetType = components.getGradleDslProvider().getType("org.gradle.api.tasks.SourceSet");

		/* test */
		assertType(sourceSetType).hasName("org.gradle.api.tasks.SourceSet").hasMethod("scala", "groovy.lang.Closure");
	}

	@Test
	public void sourceset_shortname_has_mixin_parts_from_scala() {
		/* execute */
		Type sourceSetType = components.getGradleDslProvider().getType("SourceSet");

		/* test */
		assertType(sourceSetType).hasName("org.gradle.api.tasks.SourceSet").hasMethod("scala", "groovy.lang.Closure");
	}

	@Test
	public void jar_has_zip_as_super_class__this_information_must_be_available_in_data() {
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.jvm.tasks.Jar");

		/* test */
		assertType(jarType).hasSuperType("org.gradle.api.tasks.bundling.Zip");
	}

	@Test
	public void bundling_jar_has_jar_as_super_class__this_information_must_be_available_in_data() {
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.api.tasks.bundling.Jar");

		/* test */
		assertType(jarType).hasSuperType("org.gradle.jvm.tasks.Jar");
	}

	@Test
	public void jar_1_has_manifest_method_itself__and_also_inherited_method_getTemporaryDirFactory_did_not_fail_alone_but_when_all() {
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.jvm.tasks.Jar");

		/* test */
		/* @formatter:off */
		assertType(jarType).
			hasMethod("manifest", "groovy.lang.Closure").
			hasMethod("getTemporaryDirFactory");
		/* @formatter:on */
	}

	/**
	 * Special test case which did produce a loop inheratance problem. An
	 * example
	 * 
	 * <pre>
	 * Class A Class B
	 * 
	 * methodA:ClassB methodB: ClassA
	 * 
	 * -> extends Class C -> extends Class C
	 * 
	 * Class C
	 * 
	 * methodC: String
	 * 
	 * <pre>
	 * 
	 * Now it depends which of the classes will be first initialized:
	 * 
	 * 1. Class A: will resolve Class B which will resolve
	 * 
	 */
	@Test
	public void jar_2_has_manifest_method_itself__and_also_inherited_method_getTemporaryDirFactory__failed_always_alone_and_also_when_all() {
		/* execute */
		Type copyType = components.getGradleDslProvider().getType("org.gradle.api.tasks.Copy");
		// jar type seems to be already loaded by former call */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.jvm.tasks.Jar");

		/* test */
		/* @formatter:off */
		assertType(copyType).
			hasMethod("getTemporaryDirFactory");
		assertType(jarType).
			hasMethod("manifest", "groovy.lang.Closure").
			hasMethod("getTemporaryDirFactory");
		/* @formatter:on */
	}

	@Test
	public void jar_3_has_manifest_method_itself__and_also_inherited_method_getTemporaryDirFactory_did_not_fail_alone_but_when_all() {
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.jvm.tasks.Jar");

		Type copyType = components.getGradleDslProvider().getType("org.gradle.api.tasks.Copy");

		/* test */
		/* @formatter:off */
		assertType(jarType).
			hasMethod("manifest", "groovy.lang.Closure").
			hasMethod("getTemporaryDirFactory");
		assertType(copyType).
			hasMethod("getTemporaryDirFactory");
		/* @formatter:on */
	}

	@Test
	public void jar_has_inherited_property_zip64() {
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.jvm.tasks.Jar");

		/* test */
		assertType(jarType).hasSuperType().hasProperty("zip64");

	}
}
