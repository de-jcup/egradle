package de.jcup.egradle.other;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SDKBuilderTest {
	private SDKBuilder preparatorToTest;

	/*
	 * FIXME ATR, 02.02.2017: add testcases - e.g. AntBuilder does currently
	 * have a "This is resolved as per <a
	 * href='type://Project#file(Object)'>Project#file(Object)</a>" inside!.
	 */
	@Before
	public void before() {
		preparatorToTest = new SDKBuilder();
	}

	@Test
	public void test1() {
		assertEquals("alpha", preparatorToTest.convertLine("    *alpha"));
	}

	@Test
	public void test2() {
		assertEquals("alpha", preparatorToTest.convertLine(" alpha"));
	}

	@Test
	public void test3() {
		assertEquals("alpha", preparatorToTest.convertLine("alpha"));
	}

	@Test
	public void test4() {
		assertEquals("a lpha", preparatorToTest.convertLine("a lpha"));
	}

	@Test
	public void testJavadocLinkConversion_simple_link_with_full_path_name() {
		String line = "{@link org.gradle.api.invocation.Gradle}";
		String expected = "<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, preparatorToTest.convertLine(line));
	}

	@Test
	public void testJavadocLinkConversion_simple_link_with_short_path_name() {
		String line = "{@link EclipseProject}";
		String expected = "<a href='type://EclipseProject'>EclipseProject</a>";
		assertEquals(expected, preparatorToTest.convertLine(line));
	}

	@Test
	public void testJavadocLinkConversion_simple_link_with_short_path_name_but_prefix() {
		String line = "More examples in docs for {@link EclipseProject}";
		String expected = "More examples in docs for <a href='type://EclipseProject'>EclipseProject</a>";
		assertEquals(expected, preparatorToTest.convertLine(line));
	}

	@Test
	public void testJavaDocLink3() {
		String line = "More examples in docs for {@link EclipseProject}, {@link EclipseClasspath}, {@link EclipseWtp} ";
		String expected = "More examples in docs for " + "<a href='type://EclipseProject'>EclipseProject</a>, "
				+ "<a href='type://EclipseClasspath'>EclipseClasspath</a>, "
				+ "<a href='type://EclipseWtp'>EclipseWtp</a> ";
		assertEquals(expected, preparatorToTest.convertLine(line));
	}

	@Test
	public void testJavaDocLink4_mixed_arguments() {
		String line = "More examples in docs for {@link EclipseProject}, {@return EclipseClasspath}, {@link EclipseWtp} ";
		String expected = "More examples in docs for " + "<a href='type://EclipseProject'>EclipseProject</a>, "
				+ "{@return EclipseClasspath}, " + "<a href='type://EclipseWtp'>EclipseWtp</a> ";
		assertEquals(expected, preparatorToTest.convertLine(line));
	}

}
