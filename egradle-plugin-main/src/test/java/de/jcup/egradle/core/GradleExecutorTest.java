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
package de.jcup.egradle.core;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.config.GradleConfiguration;
import de.jcup.egradle.core.domain.CancelStateProvider;
import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.process.EGradleShellType;
import de.jcup.egradle.core.process.ProcessExecutor;

public class GradleExecutorTest {

	// mockito mocks getVariables on mocked object with a empty map!
	private static final Map<String, String> EMPTY_ENV = Collections.emptyMap();
	private GradleExecutor executorToTest;
	private GradleContext mockedContext;
	private GradleCommand mockedCommand1;
	private ProcessExecutor mockedProcessExecutor;
	private GradleRootProject mockedRootProject;
	private GradleCommand mockedCommand2;
	private GradleConfiguration mockedConfiguration;
	private CancelStateProvider mockedCancelStateProvider;

	@Before
	public void before() {
		mockedProcessExecutor = mock(ProcessExecutor.class);

		executorToTest = new GradleExecutor(mockedProcessExecutor);
		mockedRootProject = mock(GradleRootProject.class);
		mockedContext = mock(GradleContext.class);
		mockedConfiguration = mock(GradleConfiguration.class);
		mockedCancelStateProvider=mock(CancelStateProvider.class);
		
		when(mockedContext.getOptions()).thenReturn(new String[] {});
		when(mockedContext.getRootProject()).thenReturn(mockedRootProject);
		when(mockedContext.getConfiguration()).thenReturn(mockedConfiguration);
		when(mockedContext.getEnvironment()).thenReturn(EMPTY_ENV);
		when(mockedConfiguration.getGradleCommandFullPath()).thenReturn("gradleCommand");
		when(mockedConfiguration.getGradleBinDirectory()).thenReturn("");
		when(mockedContext.getCancelStateProvider()).thenReturn(mockedCancelStateProvider);

		mockedCommand1 = mock(GradleCommand.class);
		mockedCommand2 = mock(GradleCommand.class);

	}
	@Test
	public void create_execution_command__test_with_arguments_is_correct_created() {
		List<String> data = new ArrayList<>();
		data.add("--tests");
		data.add("MyTestClass");
		
		when(mockedCommand1.getCommand()).thenReturn("test"); 
		when(mockedCommand1.getCommandArguments()).thenReturn(data);
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });

		String[] result = executorToTest.createExecutionCommand(mockedContext);
		assertNotNull(result);
		
		assertEquals(4, result.length);
		
		int i=0;
		assertEquals("gradleCommand",result[i++]);
		assertEquals("test",result[i++]);
		assertEquals("--tests",result[i++]);
		assertEquals("MyTestClass",result[i++]);
		
	}
	
	
	@Test
	public void executing_returns_result_not_null() {
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });

		ProcessExecutionResult processExecutionResult = executorToTest.execute(mockedContext);
		assertNotNull(processExecutionResult);
	}

	@Test
	public void executing_returns_result_being_ok() {
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });

		ProcessExecutionResult processExecutionResult = executorToTest.execute(mockedContext);
		assertNotNull(processExecutionResult);
		assertTrue(processExecutionResult.isOkay());
	}
	
	@Test
	public void gradleInstallationDirectory_is_appended_before_gradle_command() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });
		File userHome = new  File(System.getProperty("user.home"));
		when(mockedConfiguration.getGradleBinDirectory()).thenReturn(userHome.getAbsolutePath());
		when(mockedConfiguration.getGradleCommandFullPath()).thenReturn("fullpath");
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext, mockedContext,"fullpath", "mockedCommand1");
	}
	
	@Test
	public void when_gradle_context_gets_empty_string_array_for_options_the_no_option_is_added() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });
		when(mockedContext.getOptions()).thenReturn(new String[]{});
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext, mockedContext, "gradleCommand", "mockedCommand1");
	}
	
	@Test
	public void when_gradle_context_gets_filled_string_array_with_2_options_for_options_the_options_are_added() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });
		when(mockedContext.getOptions()).thenReturn(new String[]{"opt1","opt2"});
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext, mockedContext,"gradleCommand","opt1", "opt2", "mockedCommand1");
	}
	
	@Test
	public void when_gradle_context_gets_filled_string_array_for_options_but_contains_only_single_empty_string_the_options_are_NOT_added() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });
		when(mockedContext.getOptions()).thenReturn(new String[]{""});
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext, mockedContext,"gradleCommand", "mockedCommand1");
	}
	
	@Test
	public void when_gradle_context_gets_filled_string_array_for_options_but_contains_only_single_blank_string_the_options_are_NOT_added() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });
		when(mockedContext.getOptions()).thenReturn(new String[]{""});
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext, mockedContext,"gradleCommand", "mockedCommand1");
	}
	
	@Test
	public void when_gradle_context_gets_filled_string_array_for_options_but_contains_a_filled_option_and_a_blank_string_the_blank_option_is_NOT_added() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });
		when(mockedContext.getOptions()).thenReturn(new String[]{" ","opt1"});
		when(mockedConfiguration.getShellType()).thenReturn(EGradleShellType.NONE);
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext, mockedContext,"gradleCommand", "opt1", "mockedCommand1");
	}
	
	@Test
	public void when_shell_is_blank_the_shell_part_is_removed() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });
		when(mockedConfiguration.getShellType()).thenReturn(EGradleShellType.NONE);
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext,mockedContext,"gradleCommand", "mockedCommand1");
	}
	
	@Test
	public void when_shell_is_null_the_shell_part_is_removed() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });
		when(mockedConfiguration.getShellType()).thenReturn(null);
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext,mockedContext,"gradleCommand", "mockedCommand1");
	}
	
	@Test
	public void when_shell_is_set_the_shell_part_is_added() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });
		when(mockedConfiguration.getShellType()).thenReturn(EGradleShellType.CMD);
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext, mockedContext,"cmd.exe", "/C", "gradleCommand", "mockedCommand1");
	}
	

	@Test
	public void executing_gives_command_string_to_process_executor_but_gradle_call_is_before() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext, mockedContext,"gradleCommand", "mockedCommand1");
	}

	@Test
	public void two_gradle_options_are_prefixed_to_command() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });

		when(mockedContext.getOptions()).thenReturn(new String[] { "-test1", "-test2" });
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext, mockedContext,"gradleCommand", "-test1",
				"-test2", "mockedCommand1");
	}

	@Test
	public void gradle_properties_are_prefixed_to_command() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });

		Map<String, String> gradleProperties = new HashMap<>();
		gradleProperties.put("gradle.test.property", "test");
		when(mockedContext.getGradleProperties()).thenReturn(gradleProperties);
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext,  mockedContext,
				"gradleCommand", "-Pgradle.test.property=test", "mockedCommand1");
	}

	@Test
	public void system_properties_are_prefixed_to_command() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });

		Map<String, String> systemProperties = new HashMap<>();
		systemProperties.put("system.test.property", "test");
		when(mockedContext.getSystemProperties()).thenReturn(systemProperties);
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext, 
				mockedContext, "gradleCommand", "-Dsystem.test.property=test", "mockedCommand1");
	}

	@Test
	public void gradle_properties_and_then_system_properties_are_prefixed_to_command() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });

		Map<String, String> systemProperties = new HashMap<>();
		Map<String, String> gradleProperties = new HashMap<>();
		gradleProperties.put("gradle.test.property", "test");
		systemProperties.put("system.test.property", "test");
		when(mockedContext.getSystemProperties()).thenReturn(systemProperties);
		when(mockedContext.getGradleProperties()).thenReturn(gradleProperties);
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext,  mockedContext,
				"gradleCommand", "-Pgradle.test.property=test", "-Dsystem.test.property=test", "mockedCommand1");
	}

	@Test
	public void options_then_gradle_properties_and_then_system_properties_are_prefixed_to_command() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });

		Map<String, String> systemProperties = new HashMap<>();
		Map<String, String> gradleProperties = new HashMap<>();
		gradleProperties.put("gradle.test.property", "test");
		systemProperties.put("system.test.property", "test");
		when(mockedContext.getOptions()).thenReturn(new String[] { "-option" });
		when(mockedContext.getSystemProperties()).thenReturn(systemProperties);
		when(mockedContext.getGradleProperties()).thenReturn(gradleProperties);
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext, mockedContext,"gradleCommand",
				"-option", "-Pgradle.test.property=test", "-Dsystem.test.property=test", "mockedCommand1");
	}

	@Test
	public void executing_gives_commands_string_to_process_executor_but_gradle_call_is_before() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		when(mockedCommand2.getCommand()).thenReturn("mockedCommand2");

		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1, mockedCommand2 });
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext, mockedContext,"gradleCommand", "mockedCommand1", "mockedCommand2");
	}

	@Test
	public void executing_uses_given_working_folder() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("mockedCommand1");
		File mockedWorkingFolder = mock(File.class);
		when(mockedWorkingFolder.isDirectory()).thenReturn(true);
		when(mockedRootProject.getFolder()).thenReturn(mockedWorkingFolder);
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[] { mockedCommand1 });

		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mockedConfiguration, mockedContext, mockedContext,"gradleCommand", "mockedCommand1");
	}
}
