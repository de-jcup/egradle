package de.jcup.egradle.sdk.builder.action.javadoc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.Type;

public class ReplaceJavaDocPartsActionTest {

	private ReplaceJavaDocPartsAction actionToTest;

	@Before
	public void before() {
		actionToTest = new ReplaceJavaDocPartsAction();
	}

	@Test
	public void testJavadocLinkConversion_simple_link_with_full_path_name() {
		String line = "{@link org.gradle.api.invocation.Gradle}";
		String expected = "<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void testJavadocLinkConversion_simple_link_with_full_path_name__whitespaces_after_curly() {
		String line = "{      @link org.gradle.api.invocation.Gradle}";
		String expected = "<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void handle_type_links_without_type() {
		Type type = mock(Type.class);
		when(type.getName()).thenReturn("Blubb");
		String line = "<a href='type://#method(xyz)'>org.gradle.api.invocation.Gradle</a>";
		String expected = "<a href='type://Blubb#method(xyz)'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, actionToTest.handleTypeLinksWithoutType(line, type));
	}

	@Test
	public void testJavadocLinkConversion_simple_link_with_full_path_name__whitespaces_after_curly__and_after_link() {
		String line = "{      @link     org.gradle.api.invocation.Gradle}";
		String expected = "<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void testJavadocLinkConversion_simple_link_with_short_path_name() {
		String line = "{@link EclipseProject}";
		String expected = "<a href='type://EclipseProject'>EclipseProject</a>";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void testJavadocParamConversion() {
		String line = "  @param    name1 a description";
		String expected = "  <br><b class='param'>param:</b>name1 a description";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void testJavadocParamConversion2() {

		String line = " * @param gradle The build which has been loaded. Never null.";
		line = actionToTest.removeWhitespacesAndStars(line);
		String expected = " <br><b class='param'>param:</b>gradle The build which has been loaded. Never null.";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void testJavadocParamConversion3() throws IOException {
		List<String> list = new ArrayList<>();
		list.add("<method name=\"projectsLoaded\" returnType=\"void\">");
		list.add("        <parameter name=\"gradle\" type=\"org.gradle.api.invocation.Gradle\"/>");
		list.add("        <description><![CDATA[");
		list.add(
				"     * <p>Called when the projects for the build have been created from the settings. None of the projects have been");
		list.add("     * evaluated.</p>");
		list.add("     *");
		list.add("     * @param gradle The build which has been loaded. Never null.");
		list.add("     ]]></description>");
		list.add("    </method>");

		StringBuilder fullDescription = new StringBuilder();
		for (String line: list){
			fullDescription.append(line).append("\n");
		}
		String transformed = actionToTest.replaceJavaDocParts(fullDescription.toString());
		assertTrue(transformed.indexOf("@param") == -1);
	}

	@Test
	public void testJavadocParamConversion5_two_params_following() throws Exception {
		String line = "  @param    name1 a description\n  @param    name2 a description\n";
		String converted = actionToTest.replaceJavaDocParts(line);
		assertTrue(converted.indexOf("@param") == -1);
	}

	@Test
	public void testJavadocParamConversion4() throws Exception {
		/* prepare */
		String description = "{@code xyz} bla bla";

		/* execute */
		String transformed = actionToTest.replaceJavaDocParts(description);

		/* test */
		assertTrue(transformed.indexOf("@code") == -1);

	}
	
	@Test
	public void testJavadocParamConversion_param_replaced() throws Exception {
		/* prepare */
		String fullDescription = "@param xyz bla bla";
		
		/* execute */
		String transformed = actionToTest.replaceJavaDocParts(fullDescription);

		/* test */
		assertTrue(transformed.indexOf("@param") == -1);

	}

	@Test
	public void testJavadocReturnConversion() {
		String line = "  @return name1 a description";
		String expected = "  <br><br><b class='return'>returns:</b>name1 a description";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void testJavadocLinkConversion_simple_link_with_short_path_name_but_prefix() {
		String line = "More examples in docs for {@link EclipseProject}";
		String expected = "More examples in docs for <a href='type://EclipseProject'>EclipseProject</a>";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void testJavaDocLink3() {
		String line = "More examples in docs for { @link EclipseProject}, {@link EclipseClasspath}, {@link EclipseWtp} ";
		String expected = "More examples in docs for " + "<a href='type://EclipseProject'>EclipseProject</a>, "
				+ "<a href='type://EclipseClasspath'>EclipseClasspath</a>, "
				+ "<a href='type://EclipseWtp'>EclipseWtp</a> ";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void testJavaDocLink4_mixed_arguments() {
		String line = "More examples in docs for {@link EclipseProject}, {@xxx EclipseClasspath}, {@link EclipseWtp} ";
		String expected = "More examples in docs for " + "<a href='type://EclipseProject'>EclipseProject</a>, "
				+ "{@xxx EclipseClasspath}, " + "<a href='type://EclipseWtp'>EclipseWtp</a> ";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

}
