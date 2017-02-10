package de.jcup.egradle.test.integregation;

import static de.jcup.egradle.core.TestUtil.*;
import static de.jcup.egradle.test.integregation.ProposalsAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Rule;
import org.junit.Test;

import de.jcup.egradle.codeassist.GradleDSLProposalFactory;
import de.jcup.egradle.codeassist.Proposal;
import de.jcup.egradle.codeassist.ProposalFactoryContentProvider;
public class ProposalIntegrationTest {


	@Rule
	public IntegrationTestComponents components = IntegrationTestComponents.initialize();


	@Test
	public void buildfile__with_dependencies_in_root__when_cursor_is_after_dependencies_bracket() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test1-dependencies-block-inside-root.gradle");
		int offset = calculateIndexEndOf(text,"dependencies {");

		/* execute */
		Set<Proposal> proposals = createProposals(text, offset);
		
		/* test */
		/* @formatter:off*/
		assertThat(proposals).
			containsAtLeastOneProposal().
				whichHasReasonType("org.gradle.api.artifacts.dsl.DependencyHandler").
		and().
			containsProposalWithLabel("add(String configurationName, Object dependencyNotation, Closure configureClosure)").
				whichHasReasonType("org.gradle.api.artifacts.dsl.DependencyHandler").
				whichHasDescription().
		and();
		/* @formatter:on*/
	}
	
	@Test
	public void buildfile__with_task_in_root__when_cursor_is_after_task_bracket() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test8-task-inside-root.gradle");
		int offset = calculateIndexEndOf(text,"task myTask {");

		/* execute */
		Set<Proposal> proposals = createProposals(text, offset);
		
		/* test */
		/* @formatter:off*/
		assertThat(proposals).
			containsAtLeastOneProposal().
				whichHasReasonType("org.gradle.api.Task").
		and().
			containsProposalWithLabel("doFirst(Closure action)").
				whichHasReasonType("org.gradle.api.Task").
				whichHasDescription().
		and();
		/* @formatter:on*/
	}
	
	@Test
	public void buildfile__with_task_extendending_jar_in_root__when_cursor_is_after_task_bracket() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test9-task-extending-jar-inside-root.gradle");
		int offset = calculateIndexEndOf(text,"task myTask(type:jar) {");

		/* execute */
		Set<Proposal> proposals = createProposals(text, offset);
		
		/* test */
		/* @formatter:off*/
		assertThat(proposals).
			containsAtLeastOneProposal().
				whichHasReasonType("org.gradle.api.Task").
		and().
			containsProposalWithLabel("doFirst(Closure action)").
				whichHasReasonType("org.gradle.api.Task").
				whichHasDescription().
		and();
		/* @formatter:on*/
	}
	
	@Test
	public void buildfile__with_jar_configuration_in_root__when_cursor_is_after_dependencies_bracket() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test-10-jar-task-configuration-in-root.gradle");
		int offset = calculateIndexEndOf(text,"jar {");

		/* execute */
		Set<Proposal> proposals = createProposals(text, offset);
		
		/* test */
		/* @formatter:off*/
		assertThat(proposals).
			containsAtLeastOneProposal().
				whichHasReasonType("org.gradle.api.tasks.bundling.Jar").
		and().
			containsProposalWithLabel("eachFile(Closure closure)").
				whichHasReasonType("org.gradle.api.tasks.bundling.Jar").
				whichHasDescription().
		and();
		/* @formatter:on*/
	}
	
	@Test
	public void buildfile__empty_offset_is_0() {
		/* prepare */
		String text = "";
		int offset = 0;
	
		/* execute */
		Set<Proposal> proposals = createProposals(text, offset);
		/* test */
		/* @formatter:off*/
		assertThat(proposals).
			containsAtLeastOneProposal().
		and().
			containsProposalWithLabel("dependencies(Closure configureClosure)").
				whichHasDescription().
				hasTemplate("dependencies {\n    $cursor\n}").
		and();
		/* @formatter:on*/
	}

	

	private Set<Proposal> createProposals(String text, int offset) {
		ProposalFactoryContentProvider contentProvider = components.buildContentProvider(text,offset);
		GradleDSLProposalFactory gradleDSLProposalFactory = components.getGradleDSLProposalFactory();
		return gradleDSLProposalFactory.createProposals(offset,contentProvider);
	}
	
	private String loadTextFromIntegrationTestFile(String testFileName) {
		String text = components.loadTestFile("integration/" + testFileName);
		assertNotNull("testcase corrupt", text);
		return text;
	}
	
}