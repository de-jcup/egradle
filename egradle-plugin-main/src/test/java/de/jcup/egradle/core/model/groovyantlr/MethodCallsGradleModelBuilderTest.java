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
 package de.jcup.egradle.core.model.groovyantlr;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.ItemType;
import de.jcup.egradle.core.model.Model;

public class MethodCallsGradleModelBuilderTest {
	
	/**
	 * Test bug 171 - configure was not showing in outline view
	 * <a href="https://github.com/de-jcup/egradle/issues/171">Bug 171</a>
	 * @throws Exception
	 */
	@Test
	public void configure_must_be_inside_model__bugfix171() throws Exception{
		// @formatter:off
		String code = 
		"configure(subprojects.findAll(IS_PLUGIN)) {                \n"+
		"	def PROJECT_NAME = it.name                              \n"+
	    "                                                           \n"+
		"	apply plugin: 'java'                                    \n"+
		"	sourceSets {                                            \n"+
		"		main { java {                                       \n"+
		"			srcDir 'src'                                    \n"+
		"		} }                                                 \n"+
		"		test { java {                                       \n"+
		"			srcDir 'test'                                   \n"+
		"		} }                                                 \n"+
		"	}                                                       \n"+
		"	                                                        \n"+
		"}                                                          \n"+
		"                                                           \n";
		// @formatter:on
		InputStream is = new ByteArrayInputStream(code.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item configure = items[0];
		assertEquals(ItemType.CLOSURE, configure.getItemType());
		assertEquals("configure subprojects.findAll(IS_PLUGIN)", configure.getName());

		Item[] configChildren = configure.getChildren();
		assertEquals(3, configChildren.length);
		Item variableDef = configChildren[0];
		assertNotNull(variableDef);
		assertEquals("PROJECT_NAME", variableDef.getName());
	}
	
	@Test
	public void closures_without_parameters_have_no_brackets() throws Exception{
		// @formatter:off
		String code = 
		"configure{                \n"+
		"	def PROJECT_NAME = it.name                              \n"+
		"}                                                          \n"+
		"                                                           \n";
		// @formatter:on
		InputStream is = new ByteArrayInputStream(code.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item configure = items[0];
		assertEquals(ItemType.CLOSURE, configure.getItemType());
		assertEquals("configure", configure.getName());

		Item[] configChildren = configure.getChildren();
		assertEquals(1, configChildren.length);
		Item variableDef = configChildren[0];
		assertNotNull(variableDef);
		assertEquals("PROJECT_NAME", variableDef.getName());
	}
	
	
	@Test
	public void methodcall_with_dots__without_arguments__contains_fullname() throws Exception{
		// @formatter:off
		String code =
		"configurations.all{                \n"+
		"	def PROJECT_NAME = it.name                              \n"+
		"}                                                          \n"+
		"                                                           \n";
		// @formatter:on
		InputStream is = new ByteArrayInputStream(code.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item configure = items[0];
		assertEquals(ItemType.CONFIGURATIONS, configure.getItemType());
		assertEquals("configurations.all", configure.getName());

		Item[] configChildren = configure.getChildren();
		assertEquals(1, configChildren.length);
		Item variableDef = configChildren[0];
		assertNotNull(variableDef);
		assertEquals("PROJECT_NAME", variableDef.getName());
	}
	
}
