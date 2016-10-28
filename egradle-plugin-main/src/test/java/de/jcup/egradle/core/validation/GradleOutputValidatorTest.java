package de.jcup.egradle.core.validation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.Constants;

public class GradleOutputValidatorTest {

	private GradleOutputValidator validatorToTest;
	private List<String> output;

	@Before
	public void before() {
		validatorToTest = new GradleOutputValidator();
		output = new ArrayList<>();
	}

	@Test
	public void convert_problem_is_recognized() {
		output.add("* Where:");
		output.add(
				"Build file '/home/albert/dev/src/git/egradle-testcase-projects/singleproject-01-java-with-gradlewrapper/build.gradle' line: 16");
		output.add("");
		output.add("* What went wrong:");
		output.add("A problem occurred evaluating root project 'singleproject-01-java-with-gradlewrapper'.");
		output.add("> Cannot convert a null value to an object of type Dependency.");
		output.add("The following types/formats are supported:");
		output.add("     - Instances of Dependency.");
		output.add("     - String or CharSequence values, for example 'org.gradle:gradle-core:1.0'.");
		output.add("     - Maps, for example [group: 'org.gradle', name: 'gradle-core', version: '1.0'].");
		output.add("     - FileCollections, for example files('some.jar', 'someOther.jar').");
		output.add("     - Projects, for example project(':some:project:path').");
		output.add("     - ClassPathNotation, for example gradleApi().");
		output.add("");
		output.add(
				"Comprehensive documentation on dependency notations is available in DSL reference for DependencyHandler type.");

		output.add("* Try:");
		output.add(
				"Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output.");
		output.add("");
		output.add("BUILD FAILED");
		output.add("");
		output.add("Total time: 2.462 secs");

		ValidationResult problem = validatorToTest.validate(output);
		assertNotNull(problem);
		assertTrue(problem.hasProblem());
		assertEquals(16, problem.getLine());
		assertEquals(
				"/home/albert/dev/src/git/egradle-testcase-projects/singleproject-01-java-with-gradlewrapper/build.gradle",
				problem.getScriptPath());

		assertTestOutputDoesNotExceedLimits();
	}

	@Test
	public void no_problem_on_evaluation_returns_in_validation_result_without_script_evaluation_problem() {
		/* execute */
		output.add("okay...");
		output.add("no problem!");

		/* test */
		ValidationResult problem = validatorToTest.validate(output);
		assertNotNull(problem);
		assertFalse(problem.hasScriptEvaluationProblem());
		assertTestOutputDoesNotExceedLimits();
	}

	@Test
	public void a_problem_on_evaluation_build__buildfile__line_1_is_is_inside_validation_result() {
		/* execute */
		output.add("* Where:");
		output.add("Build file '/home/albert/dev/src/git/gradle-project-template/build.gradle' line: 1");
		output.add("");
		output.add("* What went wrong:");
		output.add("A problem occurred evaluating root project 'gradle-project-template'.");
		output.add("> Cannot get property 'x' on null object");
		output.add("");
		output.add("* Try:");
		output.add(
				"Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output.");
		output.add("");
		output.add("BUILD FAILED");
		/* test */
		ValidationResult problem = validatorToTest.validate(output);
		assertNotNull(problem);
		assertTrue(problem.hasScriptEvaluationProblem());
		assertEquals(1, problem.getLine());
		assertEquals("/home/albert/dev/src/git/gradle-project-template/build.gradle", problem.getScriptPath());
		assertEquals("Cannot get property 'x' on null object", problem.getErrorMessage());
		assertTestOutputDoesNotExceedLimits();
	}

	@Test
	public void a_problem_on_evaluation_script__home_albert_build_gradle__line_612_is_is_inside_validation_result() {
		/* execute */
		output.add("* Where:");
		output.add("* Script 'home/albert/build.gradle' line: 612");
		output.add("* What went wrong:");
		output.add("A problem occurred evaluating script.");
		output.add("> Could not get unknown property 'x' for root project 'xyz' of type org.gradle.api.Project");

		/* test */
		ValidationResult problem = validatorToTest.validate(output);
		assertNotNull(problem);
		assertTrue(problem.hasScriptEvaluationProblem());
		assertEquals(612, problem.getLine());
		assertEquals("home/albert/build.gradle", problem.getScriptPath());
		assertEquals("Could not get unknown property 'x' for root project 'xyz' of type org.gradle.api.Project",
				problem.getErrorMessage());
		assertTestOutputDoesNotExceedLimits();
	}

	// output:* Where:
	// output:Script
	// '/home/albert/dev/src/git/gradle-project-template/settings.gradle' line:
	// 7
	// output:* What went wrong:
	// output:Could not compile settings file
	// '/home/albert/dev/src/git/gradle-project-template/settings.gradle'.
	// output:> startup failed:
	// output: settings file
	// '/home/albert/dev/src/git/gradle-project-template/settings.gradle': 7:
	// unexpected token: import @ line 7, column 1.
	// output: import org.gradle.*
	// output: ^
	// output:
	// output: 1 error
	@Test
	public void settings_compile_error() {
		output.add("* Where:");
		output.add("Script '/home/albert/dev/src/git/gradle-project-template/settings.gradle' line: 7");
		output.add("* What went wrong:");
		output.add("Could not compile settings file\n"
				+ "'/home/albert/dev/src/git/gradle-project-template/settings.gradle'.");
		output.add("> startup failed:");
		output.add(" settings file\n" + "'/home/albert/dev/src/git/gradle-project-template/settings.gradle': 7:\n"
				+ "unexpected token: import @ line 7, column 1.");
		output.add(" import org.gradle.*");
		output.add(" ^");
		output.add("");
		output.add(" 1 error");

		/* test */
		ValidationResult problem = validatorToTest.validate(output);
		assertNotNull(problem);
		assertTrue(problem.hasCompileProblem());
		assertEquals(7, problem.getLine());
		assertEquals("/home/albert/dev/src/git/gradle-project-template/settings.gradle", problem.getScriptPath());
		assertTrue(problem.getErrorMessage().indexOf("unexpected token: import ") != -1);
		assertEquals(1, problem.getColumn());
		assertTestOutputDoesNotExceedLimits();
	}

	// output:FAILURE: Build failed with an exception.
	// output:
	// output:* Where:
	// output:Script '/home/albert/dev/src/git/code2doc/libraries.gradle' line:
	// 7
	// output:
	// output:* What went wrong:
	// output:Could not compile script
	// '/home/albert/dev/src/git/code2doc/libraries.gradle'.
	// output:> startup failed:
	// output: script '/home/albert/dev/src/git/code2doc/libraries.gradle': 7:
	// expecting ']', found 'mockito_all' @ line 7, column 3.
	// output: mockito_all: "org.mockito:mockito-all:1.8.5",
	// output: ^
	// output:
	// output: 1 error
	// output:
	// output:
	@Test
	public void compile_script_error() {
		output.add("* Where:");
		output.add("Script '/home/albert/dev/src/git/code2doc/libraries.gradle' line: 7");
		output.add("");
		output.add("* What went wrong:");
		output.add("Could not compile script '/home/albert/dev/src/git/code2doc/libraries.gradle'.");
		output.add("> startup failed:");
		output.add(
				"  script '/home/albert/dev/src/git/code2doc/libraries.gradle': 7: expecting ']', found 'mockito_all' @ line 7, column 3.");
		output.add("     		mockito_all:					\"org.mockito:mockito-all:1.8.5\",");
		output.add("       ^");

		/* test */
		ValidationResult problem = validatorToTest.validate(output);
		assertNotNull(problem);
		assertTrue(problem.hasCompileProblem());
		assertEquals(7, problem.getLine());
		assertEquals("/home/albert/dev/src/git/code2doc/libraries.gradle", problem.getScriptPath());
		assertTrue(problem.getErrorMessage().indexOf("expecting ']', found 'mockito_all' ") != -1);
		assertEquals(3, problem.getColumn());
		assertTestOutputDoesNotExceedLimits();
	}

	/**
	 * This is not optimal and tests another component part, but it tests that each output created by this tests, does not exceed the current shrink limit.
	 * Otherwise the tests will not fail but it will not work in EGradle, because the console line handler shrinks the output to the limit given in 
	 * {@link Constants#VALIDATION_OUTPUT_SHRINK_LIMIT}. So everything what is tested here, has to work in EGradle too
	 */
	protected void assertTestOutputDoesNotExceedLimits() {
		int size = output.size();
		assertTrue(
				"validation has no problems, but not output contains too much lines - so limit constant must be made bigger, at least to:"
						+ size,
				size <= Constants.VALIDATION_OUTPUT_SHRINK_LIMIT);
	}

}
