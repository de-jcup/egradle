package de.jcup.egradle.core.process.scraping;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.jcup.egradle.core.process.scraping.ValidationOutputHandler.ValidationResult;

public class ValidationOutputHandlerTest {

	private ValidationOutputHandler toTest;

	@Before
	public void before() {
		toTest = new ValidationOutputHandler();
	}

	@Test
	public void no_problem_on_evaluation_returns_in_validation_result_without_script_evaluation_problem() {
		/* execute */
		toTest.output("okay...");
		toTest.output("no problem!");

		/* test */
		ValidationResult problem = toTest.getResult();
		assertNotNull(problem);
		assertFalse(problem.hasScriptEvaluationProblem());
	}

	@Test
	public void a_problem_on_evaluation_build__buildfile__line_1_is_is_inside_validation_result() {
		/* execute */
		toTest.output("* Where:");
		toTest.output("Build file '/home/albert/dev/src/git/gradle-project-template/build.gradle' line: 1");
		toTest.output("");
		toTest.output("* What went wrong:");
		toTest.output("A problem occurred evaluating root project 'gradle-project-template'.");
		toTest.output("> Cannot get property 'x' on null object");
		toTest.output("");
		toTest.output("* Try:");
		toTest.output(
				"Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output.");
		toTest.output("");
		toTest.output("BUILD FAILED");
		/* test */
		ValidationResult problem = toTest.getResult();
		assertNotNull(problem);
		assertTrue(problem.hasScriptEvaluationProblem());
		assertEquals(1, problem.getLine());
		assertEquals("/home/albert/dev/src/git/gradle-project-template/build.gradle", problem.getScriptPath());
		assertEquals("Cannot get property 'x' on null object",
				problem.getErrorMessage());
	}

	@Test
	public void a_problem_on_evaluation_script__home_albert_build_gradle__line_612_is_is_inside_validation_result() {
		/* execute */
		toTest.output("* Where:");
		toTest.output("* Script 'home/albert/build.gradle' line: 612");
		toTest.output("* What went wrong:");
		toTest.output("A problem occurred evaluating script.");
		toTest.output("> Could not get unknown property 'x' for root project 'xyz' of type org.gradle.api.Project");

		/* test */
		ValidationResult problem = toTest.getResult();
		assertNotNull(problem);
		assertTrue(problem.hasScriptEvaluationProblem());
		assertEquals(612, problem.getLine());
		assertEquals("home/albert/build.gradle", problem.getScriptPath());
		assertEquals("Could not get unknown property 'x' for root project 'xyz' of type org.gradle.api.Project",
				problem.getErrorMessage());
	}

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
	@Ignore
	public void settings_compile_error() {
		fail("not implemented!");
	}
}
