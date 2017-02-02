package de.jcup.egradle.core.api;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class LinkToTypeConverterTest {

	private LinkToTypeConverter converterToTest;

	@Before
	public void before() {
		converterToTest = new LinkToTypeConverter();
	}
	
	@Test
	public void test_type__Eclipse_ended_with_slash__converted_to_Eclipse() {
		assertEquals("Eclipse",converterToTest.convertLink("type://Eclipse/"));
	}
	
	@Test
	public void test_type__Eclipse_not_slash_ended_converted_to_Eclipse() {
		assertEquals("Eclipse",converterToTest.convertLink("type://Eclipse"));
	}
	

}
