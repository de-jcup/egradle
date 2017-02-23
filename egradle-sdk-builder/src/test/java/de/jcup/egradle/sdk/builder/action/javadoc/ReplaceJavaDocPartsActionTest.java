package de.jcup.egradle.sdk.builder.action.javadoc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
	public void testJavaDocParts_empty_lines_are_not_removed(){
		String line="abc\n\nxyz\n";
		assertEquals("abc\n\nxyz\n",actionToTest.replaceJavaDocParts(line));
	}
	
	@Test
	public void testJavaDocParts_empty_lines_are_not_removed_also_when_comments(){
		String line="abc//comment\n\nxyz//comment\n";
		assertEquals("abc<em class='comment'>//comment</em>\n\nxyz<em class='comment'>//comment</em>\n",actionToTest.replaceJavaDocParts(line));
	}
	
	@Test
	public void testJavaDocParts_comment_code_before__replaces_multiple_lines(){
		String line="xyz // comment1\nabc // comment2\n";
		assertEquals("xyz <em class='comment'>// comment1</em>\nabc <em class='comment'>// comment2</em>\n",actionToTest.replaceJavaDocParts(line));
	}
	
	@Test
	public void testJavaDocParts_comment_full_line_but_but_opening_tagidentifier_before(){
		String line="<// my comment";
		assertEquals("<// my comment",actionToTest.replaceJavaDocParts(line));
	}
	
	@Test
	public void testJavaDocParts_comment_full_line_but_but_opening_and_closing_tag_identifierbefore(){
		String line="<>// my comment";
		assertEquals("<><em class='comment'>// my comment</em>",actionToTest.replaceJavaDocParts(line));
	}
	
	@Test
	public void testJavaDocParts_comment_full_line_but_but_opening_and_closing_tag_identifierbefore_and_opening_agin(){
		String line="<><// my comment";
		assertEquals("<><// my comment",actionToTest.replaceJavaDocParts(line));
	}
	
	@Test
	public void testJavaDocParts_comment_full_line(){
		String line="// my comment";
		assertEquals("<em class='comment'>// my comment</em>",actionToTest.replaceJavaDocParts(line));
	}
	
	@Test
	public void testJavaDocParts_comment_line_has_some_chars_before(){
		String line="xyz // my comment";
		assertEquals("xyz <em class='comment'>// my comment</em>",actionToTest.replaceJavaDocParts(line));
	}
	
	@Test
	public void testJavaDocParts_comment_line_has_some_whitespaces_before(){
		String line= "              // my comment";
		assertEquals("              <em class='comment'>// my comment</em>",actionToTest.replaceJavaDocParts(line));
	}
	
	@Test
	public void testJavaDocParts_full_code_block() {
		StringBuilder lines = new StringBuilder();

		lines.append("   wtp {\n");
		lines.append("     component {\n");
		lines.append("       //for examples see docs for {@link EclipseWtpComponent}\n");
		lines.append("     }\n");
		lines.append("\n");
		lines.append("     facet {\n");
		lines.append("       //for examples see docs for {@link EclipseWtpFacet}\n");
		lines.append("     }\n");
		lines.append("   }\n");
		
		StringBuilder expectedLines = new StringBuilder();

		expectedLines.append("   wtp {\n");
		expectedLines.append("     component {\n");
		expectedLines.append("       <em class='comment'>//for examples see docs for <a href='type://EclipseWtpComponent'>EclipseWtpComponent</a></em>\n");
		expectedLines.append("     }\n");
		expectedLines.append("\n");
		expectedLines.append("     facet {\n");
		expectedLines.append("       <em class='comment'>//for examples see docs for <a href='type://EclipseWtpFacet'>EclipseWtpFacet</a></em>\n");
		expectedLines.append("     }\n");
		expectedLines.append("   }\n");
		
		/* execute */
		String result = actionToTest.replaceJavaDocParts(lines.toString());
		
		/* test */
		assertEquals(expectedLines.toString(),result);
	}

	@Test
	public void testjavaDocParts_links_with_multiple_text(){
		String linkText="{@link org.gradle.plugin.use.PluginDependenciesSpec plugins script block}";
		String expectedText="<a href='type://org.gradle.plugin.use.PluginDependenciesSpec'>plugins script block</a>";
		assertEquals(expectedText, actionToTest.replaceJavaDocParts(linkText));
	}
	
	@Test
	public void testJavadocLinkConversion_simple_link_with_full_path_name() {
		String line = "{@link org.gradle.api.invocation.Gradle}";
		String expected = "<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}
	
	@Test
	public void testJavadocLinkConversion_additional_curlyBracketBefore_simple_link_with_full_path_name() {
		String line = "{{@link org.gradle.api.invocation.Gradle}";
		String expected = "{<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}
	
	@Test
	public void testJavadocLinkConversion_additional_curlyBracketAfter_simple_link_with_full_path_name() {
		String line = "{@link org.gradle.api.invocation.Gradle}}";
		String expected = "<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>}";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}
	
	@Test
	public void testJavadocLinkConversion_additional_curlyBracketOpening_a_curlyBracketClosing_Before_simple_link_with_full_path_name() {
		String line = "{a}{@link org.gradle.api.invocation.Gradle}";
		String expected = "{a}<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
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
		for (String line : list) {
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
