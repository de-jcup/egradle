package de.jcup.egradle.ide;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.template.Features;
import de.jcup.egradle.template.FileStructureTemplate;

public class NewProjectContextTest {

	private static final String PROJECT_NAME = "project1";
	private NewProjectContext contextToTest;
	private FileStructureTemplate mockedTemplate;
	@Before
	public void before(){
		mockedTemplate = mock(FileStructureTemplate.class);

		contextToTest = new NewProjectContext();
		contextToTest.setSelectedTemplate(mockedTemplate);
		contextToTest.setProjectName(PROJECT_NAME);
		
	}
	
	@Test
	public void when_validation_ok_an_old_problem_message_is_replaced_by_null(){
		/* prepare */
		contextToTest.lastValidationProblem="error";
		assertNotNull(contextToTest.getLastValidationProblem());

		/*execute */
		boolean valid = contextToTest.validate();

		/* test */
		assertTrue(valid);
		assertNull(contextToTest.getLastValidationProblem());
	}
	
	@Test
	public void not_valid_when_no_project_name_set() {
		/* prepare */
		contextToTest.setProjectName(null);

		/* test */
		assertFalse(contextToTest.validate());
	}
	
	@Test
	public void valid_when_template_set_but_supports_no_feature_at_all() {
		/* test - mocks will always return false for features...*/
		assertTrue(contextToTest.validate());
	}
	
	@Test
	public void valid_when_multiproject_is_set_and_multiprojects_not_empty() {
		/* prepare */
		when(mockedTemplate.hasFeature(Features.NEW_PROJECT__TYPE_MULTI_PROJECT)).thenReturn(true);
		contextToTest.setMultiProjects("ui,server,core");
	
		assertTrue(contextToTest.validate());
	}
	
	@Test
	public void not_valid_when_multiproject_support_enabled_but_multiprojects_only_spacet() {
		/* prepare */
		when(mockedTemplate.hasFeature(Features.NEW_PROJECT__TYPE_MULTI_PROJECT)).thenReturn(true);
		contextToTest.setMultiProjects(" ");

		/* test */
		assertFalse(contextToTest.validate());
	}
	
	@Test
	public void not_valid_when_multiproject_support_enabled_but_multiprojects_null() {
		/* prepare */
		when(mockedTemplate.hasFeature(Features.NEW_PROJECT__TYPE_MULTI_PROJECT)).thenReturn(true);
		contextToTest.setMultiProjects(null);

		/* test */
		assertFalse(contextToTest.validate());
	}

	
	@Test
	public void valid_when_java_support_enabled_and_java_version_set() {
		/* prepare */
		when(mockedTemplate.hasFeature(Features.NEW_PROJECT__SUPPORTS_JAVA)).thenReturn(true);
		contextToTest.setJavaSourceCompatibility("JavaVersion.VERSION_1_8");

		/* test */
		assertTrue(contextToTest.validate());
	}
	
	@Test
	public void not_valid_when_java_support_enabled_but_java_version_only_spaces() {
		/* prepare */
		when(mockedTemplate.hasFeature(Features.NEW_PROJECT__SUPPORTS_JAVA)).thenReturn(true);
		contextToTest.setJavaSourceCompatibility(" ");

		/* test */
		assertFalse(contextToTest.validate());
	}
	
	@Test
	public void not_valid_when_java_support_enabled_but_java_version_null() {
		/* prepare */
		when(mockedTemplate.hasFeature(Features.NEW_PROJECT__SUPPORTS_JAVA)).thenReturn(true);
		contextToTest.setJavaSourceCompatibility(null);

		/* test */
		assertFalse(contextToTest.validate());
	}

	@Test
	public void not_valid_when_no_template_set() {
		/* prepare */
		contextToTest.setSelectedTemplate(null);
		
		/* test */
		assertFalse(contextToTest.validate());
	}
	
	@Test
	public void to_properties_not_null_even_when_nothing_featured() {
		/* prepare */
		when(mockedTemplate.hasFeature(Features.NEW_PROJECT__SUPPORTS_JAVA)).thenReturn(true);
		contextToTest.setJavaSourceCompatibility(null);

		/* test */
		assertNotNull(contextToTest.toProperties());
	}
	

	@Test
	public void to_properties_contains_java_version_set_in_context() {
		/* prepare */
		contextToTest.setJavaSourceCompatibility("JavaVersion.VERSION_1_8");

		/* test */
		Properties properties = contextToTest.toProperties();
		assertNotNull(properties);
		assertEquals("JavaVersion.VERSION_1_8", properties.get(NewProjectTemplateVariables.VAR__JAVA__VERSION.getVariableName()));
	}
	
	@Test
	public void to_properties_contains_multi_projects_set_in_context() {
		/* prepare */
		contextToTest.setMultiProjects("ui, core, server");

		/* test */
		Properties properties = contextToTest.toProperties();
		assertNotNull(properties);
		assertEquals("'core',\n'server',\n'ui'", properties.get(NewProjectTemplateVariables.VAR__MULTIPROJECTS__INCLUDE_SUBPROJECTS.getVariableName()));
	}
	
	@Test
	public void to_properties_contains_group_name_set_in_context() {
		/* prepare */
		contextToTest.setGroupName("groupName1");

		/* test */
		Properties properties = contextToTest.toProperties();
		assertNotNull(properties);
		assertEquals("groupName1", properties.get(NewProjectTemplateVariables.VAR__NAME_OF_GROUP.getVariableName()));
	}
	
	@Test
	public void get_multiProjectsAsList_for_ui_core_server_contains_separated_by_comma_returns_ui_core_server_in_list() {
		/* prepare */
		contextToTest.setMultiProjects("ui, core, server");

		/* test */
		List<String> list = contextToTest.getMultiProjectsAsList();
		assertTrue(list.contains("core"));
		assertTrue(list.contains("server"));
		assertTrue(list.contains("ui"));
	}
	
	@Test
	public void get_multiProjectsAsIncludeString_for_ui_core_server_contains_separated_by_comma_returns_ui_core_server_in_list() {
		/* prepare */
		contextToTest.setMultiProjects("ui, core, server");

		/* test */
		String include = contextToTest.getMultiProjectsAsIncludeString();
		assertEquals("'core',\n'server',\n'ui'", include);
	}
	
	
	@Test
	public void get_multiProjectsAsList_is_never_null() {
		/* prepare */
		contextToTest.setMultiProjects(null);

		/* test */
		List<String> list = contextToTest.getMultiProjectsAsList();
		assertNotNull(list);
	}
	
	@Test
	public void get_multiProjectsAsIncludeString_is_never_null() {
		/* prepare */
		contextToTest.setMultiProjects(null);

		/* test */
		String include = contextToTest.getMultiProjectsAsIncludeString();
		assertNotNull(include);
	}
	
	@Test
	public void get_groupName_returns_project_when_groupname_is_null() {
		/* prepare */
		contextToTest.setGroupName(null);

		/* test */
		assertEquals(PROJECT_NAME, contextToTest.getGroupName());
	}
	
	@Test
	public void get_groupName_returns_project_when_groupname_is_empty() {
		/* prepare */
		contextToTest.setGroupName("  ");

		/* test */
		assertEquals(PROJECT_NAME, contextToTest.getGroupName());
	}
	
	@Test
	public void get_groupName_returns_groupName_when_groupname_is_set() {
		/* prepare */
		contextToTest.setGroupName("de.jcup.group");

		/* test */
		assertEquals("de.jcup.group", contextToTest.getGroupName());
	}

}
