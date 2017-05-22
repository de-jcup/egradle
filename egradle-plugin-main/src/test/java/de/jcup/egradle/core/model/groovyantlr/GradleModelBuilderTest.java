/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
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

import de.jcup.egradle.core.model.BuildContext;
import de.jcup.egradle.core.model.Error;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.ItemType;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.model.ModelBuilder.ModelBuilderException;

public class GradleModelBuilderTest {
	
	
	@Test
	public void task_clean_with_two_slides_has_child() throws Exception{
		/* @formatter:off*/
		String code = 
		"task clean << {\n"+
		"	delete {\n"+
		"		delete file(\"build-release/generated\")\n"+
		"	}\n"+
		"}\n";
		/* @formatter:on*/
		InputStream is = new ByteArrayInputStream(code.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item task = items[0];

		assertEquals(ItemType.TASK, task.getItemType());
		Item[] children = task.getChildren();
		assertNotNull(children);
		assertEquals(1, children.length);
	}

	@Test
	public void task_xyz_with_parameter_two_slides_has_child_and_type_is_detected() throws Exception{
		/* @formatter:off*/  
		String code = 
		"task xyz(type:Jar) << {\n"+
		"	delete {\n"+
		"		delete file(\"build-release/generated\")\n"+
		"	}\n"+
		"}\n";
		/* @formatter:on*/
		InputStream is = new ByteArrayInputStream(code.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item task = items[0];

		assertEquals(ItemType.TASK, task.getItemType());
		Item[] children = task.getChildren();
		assertNotNull(children);
		assertEquals(1, children.length);
		assertEquals("Jar",task.getType());
	}
	
	@Test
	public void task_xyz_with_parameter_no_slides_has_child_and_type_is_detected() throws Exception{
		/* @formatter:off*/  
		String code = 
		"task xyz(type:Jar) {\n"+
		"	delete {\n"+
		"		delete file(\"build-release/generated\")\n"+
		"	}\n"+
		"}\n";
		/* @formatter:on*/
		InputStream is = new ByteArrayInputStream(code.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item task = items[0];

		assertEquals(ItemType.TASK, task.getItemType());
		Item[] children = task.getChildren();
		assertNotNull(children);
		assertEquals(1, children.length);
		assertEquals("Jar",task.getType());
	}

	
	
	@Test
	public void test_task_dot_do_last_has_task_as_first_type_but_do_last_as_last_one() throws Exception {
		/* @formatter:off*/
		String text = 
		"		task.doLast {\n"+
		" }\n";
		/* @formatter:on*/
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item tasks = items[0];

		assertEquals(ItemType.TASK, tasks.getItemType());
		assertEquals(ItemType.DO_LAST, tasks.getLastChainedItemType());
		
	}
	
	@Test
	public void test_task_dot_gargamel_has_task_as_first_type_but_null_as_last_one() throws Exception {
		/* @formatter:off*/
		String text = 
		"		task.gargamel {\n"+
		" }\n";
		/* @formatter:on*/
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item tasks = items[0];

		assertEquals(ItemType.TASK, tasks.getItemType());
		assertEquals(null, tasks.getLastChainedItemType());
		
	}
	
	@Test
	public void test_dependencies_item_has_closure_as_parameter() throws Exception {
		/* @formatter:off*/
		String text = 
		"		dependencies{\n"+
		"			compile project(':myproject')\n"+
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
		String[] parameters = dependencies.getParameters();
		assertNotNull(parameters);
		assertEquals(1,parameters.length);
		assertEquals("groovy.lang.Closure",parameters[0]);
		
	}
	
	@Test
	public void test_name_resolved_filetree_only_closure() throws Exception {
		/* @formatter:off*/
		String text = 
		"		fileTree{\n"+
		"		}\n";
		/* @formatter:on*/
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item fileTree = items[0];

		assertEquals("fileTree", fileTree.getIdentifier());
		
	}
	
	@Test
	public void test_name_resolved_filetree_with_baseDir_and_closure() throws Exception {
		/* @formatter:off*/
		String text = 
		"		fileTree 'asDir' {\n"+
		"		}\n";
		/* @formatter:on*/
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item fileTree = items[0];

		assertEquals("fileTree", fileTree.getIdentifier());
		String[] params = fileTree.getParameters();
		assertNotNull(params);
		assertEquals(2, params.length);
		assertEquals("java.lang.String",params[0]);
		assertEquals("groovy.lang.Closure",params[1]);
		assertTrue(fileTree.isClosureBlock()); // necessary to be shown in outline view!
		
	}
	
	@Test
	public void test_parameters_resolved_filetree_with_baseDir_and_closure() throws Exception {
		/* @formatter:off*/
		String text = 
		"		fileTree baseDir {\n"+
		"		}\n";
		/* @formatter:on*/
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item fileTree = items[0];

		String[] parameters = fileTree.getParameters();
		assertNotNull(parameters);
		assertEquals(2,parameters.length);
		assertEquals("Object:baseDir", parameters[0]); // not resolveable so returns Object:name of parameter
		assertEquals("groovy.lang.Closure", parameters[1]); 
		
	}
	
	@Test
	public void test_parameters_resolved_something_with_string_and_closure() throws Exception {
		/* @formatter:off*/
		String text = 
		"		something 'test' {\n"+
		"		}\n";
		/* @formatter:on*/
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item fileTree = items[0];

		String[] parameters = fileTree.getParameters();
		assertNotNull(parameters);
		assertEquals(2,parameters.length);
		assertEquals("java.lang.String", parameters[0]);
		assertEquals("groovy.lang.Closure", parameters[1]);
		
	}
	
	@Test
	public void test_compile_my_project_contains_my_project_in_model_item_text() throws Exception {
		/* @formatter:off*/
		String text = 
		"		dependencies{\n"+
		"			compile project(':myproject')\n"+
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

		Item compileDependency = dependenciesChildren[0];
		assertEquals("project(:myproject)", compileDependency.getName());
		
	}

	@Test
	public void test_define_method__contains_child() throws Exception {
		// @formatter:off
		String code=
		"def jacocoRemoteAction(doDump, doReset, doAppend) {\n"+
		"	def  serverString = 'localhost:6300'\n"+
		"}";
		// @formatter:on
		InputStream is = new ByteArrayInputStream(code.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item methodDef = items[0];

		assertEquals("jacocoRemoteAction", methodDef.getName());

		Item[] children = methodDef.getChildren();
		assertEquals(1, children.length);
		Item variableDef = children[0];
		assertNotNull(variableDef);
		assertEquals("serverString", variableDef.getName());
	}

	@Test
	public void test_define_method__found_with_parameters() throws Exception {
		// @formatter:off
		String code=
		"def jacocoRemoteAction(doDump, doReset, doAppend) {\n"+
		"	def  serverString = 'localhost:6300'\n"+
		"}";
		// @formatter:on
		InputStream is = new ByteArrayInputStream(code.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item methodDef = items[0];

		assertEquals("jacocoRemoteAction", methodDef.getName());
		assertEquals(ItemType.METHOD, methodDef.getItemType());

		String[] parameters = methodDef.getParameters();
		assertNotNull(parameters);
		assertEquals(3, parameters.length);

		assertEquals("doDump", parameters[0]);
		assertEquals("doReset", parameters[1]);
		assertEquals("doAppend", parameters[2]);
	}

	@Test
	public void task_clean_with_dofirst_short__returns_name_clean() throws ModelBuilderException {
		String text = "task clean << {}";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item classDef = items[0];

		assertEquals("task clean <<", classDef.getName());
		assertEquals(ItemType.TASK, classDef.getItemType());

	}
	
	@Test
	public void tasks_with_type_Jar__returns_name_clean() throws ModelBuilderException {
		String text = "tasks.withType(Jar) {}";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item classDef = items[0];

		assertEquals("tasks.withType(Jar)", classDef.getName());
		assertEquals(ItemType.TASKS, classDef.getItemType());

	}

	@Test
	public void class_definition_returns_class_item() throws ModelBuilderException {
		String text = "class MyAdminTask extends DefaultTask {}";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item classDef = items[0];

		assertEquals(ItemType.CLASS, classDef.getItemType());
		assertEquals("MyAdminTask", classDef.getName());

	}
	
	
	@Test
	public void class_constructor_definitions_are_contained_as_chilren_of_class_item() throws ModelBuilderException {
		String text = "class MyAdminTask extends DefaultTask {\n"
				+ "MyAdminTask(){\n"
				+ "}\n"
				+ "}";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item classDef = items[0];

		assertEquals(ItemType.CLASS, classDef.getItemType());
		assertEquals("MyAdminTask", classDef.getName());
		
		assertTrue(classDef.hasChildren());
		Item[] children = classDef.getChildren();
		assertNotNull(children);
		assertEquals(1,children.length);
		
		Item child = children[0];
		assertNotNull(child);
		assertEquals(ItemType.CONSTRUCTOR, child.getItemType());
		
		assertEquals("MyAdminTask", child.getName());
	}
	
	@Test
	public void class_public_method1_with_annotations_definitions_are_contained_as_chilren_of_class_item() throws ModelBuilderException {
		String text = "class MyAdminTask extends DefaultTask {\n"
				+ "@Deprecated\n"
				+ "public void method1(){\n"
				+ "}\n"
				+ "}";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item classDef = items[0];

		assertEquals(ItemType.CLASS, classDef.getItemType());
		assertEquals("MyAdminTask", classDef.getName());
		
		assertTrue(classDef.hasChildren());
		Item[] children = classDef.getChildren();
		assertNotNull(children);
		assertEquals(1,children.length);
		
		Item child = children[0];
		assertNotNull(child);
		assertEquals(ItemType.METHOD, child.getItemType());
		
		assertEquals("method1", child.getName());
	}
	
	@Test
	public void interface_public_method1_definitions_are_contained_as_chilren_of_interface_item() throws ModelBuilderException {
		String text = "interface People {\n"
				+ "public String talk() "
				+ "}";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item classDef = items[0];

		assertEquals(ItemType.INTERFACE, classDef.getItemType());
		assertEquals("People", classDef.getName());
		
		assertTrue(classDef.hasChildren());
		Item[] children = classDef.getChildren();
		assertNotNull(children);
		assertEquals(1,children.length);
		
		Item child = children[0];
		assertNotNull(child);
		assertEquals(ItemType.METHOD, child.getItemType());
		
		assertEquals("talk", child.getName());
	}
	
	@Test
	public void enum_defintion_contains_also_entries() throws ModelBuilderException {
		String text = "enum Directions {\n"
				+ "NORTH, SOUTH, WEST, EAST "
				+ "}";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item classDef = items[0];

		assertEquals(ItemType.ENUM, classDef.getItemType());
		assertEquals("Directions", classDef.getName());
		
		assertTrue(classDef.hasChildren());
		Item[] children = classDef.getChildren();
		assertNotNull(children);
		assertEquals(4,children.length);
		
		Item child = children[0];
		assertNotNull(child);
		assertEquals(ItemType.ENUM_CONSTANT, child.getItemType());
		
		assertEquals("NORTH", child.getName());
		
		child = children[1];
		assertNotNull(child);
		assertEquals(ItemType.ENUM_CONSTANT, child.getItemType());
		
		assertEquals("SOUTH", child.getName());
		
		child = children[2];
		assertNotNull(child);
		assertEquals(ItemType.ENUM_CONSTANT, child.getItemType());
		
		assertEquals("WEST", child.getName());
		
		child = children[3];
		assertNotNull(child);
		assertEquals(ItemType.ENUM_CONSTANT, child.getItemType());
		
		assertEquals("EAST", child.getName());
	}
	
	@Test
	public void class_public_method1_and_method2_definitions_are_contained_as_chilren_of_class_item() throws ModelBuilderException {
		String text = "class MyAdminTask extends DefaultTask {\n"
				+ "public void method1(){\n"
				+ "}\n"
				+ "public String method2(){\n"
				+ " return 'test';\n"
				+ "}\n"
				+ "}";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item classDef = items[0];

		assertEquals(ItemType.CLASS, classDef.getItemType());
		assertEquals("MyAdminTask", classDef.getName());
		
		assertTrue(classDef.hasChildren());
		Item[] children = classDef.getChildren();
		assertNotNull(children);
		assertEquals(2,children.length);
		
		Item child1 = children[0];
		assertNotNull(child1);
		
		assertEquals("method1", child1.getName());
		assertEquals(ItemType.METHOD, child1.getItemType());
		
		Item child2 = children[1];
		assertNotNull(child2);
		
		assertEquals("method2", child2.getName());
		assertEquals(ItemType.METHOD, child2.getItemType());
	}
	
	@Test
	public void class_public_variable1_method1__definitions_are_contained_as_chilren_of_class_item() throws ModelBuilderException {
		String text = "class MyAdminTask extends DefaultTask {\n"
				+ "private String data='test';\n"
				+ "\n"
				+ "public String method2(){\n"
				+ " return data;\n"
				+ "}\n"
				+ "}";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item classDef = items[0];

		assertEquals(ItemType.CLASS, classDef.getItemType());
		assertEquals("MyAdminTask", classDef.getName());
		
		assertTrue(classDef.hasChildren());
		Item[] children = classDef.getChildren();
		assertNotNull(children);
		assertEquals(2,children.length);
		
		Item child1 = children[0];
		assertNotNull(child1);
		
		assertEquals("data", child1.getName());
		assertEquals(ItemType.VARIABLE, child1.getItemType());
		Item child2 = children[1];
		assertNotNull(child2);
		
		assertEquals("method2", child2.getName());
		assertEquals(ItemType.METHOD, child2.getItemType());
	}

	@Test
	public void package_definition_returns_package_item() throws ModelBuilderException {
		String text = "package de.jcup.egradle.examples";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item packageDef = items[0];

		assertEquals(ItemType.PACKAGE, packageDef.getItemType());
		assertEquals("de.jcup.egradle.examples", packageDef.getName());

	}

	@Test
	public void import_definition_returns_import_item() throws ModelBuilderException {
		String text = "import org.gradle.api.DefaultTask";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item importDef = items[0];

		assertEquals(ItemType.IMPORT, importDef.getItemType());
		assertEquals("org.gradle.api.DefaultTask", importDef.getName());

	}

	@Test
	public void dependencies_with_classpath_gradle_api__contains_classpath_gradleapi() throws ModelBuilderException {
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
		assertEquals("gradleApi()", classPathDependency.getName());

	}

	@Test
	public void three_variable_definitions_second_defintion_has_failure_on_line2__context_must_have_error_for_line2()
			throws ModelBuilderException {
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
	public void task_without_type__and__execution_parts() throws ModelBuilderException {
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

		assertEquals("task jacocoRemoteDump() <<", taskClosure.getName());
		assertEquals(ItemType.TASK, taskClosure.getItemType());

	}

	@Test
	public void task_without_type() throws ModelBuilderException {
		String text = "task jacocoRemoteDump() {}";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item taskClosure = items[0];

		assertEquals("task jacocoRemoteDump", taskClosure.getName());
		assertEquals(ItemType.TASK, taskClosure.getItemType());

	}

	@Test
	public void dependencies__contains_compile_with_parameter_all() throws ModelBuilderException {
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
	public void dependencies__contains_testCompile_with_name() throws ModelBuilderException {
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
	public void simple_string() throws Exception {
		String text = "String cp = ''";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item varaibleDef = items[0];

		assertEquals(ItemType.VARIABLE, varaibleDef.getItemType());
		assertEquals("cp", varaibleDef.getName());
	}

	@Test
	public void groovy_string() throws Exception {
		String text = "String cp = \"\"";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item varaibleDef = items[0];

		assertEquals(ItemType.VARIABLE, varaibleDef.getItemType());
		assertEquals("cp", varaibleDef.getName());
	}

	@Test
	public void def_groovy_string() throws Exception {
		String text = "def String cp = \"\"";

		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		Item varaibleDef = items[0];

		assertEquals(ItemType.VARIABLE, varaibleDef.getItemType());
		assertEquals("cp", varaibleDef.getName());
	}

	@Test
	public void variable_definition_in_one_line__item_created_has_correct_type() throws Exception {
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
	public void two_variable_definitions_in_two_lines__types_and_offset_are_correct() throws Exception {
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
	public void closure_named_test1234_contains_variable1_is_inside_tree() throws Exception {
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
	public void task_doit_contains_variable1_is_inside_tree() throws Exception {
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
		assertEquals("task doit", taskDef.getName());
		assertEquals(ItemType.TASK, taskDef.getItemType());

		items = taskDef.getChildren();

		assertEquals("closure children amount not as expected. ", 1, items.length);
		Item variable1Item = items[0];
		assertEquals(ItemType.VARIABLE, variable1Item.getItemType());
		assertEquals("variable1", variable1Item.getName());

	}

	@Test
	public void task_doit_type_compile_contains_variable1_is_inside_tree() throws Exception {
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
		assertEquals("task doit", taskDef.getName());
		assertEquals("compile", taskDef.getType());
		assertEquals(ItemType.TASK, taskDef.getItemType());

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
		assertEquals("xyz.sonarrunner.dependson check", taskSetupItem.getName());

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
		assertEquals("tasks.sonarrunner.dependson check", taskSetupItem.getName());
		assertEquals(ItemType.TASKS, taskSetupItem.getItemType());

	}
	
	@Test
	public void assign_map() throws Exception{
		// @formatter:off
		String code = 
		"ext {                                                                    \n"+
		"	                                                                      \n"+
		"	 library = [                                                    \n"+
		"		                                                                  \n"+
		"		junit:							\"junit:junit:4.9\",                \n"+
		"		mockito_all:					\"org.mockito:mockito-all:1.8.5\",  \n"+
		"	]                                                                     \n"+
		"	                                                                      \n"+
		"}                                                                        \n";
		// @formatter:on
		InputStream is = new ByteArrayInputStream(code.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);

		Item extItem = items[0];
		assertEquals(ItemType.CLOSURE, extItem.getItemType());
		assertTrue(extItem.hasChildren());
		Item[] children = extItem.getChildren();
		assertEquals(1, children.length);
		Item libraryItem = children[0];
		assertEquals(ItemType.ASSIGNMENT, libraryItem.getItemType());
		assertEquals("library", libraryItem.getName());
	}
	
	@Test
	public void assign_map_with_map_inside() throws Exception{
		// @formatter:off
		String code = 
		"ext {                                                                    \n"+
		"	                                                                      \n"+
		"	 library = [                                                    \n"+
		"		                                                                  \n"+
		"		junit:							\"junit:junit:4.9\",                \n"+
		"		mockito_all:					\"org.mockito:mockito-all:1.8.5\",  \n"+
		"       entries = [ \n"+
		"                      key:'value'\n"+
		"	    ]                                                                     \n"+
		"	]                                                                     \n"+
		"	                                                                      \n"+
		"}                                                                        \n";
		// @formatter:on
		InputStream is = new ByteArrayInputStream(code.getBytes());
		GradleModelBuilder b = new GradleModelBuilder(is);

		/* execute */
		Model model = b.build(null);

		/* test */
		Item[] items = model.getRoot().getChildren();

		/* level 0*/
		assertEquals(1, items.length);
		
		/* level 1*/
		Item extItem = items[0];
		assertEquals(ItemType.CLOSURE, extItem.getItemType());
		assertTrue(extItem.hasChildren());
		Item[] children = extItem.getChildren();
		assertEquals(1, children.length);
		Item libraryItem = children[0];
		assertEquals(ItemType.ASSIGNMENT, libraryItem.getItemType());
		assertEquals("library", libraryItem.getName());
		
		/* level 2*/
		assertTrue(libraryItem.hasChildren());
		Item[] children2 = libraryItem.getChildren();
		assertEquals(1, children2.length);
		Item entriesItem = children2[0];
		assertEquals(ItemType.ASSIGNMENT, entriesItem.getItemType());
		assertEquals("entries", entriesItem.getName());
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
		assertEquals("apply from", taskSetupItem.getName());
		assertEquals("rootProject.projectDir/libraries.gradle", taskSetupItem.getTarget());
		assertEquals(ItemType.APPLY_FROM, taskSetupItem.getItemType());

	}

}
