package de.jcup.egradle.core.model.groovyantlr;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import de.jcup.egradle.core.model.BuildContext;
import de.jcup.egradle.core.model.Error;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.ItemType;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.model.ModelBuilder.OutlineModelBuilderException;
import de.jcup.egradle.core.model.groovyantlr.GradleModelBuilder;

public class GradleModelBuilderTest {
	
	@Test
	public void test_dependencies_with_classpath_gradle_api__contains_classpath_gradleapi() throws OutlineModelBuilderException {
		/* @formatter:off*/
		String text = 
		"		dependencies{\n"+
		"			classpath gradleApi()\n"+
		"		}\n";
		/* @formatter:on*/
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item dependencies = items[0];

		assertEquals(ItemType.DEPENDENCIES, dependencies.getItemType());
		Item[] dependenciesChildren = dependencies.getChildren();
		assertEquals(1, dependenciesChildren.length);
		
		Item classPathDependency = dependenciesChildren[0];
		assertEquals("gradleApi()",classPathDependency.getName());

	}
	@Test
	public void test_three_variable_definitions_second_defintion_has_failure_on_line2__context_must_have_error_for_line2()
			throws OutlineModelBuilderException {
		/* prepare */
		// @formatter:off
		String text = 
		"		def var1='correct'\n"+
		"		def var2='missing\n"+
		"	    def var3='would be correct'\n";
		// @formatter:on
		System.out.println(text);
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		BuildContext context = new BuildContext();
		/* execute */
		Model model = b.build(context);

		/* test */
		assertNotNull("Modle should never be null, even on failures!", model);
		assertTrue(context.hasErrors());
		assertEquals(1, context.getErrors().size());

		Error error = context.getErrors().get(0);
		assertNotNull(error);
		assertEquals(2, error.getLineNumber());

	}

	@Test
	public void test_task_without_type__and__execution_parts() throws OutlineModelBuilderException {
		/* @formatter:off*/
		String text = 
		"		task jacocoRemoteDump() {\n"+
		"			group gemsGroup\n"+
		"			description \"Read ... Default is localhost.\"\n"+
		"		} << {\n"+
		"			jacocoRemoteAction(\n"+
		"				true, // dump to file\n"+
		"				false, // reset coverage information on remote application\n"+
		"				true // append coverage information to local file instead of overwriting existing local data\n"+
		"				)\n"+
		"		}\n";
		/* @formatter:on*/
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item taskClosure = items[0];

		assertEquals(ItemType.TASK_CLOSURE, taskClosure.getItemType());
		assertEquals("task jacocoRemoteDump", taskClosure.getName());

	}

	@Test
	public void test_task_without_type() throws OutlineModelBuilderException {
		String text = "task jacocoRemoteDump() {}";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item taskClosure = items[0];

		assertEquals(ItemType.TASK_CLOSURE, taskClosure.getItemType());
		assertEquals("task jacocoRemoteDump", taskClosure.getName());

	}

	@Test
	public void test_dependencies__contains_compile_with_parameter_all() throws OutlineModelBuilderException {
		String text = "dependencies {\n"
				+ "compile group: 'org.codehaus.groovy', name: 'groovy-all', version: '2.4.7'\n" + "}";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item variable1Item = items[0];

		assertEquals(ItemType.DEPENDENCIES, variable1Item.getItemType());
		assertEquals("dependencies", variable1Item.getName());

		Item[] dependencies = variable1Item.getChildren();
		assertEquals(1, dependencies.length);

		Item junitDependency = dependencies[0];
		assertEquals(ItemType.DEPENDENCY, junitDependency.getItemType());
		assertEquals("group:org.codehaus.groovy, name:groovy-all, version:2.4.7", junitDependency.getName());
		assertEquals("compile", junitDependency.getConfiguration());

	}

	@Test
	public void test_dependencies__contains_testCompile_with_name() throws OutlineModelBuilderException {
		String text = "dependencies {\ntestCompile library.junit}";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item variable1Item = items[0];

		assertEquals(ItemType.DEPENDENCIES, variable1Item.getItemType());
		assertEquals("dependencies", variable1Item.getName());

		Item[] dependencies = variable1Item.getChildren();
		assertEquals(1, dependencies.length);

		Item junitDependency = dependencies[0];
		assertEquals(ItemType.DEPENDENCY, junitDependency.getItemType());
		assertEquals("library.junit", junitDependency.getName());
		assertEquals("testCompile", junitDependency.getConfiguration());

	}

	@Test
	public void test1_variable_definitions_in_one_line__item_created_has_correct_type() throws Exception {
		/* prepare */
		String text = "def variable1='Hello world'";
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item variable1Item = items[0];

		assertEquals(ItemType.VARIABLE, variable1Item.getItemType());
		assertEquals("variable1", variable1Item.getName());
		assertEquals(text.length(), variable1Item.getLength());
		assertEquals(0, variable1Item.getOffset());

	}

	@Test
	public void test2_variable_definitions_in_two_lines__types_and_offset_are_correct() throws Exception {
		/* prepare */
		String text = "def variable1='Hello world... from groovy'\n\n\n";
		int expectedOffsetOfVariable2 = text.length();
		text += "def variable2='Hello world... from groovy'";
		System.out.println("length2=" + text.length());
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(2, items.length);
		Item variable1Item = items[0];
		Item variable2Item = items[1];

		assertEquals(ItemType.VARIABLE, variable1Item.getItemType());
		assertEquals("variable1", variable1Item.getName());
		assertEquals(1, variable1Item.getColumn());
		assertEquals(1, variable1Item.getLine());

		assertEquals(ItemType.VARIABLE, variable2Item.getItemType());
		assertEquals("variable2", variable2Item.getName());
		assertEquals(1, variable2Item.getColumn());
		assertEquals(4, variable2Item.getLine());

		assertEquals(expectedOffsetOfVariable2, variable2Item.getOffset());
		assertEquals(0, variable1Item.getOffset());
	}

	@Test
	public void test_closure_named_test1234_contains_variable1_is_inside_tree() throws Exception {
		/* prepare */
		String text = "test1234{\n\ndef variable1='Hello world'\n}";
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);

		Item closureItem = items[0];
		assertEquals(ItemType.CLOSURE, closureItem.getItemType());
		assertEquals("test1234", closureItem.getName());

		items = closureItem.getChildren();

		assertEquals("closure children amount not as expected. ", 1, items.length);
		Item variable1Item = items[0];
		assertEquals(ItemType.VARIABLE, variable1Item.getItemType());
		assertEquals("variable1", variable1Item.getName());

	}

	@Test
	public void test_task_doit_contains_variable1_is_inside_tree() throws Exception {
		/* prepare */
		String text = "task doit{\n\ndef variable1='Hello world'\n}";
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);

		Item taskDef = items[0];
		assertEquals(ItemType.TASK_CLOSURE, taskDef.getItemType());
		assertEquals("task doit", taskDef.getName());

		items = taskDef.getChildren();

		assertEquals("closure children amount not as expected. ", 1, items.length);
		Item variable1Item = items[0];
		assertEquals(ItemType.VARIABLE, variable1Item.getItemType());
		assertEquals("variable1", variable1Item.getName());

	}

	@Test
	public void test_task_doit_type_compile_contains_variable1_is_inside_tree() throws Exception {
		/* prepare */
		String text = "task doit(type: compile){\n\ndef variable1='Hello world'\n}";
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);

		Item taskDef = items[0];
		assertEquals(ItemType.TASK_CLOSURE, taskDef.getItemType());
		assertEquals("task doit", taskDef.getName());
		assertEquals("compile", taskDef.getType());

		items = taskDef.getChildren();

		assertEquals("closure children amount not as expected. ", 1, items.length);
		Item variable1Item = items[0];
		assertEquals(ItemType.VARIABLE, variable1Item.getItemType());
		assertEquals("variable1", variable1Item.getName());

	}

	@Test
	public void xyz_sonarrunner_depens_on_results_in_item_methd_call_of_xyz_sonarrunner_depends_on() throws Exception {
		/* prepare */
		String text = "xyz.sonarrunner.dependson check";
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);

		Item taskSetupItem = items[0];
		assertEquals(ItemType.METHOD_CALL, taskSetupItem.getItemType());
		assertEquals("xyz.sonarrunner.dependson", taskSetupItem.getName());

	}

	@Test
	public void tasks_sonarrunner_depens_on_results_in_item_setup_of_task_sonarrunner_depends_on() throws Exception {
		/* prepare */
		String text = "tasks.sonarrunner.dependson check";
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);

		Item taskSetupItem = items[0];
		assertEquals(ItemType.TASK_SETUP, taskSetupItem.getItemType());
		assertEquals("tasks.sonarrunner.dependson", taskSetupItem.getName());

	}

	@Test
	public void apply_plugin_java() throws Exception {
		/* prepare */
		String text = "apply plugin: 'java'";
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);

		Item taskSetupItem = items[0];
		assertEquals(ItemType.APPLY_PLUGIN, taskSetupItem.getItemType());
		assertEquals("java", taskSetupItem.getTarget());
		assertEquals("apply plugin", taskSetupItem.getName());

	}

	@Test
	public void apply_from_bla() throws Exception {
		/* prepare */
		String text = "apply from: 'bla'";
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);

		Item taskSetupItem = items[0];
		assertEquals(ItemType.APPLY_FROM, taskSetupItem.getItemType());
		assertEquals("apply from", taskSetupItem.getName());
		assertEquals("bla", taskSetupItem.getTarget());

	}

	@Test
	public void apply_from_with_gstring_containing_variables() throws Exception {
		/* prepare */
		String text = "apply from: \"${rootProject.projectDir}/libraries.gradle\"";
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);

		Item taskSetupItem = items[0];
		assertEquals(ItemType.APPLY_FROM, taskSetupItem.getItemType());
		assertEquals("apply from", taskSetupItem.getName());
		assertEquals("rootProject.projectDir/libraries.gradle", taskSetupItem.getTarget());

	}

}
