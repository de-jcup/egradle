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
 package de.jcup.egradle.core.process;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jcup.egradle.core.api.GradleConfigurationValidator;
import de.jcup.egradle.core.api.ValidationException;
import de.jcup.egradle.core.config.GradleConfiguration;

public class GradleConfigurationValidatorTest {

	private static final String GRADLE_COMMAND = "gradle";
	private ProcessExecutor mockedProcessExecutor;
	private GradleConfigurationValidator validatorToTest;
	private GradleConfiguration mockedGradleConfiguration;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void before() {
		mockedGradleConfiguration = mock(GradleConfiguration.class);
		mockedProcessExecutor = mock(ProcessExecutor.class);
		validatorToTest = new GradleConfigurationValidator(mockedProcessExecutor);

		when(mockedGradleConfiguration.getGradleBinDirectory()).thenReturn("");
		when(mockedGradleConfiguration.getGradleCommandFullPath()).thenReturn(GRADLE_COMMAND);
		when(mockedGradleConfiguration.getShellType()).thenReturn(EGradleShellType.NONE);

	}

	@Test
	public void when_gradle_installation_dir_is_empty_string__and_process_is_executable_no_validation_exception_is_thrown()
			throws Exception {
		/* prepare */
		when(mockedGradleConfiguration.getGradleBinDirectory()).thenReturn("");
		/* test + execute */
		validatorToTest.validate(mockedGradleConfiguration);

	}

	@Test
	public void when_gradle_installation_dir_is_null__and_process_is_executable_no_validation_exception_is_thrown()
			throws Exception {
		/* prepare */
		when(mockedGradleConfiguration.getGradleBinDirectory()).thenReturn(null);
		/* test + execute */
		validatorToTest.validate(mockedGradleConfiguration);
	}

	@Test
	public void when_gradle_installation_dir_is_set_but_directory_does_not_exists_a_validation_exception_is_thrown()
			throws Exception {
		/* prepare */
		File notExistingfile = new File("iam_not_existing__really.sh");
		if (notExistingfile.exists()) {
			fail("Test failes - but because of wrong conditiions - expected file to not exist:" + notExistingfile);
		}
		when(mockedGradleConfiguration.getGradleBinDirectory()).thenReturn(notExistingfile.getAbsolutePath());
		thrown.expect(new IsEqual<>(
				new ValidationException(GradleConfigurationValidator.GRADLE_INSTALLATION_DIR_NOT_EXISTING)));

		/* test + execute */
		validatorToTest.validate(mockedGradleConfiguration);
	}

	@Test
	public void when_gradle_installation_dir_is_set_but_its_file_not_a_directory_a_validation_exception_is_thrown()
			throws Exception {
		/* prepare */
		File file = File.createTempFile("test", ".txt"); // throws io exception
															// when temp file
															// cannot be created

		when(mockedGradleConfiguration.getGradleBinDirectory()).thenReturn(file.getAbsolutePath());
		thrown.expect(new IsEqual<>(
				new ValidationException(GradleConfigurationValidator.GRADLE_INSTALLATION_DIR_IS_NOT_A_DIRECTORY)));

		/* test + execute */
		validatorToTest.validate(mockedGradleConfiguration);
	}

	@Test
	public void when_gradle_installation_dir_is_set_but_it_contains_not_a_file_called_gradle_a_validation_exception_is_thrown()
			throws Exception {
		/* prepare */
		File userHome = new File(System.getProperty("user.home"));
		File gradleFake = new File(userHome, "gradle");
		assertFalse("Test execution not possible - in user home there is a gradle file existing!?!",
				gradleFake.exists());

		when(mockedGradleConfiguration.getGradleBinDirectory())
				.thenReturn(gradleFake.getParentFile().getAbsolutePath());
		thrown.expect(new IsEqual<>(
				new ValidationException(GradleConfigurationValidator.GRADLE_INSTALLATION_DIR_CONTAINS_NO_GRADLE)));

		/* test + execute */
		validatorToTest.validate(mockedGradleConfiguration);
	}

	@Test
	public void when_gradle_installation_dir_is_set_andt_it_contains_a_file_called_defined_gradlecommand_no_validation_exception_is_thrown()
			throws Exception {
		/* prepare */
		File userHome = new File(System.getProperty("user.home"));
		File subFolder = new File(userHome, ".egradle-test-fakegradle-exists");
		subFolder.deleteOnExit();
		assertTrue("Test execution corrupt?!?!", subFolder.mkdirs());
		File gradleFake = new File(subFolder, "gradlewdeluxe.bat");
		assertTrue("Test execution corrupt?!?!", gradleFake.createNewFile());
		gradleFake.deleteOnExit();

		when(mockedGradleConfiguration.getGradleCommandFullPath()).thenReturn("gradlewdeluxe.bat");
		when(mockedGradleConfiguration.getGradleBinDirectory()).thenReturn(subFolder.getAbsolutePath());

		/* test + execute */
		validatorToTest.validate(mockedGradleConfiguration);
	}

	@Test
	public void when_shell_is_NONE_no_validation_exception_is_thrown() throws Exception {
		/* prepare */
		when(mockedGradleConfiguration.getShellType()).thenReturn(EGradleShellType.NONE);

		/* execute +test */
		validatorToTest.validate(mockedGradleConfiguration);

	}

	@Test
	public void when_shell_command_is_null_no_validation_exception_is_thrown() throws Exception {
		/* prepare */
		when(mockedGradleConfiguration.getShellType()).thenReturn(null);

		/* execute +test */
		validatorToTest.validate(mockedGradleConfiguration);

	}

	@Test
	public void when_shell_command_is_set_but_cannot_be_executed_standalone_a_validation_exception_is_thrown()
			throws Exception {
		/* prepare */
		thrown.expect(
				new IsEqual<>(new ValidationException(GradleConfigurationValidator.SHELL_NOT_EXECUTABLE_STANDALONE)));
		when(mockedGradleConfiguration.getShellType()).thenReturn(EGradleShellType.BASH);
		when(mockedProcessExecutor.execute(any(), any(), eq("bash"),eq("--version")))
				.thenThrow(new IOException("bash call standalone does always fail inside this test"));

		/* execute +test */
		validatorToTest.validate(mockedGradleConfiguration);
		
		/* only for debugging - if no exception occurred...*/
		verify(mockedProcessExecutor).execute(any(), any(), eq("bash"),eq("--version"));
	}

	@Test
	public void when_gradle_command_is_null_a_validation_exception_is_thrown() throws Exception {
		thrown.expect(new IsEqual<>(new ValidationException(GradleConfigurationValidator.GRADLE_COMMAND_MISSING)));
		when(mockedGradleConfiguration.getGradleCommandFullPath()).thenReturn(null);

		/* execute +test */
		validatorToTest.validate(mockedGradleConfiguration);
	}
	
	@Test
	public void when_gradle_command_is_empty_a_validation_exception_is_thrown() throws Exception {
		thrown.expect(new IsEqual<>(new ValidationException(GradleConfigurationValidator.GRADLE_COMMAND_MISSING)));
		when(mockedGradleConfiguration.getGradleCommandFullPath()).thenReturn("");

		/* execute +test */
		validatorToTest.validate(mockedGradleConfiguration);
	}
	
	@Test
	public void when_gradle_command_contains_only_whitespace_a_validation_exception_is_thrown() throws Exception {
		thrown.expect(new IsEqual<>(new ValidationException(GradleConfigurationValidator.GRADLE_COMMAND_MISSING)));
		when(mockedGradleConfiguration.getGradleCommandFullPath()).thenReturn("   ");

		/* execute +test */
		validatorToTest.validate(mockedGradleConfiguration);
	}

	@Test
	public void when_a_gradle_call_with_version_throws_an_ioexception_a_validation_exception_is_thrown()
			throws Exception {
		/* prepare */
		thrown.expect(
				new IsEqual<>(new ValidationException(GradleConfigurationValidator.GRADLE_VERSON_NOT_CALLABLE)));
		when(mockedGradleConfiguration.getShellType()).thenReturn(EGradleShellType.BASH);
		when(mockedGradleConfiguration.getGradleCommandFullPath()).thenReturn("gradlew");

		when(mockedProcessExecutor.execute(any(), any(), eq("bash"), eq("gradlew"), eq("--version")))
				.thenThrow(new IOException("the simple --version call must fail inside this test"));

		/* execute +test */
		validatorToTest.validate(mockedGradleConfiguration);
		
		/* normally dead code, but when no validation exception occured this is googd for debuging:*/
		verify(mockedProcessExecutor).execute(any(), any(), eq("bash"), eq("gradlew"), eq("--version"));
	}

	@Test
	public void when_a_gradle_call_with_version_throws_NO_ioexception_NO_validation_exception_is_thrown()
			throws Exception {
		/* prepare */
		when(mockedGradleConfiguration.getShellType()).thenReturn(EGradleShellType.BASH);
		when(mockedGradleConfiguration.getGradleCommandFullPath()).thenReturn("gradlew");

		when(mockedProcessExecutor.execute(any(), any(), eq("bash"), eq("gradlew"), eq("--version"))).thenReturn(0);

		/* execute +test */
		validatorToTest.validate(mockedGradleConfiguration);
	}

}
