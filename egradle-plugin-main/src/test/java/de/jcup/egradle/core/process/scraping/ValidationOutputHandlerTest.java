package de.jcup.egradle.core.process.scraping;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.process.scraping.ValidationOutputHandler.ValidationResult;

public class ValidationOutputHandlerTest {

	private ValidationOutputHandler toTest;

	@Before
	public void before(){
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
	public void a_problem_on_evaluation_script__home_albert_build_gradle__line_612_is_is_inside_validation_result() {
		/* execute */
		toTest.output("* Where:");
		toTest.output("* Script 'home/albert/build.gradle' line: 612");
		toTest.output("* What went wrong:");
		toTest.output("A problem occurred evaluating script.");
		toTest.output("> Could not get unknown property 'x' for root project 'xyz' of type org.gradle.api.Project" );
		
		/* test */
		ValidationResult problem = toTest.getResult();
		assertNotNull(problem);
		assertTrue(problem.hasScriptEvaluationProblem());
		assertEquals(612,problem.getLine());
		assertEquals("home/albert/build.gradle",problem.getScriptPath());
		assertEquals("Could not get unknown property 'x' for root project 'xyz' of type org.gradle.api.Project",problem.getErrorMessage());
	}

}
