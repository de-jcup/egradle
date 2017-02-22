package de.jcup.egradle.sdk.builder.action.javadoc;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BeautifyJavaDocActionTest {
	private RemoveWhitespacesAndStarsFromJavadocAction actionToTest;

	@Before
	public void before() {
		actionToTest = new RemoveWhitespacesAndStarsFromJavadocAction();
	}

	@Test
	public void test_removeWhitespacesAndStars1() {
		assertEquals("alpha", actionToTest.removeWhitespacesAndStars("    *alpha"));
	}

	@Test
	public void test_removeWhitespacesAndStars2() {
		assertEquals("alpha", actionToTest.removeWhitespacesAndStars(" alpha"));
	}

	@Test
	public void test_removeWhitespacesAndStars3() {
		assertEquals("alpha", actionToTest.removeWhitespacesAndStars("alpha"));
	}

	@Test
	public void test_removeWhitespacesAndStars4() {
		assertEquals("a lpha", actionToTest.removeWhitespacesAndStars("a lpha"));
	}

	@Test
	public void test_removeWhitespacesAndStars5() {
		String line = "*  something";
		String expected = "  something";
		assertEquals(expected, actionToTest.removeWhitespacesAndStars(line));
	}

	
}
