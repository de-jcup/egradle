package de.jcup.egradle.core.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.util.List;

public class GradleCommandTest {
	
	private GradleSubproject mockedSubProject1;

	@Before
	public void before(){
		mockedSubProject1=mock(GradleSubproject.class);
		when(mockedSubProject1.getName()).thenReturn("subproject1");
	}
	@Test
	public void clean_test_results_in_two_different_commands() {
		GradleCommand[] commands = GradleCommand.build("clean test");
		assertNotNull(commands);
		assertEquals(2,commands.length);
	}
	@Test
	public void clean_test_followed_with_test_arguments_results_in_two_commands_only() {
		GradleCommand[] commands = GradleCommand.build("clean test --tests de.jcup.MyTest");
		assertNotNull(commands);
		assertEquals(2,commands.length);
	}
	
	@Test
	public void clean_test_followed_with_test_arguments__command_for_clean_has_no_arguments() {
		GradleCommand[] commands = GradleCommand.build("clean test --tests de.jcup.MyTest");
		assertNotNull(commands);
		assertEquals(2,commands.length);
		assertEquals("clean", commands[0].getCommand());
	}
	
	@Test
	public void clean_test_followed_with_test_arguments__command_for_test_has_arguments() {
		GradleCommand[] commands = GradleCommand.build("clean test --tests de.jcup.MyTest");
		assertNotNull(commands);
		assertEquals(2,commands.length);
		assertEquals("test", commands[1].getCommand());
		List<String> arguments = commands[1].getCommandArguments();
		assertEquals(2,arguments.size());
		assertEquals("--tests",arguments.get(0));
		assertEquals("de.jcup.MyTest",arguments.get(1));
	}
	
	/* ---------------------- */
	/* ----- Subprojects ---- */
	/* ---------------------- */
	@Test
	public void subproject1_clean_test_results_in_two_different_commands() {
		GradleCommand[] commands = GradleCommand.build(mockedSubProject1, "clean test");
		assertNotNull(commands);
		assertEquals(2,commands.length);
	}
	@Test
	public void subproject1_clean_test_followed_with_test_arguments_results_in_two_commands_only() {
		GradleCommand[] commands = GradleCommand.build(mockedSubProject1,"clean test --tests de.jcup.MyTest");
		assertNotNull(commands);
		assertEquals(2,commands.length);
	}
	
	@Test
	public void subproject1_clean_test_followed_with_test_arguments__command_for_clean_has_no_arguments() {
		GradleCommand[] commands = GradleCommand.build(mockedSubProject1,"clean test --tests de.jcup.MyTest");
		assertNotNull(commands);
		assertEquals(2,commands.length);
		assertEquals(":subproject1:clean", commands[0].getCommand());
	}
	
	@Test
	public void subproject1_clean_test_followed_with_test_arguments__command_for_test_has_arguments() {
		GradleCommand[] commands = GradleCommand.build(mockedSubProject1,"clean test --tests de.jcup.MyTest");
		assertNotNull(commands);
		assertEquals(2,commands.length);
		assertEquals(":subproject1:test", commands[1].getCommand());
		List<String> arguments = commands[1].getCommandArguments();
		assertEquals(2,arguments.size());
		assertEquals("--tests",arguments.get(0));
		assertEquals("de.jcup.MyTest",arguments.get(1));
	}

}
