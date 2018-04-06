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
package de.jcup.egradle.core.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.util.LinkToTypeConverter.LinkData;

public class LinkToTypeConverterTest {

	private LinkToTypeConverter converterToTest;

	@Before
	public void before() {
		converterToTest = new LinkToTypeConverter();
	}

	@Test
	public void bugfix__link_to__org_gradle_api_Project_slash_hash_getAnt_brackets__results_in_getAntMethod() {
		/* execute */
		LinkData convertLink = converterToTest.convertLink("type://org.gradle.api.Project/#getAnt()");

		/* test */
		assertEquals("org.gradle.api.Project", convertLink.getMainName());
		assertEquals("getAnt", convertLink.getSubName());

		String[] parameters = convertLink.getParameterTypes();

		assertEquals(0, parameters.length);
	}

	@Test
	public void test_property_resolved() {
		/* execute */
		LinkData convertLink = converterToTest.convertLink("type://MyType#myProperty");

		/* test */
		assertEquals("MyType", convertLink.getMainName());
		assertEquals("myProperty", convertLink.getSubName());
	}

	@Test
	public void test_type__parameters_detected_when_html_encoded_parts_reuse_current_type() {
		/* execute */
		LinkData convertLink = converterToTest.convertLink("type://#task(java.util.Map,%20String)");

		/* test */
		assertEquals(null, convertLink.getMainName());
		assertEquals("task", convertLink.getSubName());
		String[] parameters = convertLink.getParameterTypes();
		assertEquals(2, parameters.length);
		assertEquals("java.util.Map", parameters[0]);
		assertEquals("String", parameters[1]);
	}

	@Test
	public void test_type__parameters_detected_when_html_encoded_parts() {
		/* execute */
		LinkData convertLink = converterToTest.convertLink("type://MyType#task(java.util.Map,%20String)");

		/* test */
		assertEquals("MyType", convertLink.getMainName());
		assertEquals("task", convertLink.getSubName());
		String[] parameters = convertLink.getParameterTypes();

		assertEquals(2, parameters.length);
		assertEquals("java.util.Map", parameters[0]);
		assertEquals("String", parameters[1]);
	}

	@Test
	public void test_type__parameters_detected_when_html_encoded_parts_slash_before_hash_is_handled() {
		/* execute */
		LinkData convertLink = converterToTest.convertLink("type://MyType/#task(java.util.Map,%20String)");

		/* test */
		assertEquals("MyType", convertLink.getMainName());
		assertEquals("task", convertLink.getSubName());
		String[] parameters = convertLink.getParameterTypes();

		assertEquals(2, parameters.length);
		assertEquals("java.util.Map", parameters[0]);
		assertEquals("String", parameters[1]);
	}

	@Test
	public void test_type__parameters_detected_when_html_encoded_parts_slash_before_hash_is_handled_method_has_parameter_names_inside() {
		/* execute */
		LinkData convertLink = converterToTest
				.convertLink("type://org.gradle.testing.jacoco.plugins.JacocoPluginExtension/#applyTo(T%20task)");

		/* test */
		assertEquals("org.gradle.testing.jacoco.plugins.JacocoPluginExtension", convertLink.getMainName());
		assertEquals("applyTo", convertLink.getSubName());
		String[] parameters = convertLink.getParameterTypes();

		assertEquals(1, parameters.length);
		assertEquals("T", parameters[0]);
	}

	@Test
	public void test_type__Eclipse_ended_with_slash__converted_to_Eclipse() {
		LinkData convertLink = converterToTest.convertLink("type://Eclipse/");
		assertEquals("Eclipse", convertLink.getMainName());
		assertEquals(null, convertLink.getSubName());
		assertEquals(0, convertLink.getParameterTypes().length);
	}

	@Test
	public void test_type__Eclipse_method_slash_between() {
		LinkData convertLink = converterToTest.convertLink("type://Eclipse/#bla()");
		assertEquals("Eclipse", convertLink.getMainName());
		assertEquals("bla", convertLink.getSubName());
		assertEquals(0, convertLink.getParameterTypes().length);
	}
	// type://Configuration/#getExcludeRules()

	@Test
	public void test_type__Eclipse_not_slash_ended_converted_to_Eclipse() {
		LinkData convertLink = converterToTest.convertLink("type://Eclipse");
		assertEquals("Eclipse", convertLink.getMainName());
		assertEquals(null, convertLink.getSubName());
		assertEquals(0, convertLink.getParameterTypes().length);
	}

}
