package de.jcup.egradle.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

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
	private GradleContext mockedEnvironment;
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
		mockedEnvironment = mock(GradleContext.class);
		mockedConfiguration= mock(GradleConfiguration.class);
		
		when(mockedEnvironment.getRootProject()).thenReturn(mockedRootProject);
		when(mockedEnvironment.getConfiguration()).thenReturn(mockedConfiguration);
		when(mockedConfiguration.isUsingGradleWrapper()).thenReturn(true);
		when(mockedConfiguration.getShellForGradleWrapper()).thenReturn("usedShell");
		
		mockedCommand1 = mock(GradleCommand.class);
		mockedCommand2 = mock(GradleCommand.class);

	}

	@Test
	public void executing_returns_result_not_null() {

		Result result = executorToTest.execute(mockedEnvironment, mockedCommand1);
		assertNotNull(result);
	}

	@Test
	public void executing_returns_result_being_ok() {

		Result result = executorToTest.execute(mockedEnvironment, mockedCommand1);
		assertNotNull(result);
		assertTrue(result.isOkay());
	}

	@Test
	public void executing_gives_command_string_to_process_executor_but_gradle_call_is_before() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("eclipse");
		/* execute */
		executorToTest.execute(mockedEnvironment, mockedCommand1);
		/* test */
		verify(mockedProcessExecutor).execute(null, EMPTY_ENV, "usedShell", "gradlew","eclipse");
	}
	
	@Test
	public void executing_gives_commands_string_to_process_executor_but_gradle_call_is_before() throws Exception {
		/* prepare */
		when(mockedCommand1.getCommand()).thenReturn("eclipse");
		when(mockedCommand2.getCommand()).thenReturn("cleanEclipse");
		/* execute */
		executorToTest.execute(mockedEnvironment, mockedCommand1,mockedCommand2);
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
		
		/* execute */
		executorToTest.execute(mockedEnvironment, mockedCommand1);
		/* test */
		verify(mockedProcessExecutor).execute(mcokedWorkingFolder, EMPTY_ENV, "usedShell","gradlew","eclipse");
	}
}
