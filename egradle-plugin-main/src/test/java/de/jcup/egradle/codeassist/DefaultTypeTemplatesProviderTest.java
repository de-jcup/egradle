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
package de.jcup.egradle.codeassist;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class DefaultTypeTemplatesProviderTest {

	private TypeTemplatesProvider providerToTest;

	@Before
	public void before() {
		providerToTest = new DefaultTypeTemplatesProvider();
	}

	@Test
	public void test_contains_data_for_dependency_handler() {
		List<Template> templates = providerToTest.getTemplatesForType("org.gradle.api.artifacts.dsl.DependencyHandler");
		assertNotNull(templates);
		assertTrue((templates.size() > 1));
	}

}
