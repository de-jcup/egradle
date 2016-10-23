package de.jcup.egradle.core.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class GradleOutputValidatorTest {

	private GradleOutputValidator validatorToTest;
	private List<String> output;

	@Before
	public void before() {
		validatorToTest = new GradleOutputValidator();
		output=new ArrayList<>();
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
	}

	// output:* Where:
	// output:Script '/home/albert/dev/src/git/gradle-project-template/settings.gradle' line: 7
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
		assertTrue(problem.getErrorMessage().indexOf("unexpected token: import ")!=-1);
		assertEquals(1,problem.getColumn());
	}

	//	output:FAILURE: Build failed with an exception.
	//	output:
	//	output:* Where:
	//	output:Script '/home/albert/dev/src/git/code2doc/libraries.gradle' line: 7
	//	output:
	//	output:* What went wrong:
	//	output:Could not compile script '/home/albert/dev/src/git/code2doc/libraries.gradle'.
	//	output:> startup failed:
	//	output:  script '/home/albert/dev/src/git/code2doc/libraries.gradle': 7: expecting ']', found 'mockito_all' @ line 7, column 3.
	//	output:     		mockito_all:					"org.mockito:mockito-all:1.8.5",
	//	output:       ^
	//	output:  
	//	output:  1 error
	//	output:
	//	output:
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
		assertTrue(problem.getErrorMessage().indexOf("expecting ']', found 'mockito_all' ")!=-1);
		assertEquals(3, problem.getColumn());
	}

}
