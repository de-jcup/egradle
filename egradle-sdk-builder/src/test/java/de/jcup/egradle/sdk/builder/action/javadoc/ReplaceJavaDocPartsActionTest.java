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
package de.jcup.egradle.sdk.builder.action.javadoc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.Type;

/**
 * @see ReplaceJavaDocPartsAction#replaceJavaDocParts(String)
 * @see ReplaceJavaDocPartsAction#handleTypeLinksWithoutType(String, Type)
 * @see ReplaceJavaDocPartsAction
 * @see #actionToTest
 * @author Albert Tregnaghi
 *
 */
public class ReplaceJavaDocPartsActionTest {

	private ReplaceJavaDocPartsAction actionToTest;

	@Before
	public void before() {
		actionToTest = new ReplaceJavaDocPartsAction();
	}

	@Test
	public void ankers_without_content_are_removed() {
		/*
		 * at least chrome and IE browsers have problems with this: its done in
		 * org.gradle.api.Project
		 */
		String text = "xyz <a name=\"properties\"/> bla";
		assertEquals("xyz  bla", actionToTest.replaceJavaDocParts(text));
	}

	@Test
	public void ankers_with_empty_content_but_start_and_end_tag_are_NOT_removed() {
		/*
		 * at least chrome and IE browsers have problems with this: its done in
		 * org.gradle.api.Project
		 */
		String text = "xyz <a name=\"properties\"></a> bla";
		assertEquals("xyz <a name=\"properties\"></a> bla", actionToTest.replaceJavaDocParts(text));
	}

	@Test
	public void ankers_with_contentare_NOT_removed_name() {
		/*
		 * at least chrome and IE browsers have problems with this: its done in
		 * org.gradle.api.Project
		 */
		String text = "xyz <a name=\"properties\">test</a> bla";
		assertEquals("xyz <a name=\"properties\">test</a> bla", actionToTest.replaceJavaDocParts(text));
	}

	@Test
	public void ankers_with_contentare_NOT_removed_href() {
		/*
		 * at least chrome and IE browsers have problems with this: its done in
		 * org.gradle.api.Project
		 */
		String text = "xyz <a href=\"properties\">test</a> bla";
		assertEquals("xyz <a href=\"properties\">test</a> bla", actionToTest.replaceJavaDocParts(text));
	}

	@Test
	public void value_replaced_with_text() {
		String text = "{@value org.gradle.api.initialization.Settings#DEFAULT_SETTINGS_FILE}";
		assertEquals("<em class='value'>org.gradle.api.initialization.Settings#DEFAULT_SETTINGS_FILE</em>",
				actionToTest.replaceJavaDocParts(text));
	}

	@Test
	public void empty_lines_are_not_removed() {
		/* prepare */
		String line = "abc\n\nxyz\n";
		/* execute +test */
		assertEquals("abc\n\nxyz\n", actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void since_replaced_by_text() {
		String line = "@since 3.0\n";
		/* execute +test */
		assertEquals("<br>(since 3.0)\n", actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void since_with_more_text_replaced_by_text() {
		String line = "@since 3.0 garbage\n";
		/* execute +test */
		assertEquals("<br>(since 3.0 garbage)\n", actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void see_with_link_inside_converted_to_link() {
		/* @formatter:off*/
		String text = "@see <a href=\"http://help.eclipse.org/mars/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/IAccessRule.html\">IAccessRule Javadoc</a>\n@since 3.0";
		String expected = "(see <a href=\"http://help.eclipse.org/mars/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/IAccessRule.html\">IAccessRule Javadoc</a>)\n<br>(since 3.0)";
		
		assertEquals(expected, actionToTest.replaceJavaDocParts(text));
		/* @formatter:on*/
	}

	@Test
	public void see_with_type__converted_to_link() {
		/* prepare */
		String text = "@see MyType";
		String expected = "(see <a href='type://MyType'>MyType</a>)";
		/* execute + test */
		assertEquals(expected, actionToTest.replaceJavaDocParts(text));
	}

	@Test
	public void see_no_type_with_method__converted_to_link() {
		/* prepare */
		String text = " @see #copy(Closure)";
		String expected = " (see <a href='type://#copy(Closure)'>#copy(Closure)</a>)";
		/* execute + test */
		assertEquals(expected, actionToTest.replaceJavaDocParts(text));
	}

	@Test
	public void see_type_with_method__converted_to_link() {
		/* prepare */
		String text = "@see MyType#copy(Closure)";
		String expected = "(see <a href='type://MyType#copy(Closure)'>MyType#copy(Closure)</a>)";
		/* execute + test */
		assertEquals(expected, actionToTest.replaceJavaDocParts(text));
	}

	@Test
	public void see_type_with_property__converted_to_link() {
		/* prepare */
		String text = "@see MyType#version";
		String expected = "(see <a href='type://MyType#version'>MyType#version</a>)";
		/* execute + test */
		assertEquals(expected, actionToTest.replaceJavaDocParts(text));
	}

	@Test
	public void empty_lines_are_not_removed_also_when_comments() {
		String line = "abc//comment\n\nxyz//comment\n";
		assertEquals("abc<em class='comment'>//comment</em>\n\nxyz<em class='comment'>//comment</em>\n",
				actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void comment_code_before__replaces_multiple_lines() {
		String line = "xyz // comment1\nabc // comment2\n";
		assertEquals("xyz <em class='comment'>// comment1</em>\nabc <em class='comment'>// comment2</em>\n",
				actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void comment_full_line_but_but_opening_tagidentifier_before() {
		String line = "<// my comment";
		assertEquals("<// my comment", actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void comment_full_line_but_but_opening_and_closing_tag_identifierbefore() {
		String line = "<>// my comment";
		assertEquals("<><em class='comment'>// my comment</em>", actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void comment_full_line_but_but_opening_and_closing_tag_identifierbefore_and_opening_agin() {
		String line = "<><// my comment";
		assertEquals("<><// my comment", actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void comment_full_line() {
		String line = "// my comment";
		assertEquals("<em class='comment'>// my comment</em>", actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void comment_line_has_some_chars_before() {
		String line = "xyz // my comment";
		assertEquals("xyz <em class='comment'>// my comment</em>", actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void comment_line_has_some_whitespaces_before() {
		String line = "              // my comment";
		assertEquals("              <em class='comment'>// my comment</em>", actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void full_code_block() {
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
		expectedLines.append(
				"       <em class='comment'>//for examples see docs for <a href='type://EclipseWtpComponent'>EclipseWtpComponent</a></em>\n");
		expectedLines.append("     }\n");
		expectedLines.append("\n");
		expectedLines.append("     facet {\n");
		expectedLines.append(
				"       <em class='comment'>//for examples see docs for <a href='type://EclipseWtpFacet'>EclipseWtpFacet</a></em>\n");
		expectedLines.append("     }\n");
		expectedLines.append("   }\n");

		/* execute */
		String result = actionToTest.replaceJavaDocParts(lines.toString());

		/* test */
		assertEquals(expectedLines.toString(), result);
	}

	@Test
	public void links_with_multiple_text() {
		String linkText = "{@link org.gradle.plugin.use.PluginDependenciesSpec plugins script block}";
		String expectedText = "<a href='type://org.gradle.plugin.use.PluginDependenciesSpec'>plugins script block</a>";
		assertEquals(expectedText, actionToTest.replaceJavaDocParts(linkText));
	}

	@Test
	public void simple_link_with_full_path_name() {
		String line = "{@link org.gradle.api.invocation.Gradle}";
		String expected = "<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void additional_curlyBracketBefore_simple_link_with_full_path_name() {
		String line = "{{@link org.gradle.api.invocation.Gradle}";
		String expected = "{<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void additional_curlyBracketAfter_simple_link_with_full_path_name() {
		String line = "{@link org.gradle.api.invocation.Gradle}}";
		String expected = "<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>}";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void additional_curlyBracketOpening_a_curlyBracketClosing_Before_simple_link_with_full_path_name() {
		String line = "{a}{@link org.gradle.api.invocation.Gradle}";
		String expected = "{a}<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void simple_link_with_full_path_name__whitespaces_after_curly() {
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
	public void simple_link_with_full_path_name__whitespaces_after_curly__and_after_link() {
		String line = "{      @link     org.gradle.api.invocation.Gradle}";
		String expected = "<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void simple_link_with_short_path_name() {
		String line = "{@link EclipseProject}";
		String expected = "<a href='type://EclipseProject'>EclipseProject</a>";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void parameter_conversion() {
		String line = "  @param    name1 a description";
		String expected = "  <br><b class='param'>param:</b>name1 a description";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void parameter_conversion2() {

		String line = " * @param gradle The build which has been loaded. Never null.";
		line = actionToTest.removeWhitespacesAndStars(line);
		String expected = " <br><b class='param'>param:</b>gradle The build which has been loaded. Never null.";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void parameter_conversion3() throws IOException {
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
	public void parameter_conversion5_two_params_following() throws Exception {
		String line = "  @param    name1 a description\n  @param    name2 a description\n";
		String converted = actionToTest.replaceJavaDocParts(line);
		assertTrue(converted.indexOf("@param") == -1);
	}

	@Test
	public void parameter_conversion4() throws Exception {
		/* prepare */
		String description = "{@code xyz} bla bla";

		/* execute */
		String transformed = actionToTest.replaceJavaDocParts(description);

		/* test */
		assertTrue(transformed.indexOf("@code") == -1);

	}

	@Test
	public void parameter_conversion_param_replaced() throws Exception {
		/* prepare */
		String fullDescription = "@param xyz bla bla";

		/* execute */
		String transformed = actionToTest.replaceJavaDocParts(fullDescription);

		/* test */
		assertTrue(transformed.indexOf("@param") == -1);

	}

	@Test
	public void return_converted() {
		String line = "  @return name1 a description";
		String expected = "  <br><br><b class='return'>returns:</b>name1 a description";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void simple_link_with_short_path_name_but_prefix() {
		String line = "More examples in docs for {@link EclipseProject}";
		String expected = "More examples in docs for <a href='type://EclipseProject'>EclipseProject</a>";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void link_3() {
		String line = "More examples in docs for { @link EclipseProject}, {@link EclipseClasspath}, {@link EclipseWtp} ";
		String expected = "More examples in docs for " + "<a href='type://EclipseProject'>EclipseProject</a>, "
				+ "<a href='type://EclipseClasspath'>EclipseClasspath</a>, "
				+ "<a href='type://EclipseWtp'>EclipseWtp</a> ";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

	@Test
	public void link_4_mixed_arguments() {
		String line = "More examples in docs for {@link EclipseProject}, {@xxx EclipseClasspath}, {@link EclipseWtp} ";
		String expected = "More examples in docs for " + "<a href='type://EclipseProject'>EclipseProject</a>, "
				+ "{@xxx EclipseClasspath}, " + "<a href='type://EclipseWtp'>EclipseWtp</a> ";
		assertEquals(expected, actionToTest.replaceJavaDocParts(line));
	}

}
