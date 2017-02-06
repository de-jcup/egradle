package de.jcup.egradle.core.api;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.api.LinkToTypeConverter.LinkData;

public class LinkToTypeConverterTest {

	private LinkToTypeConverter converterToTest;

	@Before
	public void before() {
		converterToTest = new LinkToTypeConverter();
	}

	@Test
	public void test_type__parameters_detected_when_html_encoded_parts_reuse_current_type() {
		/* execute */
		LinkData convertLink = converterToTest.convertLink("type://#task(java.util.Map,%20String)");

		/* test */
		assertEquals(null, convertLink.getTypeName());
		assertEquals("task", convertLink.getMethodName());
		String[] parameters = convertLink.getParameters();
		assertEquals(2, parameters.length);
		assertEquals("java.util.Map", parameters[0]);
		assertEquals("String", parameters[1]);
	}

	@Test
	public void test_type__parameters_detected_when_html_encoded_parts() {
		/* execute */
		LinkData convertLink = converterToTest.convertLink("type://MyType#task(java.util.Map,%20String)");

		/* test */
		assertEquals("MyType", convertLink.getTypeName());
		assertEquals("task", convertLink.getMethodName());
		String[] parameters = convertLink.getParameters();
		
		assertEquals(2, parameters.length);
		assertEquals("java.util.Map", parameters[0]);
		assertEquals("String", parameters[1]);
	}
	
	@Test
	public void test_type__Eclipse_ended_with_slash__converted_to_Eclipse() {
		LinkData convertLink = converterToTest.convertLink("type://Eclipse/");
		assertEquals("Eclipse", convertLink.getTypeName());
		assertEquals(null, convertLink.getMethodName());
		assertEquals(0, convertLink.getParameters().length);
	}

	@Test
	public void test_type__Eclipse_not_slash_ended_converted_to_Eclipse() {
		LinkData convertLink = converterToTest.convertLink("type://Eclipse");
		assertEquals("Eclipse", convertLink.getTypeName());
		assertEquals(null, convertLink.getMethodName());
		assertEquals(0, convertLink.getParameters().length);
	}

}
