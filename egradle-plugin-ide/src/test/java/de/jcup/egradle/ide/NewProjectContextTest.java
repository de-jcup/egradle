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
package de.jcup.egradle.ide;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
    public void before() {
        mockedTemplate = mock(FileStructureTemplate.class);

        contextToTest = new NewProjectContext();
        contextToTest.setSelectedTemplate(mockedTemplate);
        contextToTest.setProjectName(PROJECT_NAME);

    }

    @Test
    public void when_template_supports_gradlew_wrapper_wrapper_is_enabled() {
        /* prepare */
        when(mockedTemplate.hasFeature(Features.NEW_PROJECT__SUPPORTS_GRADLEWRAPPER)).thenReturn(true);

        // prepare again, so changed template loaded...
        contextToTest = new NewProjectContext();
        contextToTest.setSelectedTemplate(mockedTemplate);
        contextToTest.setProjectName(PROJECT_NAME);

        /* test */
        assertTrue(contextToTest.isGradleWrapperSupportedAndEnabled());
    }

    @Test
    public void when_template_supports_NOT_gradlew_wrapper_wrapper_is_NOT_enabled() {
        /* prepare */
        when(mockedTemplate.hasFeature(Features.NEW_PROJECT__SUPPORTS_GRADLEWRAPPER)).thenReturn(false);

        // prepare again, so changed template loaded...
        contextToTest = new NewProjectContext();
        contextToTest.setSelectedTemplate(mockedTemplate);
        contextToTest.setProjectName(PROJECT_NAME);

        /* test */
        assertFalse(contextToTest.isGradleWrapperSupportedAndEnabled());
    }

    @Test
    public void when_template_supports_gradlew_wrapper__but_turned_off_manual_wrapper_is_NOT_enabled() {
        /* prepare */
        when(mockedTemplate.hasFeature(Features.NEW_PROJECT__SUPPORTS_GRADLEWRAPPER)).thenReturn(true);

        // prepare again, so changed template loaded...
        contextToTest = new NewProjectContext();
        contextToTest.setSelectedTemplate(mockedTemplate);
        contextToTest.setProjectName(PROJECT_NAME);

        contextToTest.setGradleWrapperEnabled(false);

        /* test */
        assertFalse(contextToTest.isGradleWrapperSupportedAndEnabled());
    }

    @Test
    public void when_validation_ok_an_old_problem_message_is_replaced_by_null() {
        /* prepare */
        contextToTest.lastValidationProblem = "error";
        assertNotNull(contextToTest.getLastValidationProblem());

        /* execute */
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
        /* test - mocks will always return false for features... */
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
    public void valid_when_java_support_enabled_and_java_version_set__AND_JAVA_HOME_set() {
        /* prepare */
        when(mockedTemplate.hasFeature(Features.NEW_PROJECT__SUPPORTS_JAVA)).thenReturn(true);
        contextToTest.setJavaSourceCompatibility("JavaVersion.VERSION_1_8");
        contextToTest.setJavaHome(System.getProperty("java.home"));

        /* test */
        assertTrue(contextToTest.validate());
    }

    @Test
    public void valid_when_java_support_enabled_and_java_version_set__AND_JAVA_HOME_set_to_non_existing_folder() {
        /* prepare */
        when(mockedTemplate.hasFeature(Features.NEW_PROJECT__SUPPORTS_JAVA)).thenReturn(true);
        contextToTest.setJavaSourceCompatibility("JavaVersion.VERSION_1_8");
        contextToTest.setJavaHome(new File("xyz/xyz/xyz/non-existing").getAbsolutePath());

        /* test */
        assertFalse(contextToTest.validate());
    }

    @Test
    public void invalid_when_java_support_enabled_and_java_version_set__AND_JAVA_HOME_set_to_an_existing_file() throws IOException {
        /* prepare */
        File file = File.createTempFile("xyz", "txt");
        assertTrue(file.exists());
        when(mockedTemplate.hasFeature(Features.NEW_PROJECT__SUPPORTS_JAVA)).thenReturn(true);
        contextToTest.setJavaSourceCompatibility("JavaVersion.VERSION_1_8");
        contextToTest.setJavaHome(file.getAbsolutePath());

        /* test */
        assertFalse(contextToTest.validate());
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
    public void to_properties_contains_gradle_version_set_in_context() {
        /* prepare */
        contextToTest.setGradleVersion("5.0");

        /* test */
        Properties properties = contextToTest.toProperties();
        assertNotNull(properties);
        assertEquals("5.0", properties.get(NewProjectTemplateVariables.VAR__GRADLE__VERSION.getVariableName()));
    }

    @Test
    public void to_properties_contains_gradle_version_set_in_context__check_default_when_nothing() {
        /* prepare with null */
        contextToTest.setGradleVersion(null);

        /* test */
        Properties properties = contextToTest.toProperties();
        assertNotNull(properties);
        assertEquals(NewProjectTemplateVariables.VAR__GRADLE__VERSION.getDefaultValue(), properties.get(NewProjectTemplateVariables.VAR__GRADLE__VERSION.getVariableName()));
    }

    @Test
    public void to_properties_contains_gradle_version_set_in_context__check_default_when_null() {
        /* prepare with null */
        contextToTest.setGradleVersion(null);

        /* test */
        Properties properties = contextToTest.toProperties();
        assertNotNull(properties);
        assertEquals(NewProjectTemplateVariables.VAR__GRADLE__VERSION.getDefaultValue(), properties.get(NewProjectTemplateVariables.VAR__GRADLE__VERSION.getVariableName()));
    }

    @Test
    public void to_properties_contains_gradle_version_set_in_context__check_default_when_empty() {
        /* prepare with null */
        contextToTest.setGradleVersion("");

        /* test */
        Properties properties = contextToTest.toProperties();
        assertNotNull(properties);
        assertEquals(NewProjectTemplateVariables.VAR__GRADLE__VERSION.getDefaultValue(), properties.get(NewProjectTemplateVariables.VAR__GRADLE__VERSION.getVariableName()));
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
    public void get_multiProjectsAsIncludeString_for_predefined_projects_sub1_and_sub2_and_ui_core_server_contains_separated_by_comma_returns_ui_core_server_sub1_sub2_in_list() {
        /* prepare */
        contextToTest.setMultiProjects("ui, core, server");
        when(mockedTemplate.getPredefinedSubprojects()).thenReturn(Arrays.asList("sub1", "sub2"));

        /* test */
        String include = contextToTest.getMultiProjectsAsIncludeString();
        assertEquals("'core',\n'server',\n'ui',\n'sub1',\n'sub2'", include);
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
    public void replaceProjectNameVariable() {
        assertEquals("alpha-web", NewProjectContext.replaceProjectNameVariable("${projectName}-web", "alpha"));
        assertEquals("-web", NewProjectContext.replaceProjectNameVariable("${projectName}-web", ""));
        assertEquals("super-alpha-web", NewProjectContext.replaceProjectNameVariable("super-${projectName}-web", "alpha"));
        assertEquals("super--web", NewProjectContext.replaceProjectNameVariable("super-${projectName}-web", null));
    }

    @Test
    public void get_groupName_returns_groupName_when_groupname_is_set() {
        /* prepare */
        contextToTest.setGroupName("de.jcup.group");

        /* test */
        assertEquals("de.jcup.group", contextToTest.getGroupName());
    }

    @Test
    public void suggest_group() {
        /* test */
        assertEquals("alpha", NewProjectContext.suggestGroupName("alpha"));
        assertEquals("alpha1", NewProjectContext.suggestGroupName("alpha1"));
        assertEquals("alpha.1", NewProjectContext.suggestGroupName("alpha-1"));
        assertEquals("example.java.project", NewProjectContext.suggestGroupName("example-java-project"));
    }

}
