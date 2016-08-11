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
 package de.jcup.egradle.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.GradleExecutor.Result;
import de.jcup.egradle.core.config.GradleConfiguration;
import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.domain.GradleRootProject;
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

	@Before
	public void before() {
		mockedProcessExecutor = mock(ProcessExecutor.class);

		executorToTest = new GradleExecutor(mockedProcessExecutor);
		mockedRootProject = mock(GradleRootProject.class);
		mockedContext = mock(GradleContext.class);
		mockedConfiguration= mock(GradleConfiguration.class);
		
		when(mockedContext.getRootProject()).thenReturn(mockedRootProject);
		when(mockedContext.getConfiguration()).thenReturn(mockedConfiguration);
		when(mockedConfiguration.isUsingGradleWrapper()).thenReturn(true);
		when(mockedConfiguration.getShellForGradleWrapper()).thenReturn("usedShell");
		
		mockedCommand1 = mock(GradleCommand.class);
		mockedCommand2 = mock(GradleCommand.class);

	}

	@Test
	public void executing_returns_result_not_null() {
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[]{mockedCommand1});

		Result result = executorToTest.execute(mockedContext);
		assertNotNull(result);
	}

	@Test
	public void executing_returns_result_being_ok() {
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[]{mockedCommand1});
		
		Result result = executorToTest.execute(mockedContext);
		assertNotNull(result);
		assertTrue(result.isOkay());
	}

	@Test
	public void executing_gives_command_string_to_process_executor_but_gradle_call_is_before() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("eclipse");
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[]{mockedCommand1});
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(null, EMPTY_ENV, "usedShell", "gradlew","eclipse");
	}
	
	@Test
	public void executing_gives_commands_string_to_process_executor_but_gradle_call_is_before() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("eclipse");
		when(mockedCommand2.getCommand()).thenReturn("cleanEclipse");
		
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[]{mockedCommand1,mockedCommand2});
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(null, EMPTY_ENV, "usedShell","gradlew","eclipse","cleanEclipse");
	}
	
	@Test
	public void executing_uses_given_working_folder() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("eclipse");
		File mcokedWorkingFolder = mock(File.class);
		when(mcokedWorkingFolder.isDirectory()).thenReturn(true);
		when(mockedRootProject.getFolder()).thenReturn(mcokedWorkingFolder);
		when(mockedContext.getCommands()).thenReturn(new GradleCommand[]{mockedCommand1});
		
		/* execute */
		executorToTest.execute(mockedContext);
		/* test */
		verify(mockedProcessExecutor).execute(mcokedWorkingFolder, EMPTY_ENV, "usedShell","gradlew","eclipse");
	}
}