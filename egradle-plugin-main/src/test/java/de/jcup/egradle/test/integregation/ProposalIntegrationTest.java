package de.jcup.egradle.test.integregation;

import static org.junit.Assert.*;
import static de.jcup.egradle.test.integregation.ProposalsAssert.*;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.codeassist.Proposal;

public class ProposalIntegrationTest {

	private IntegrationTestComponents components;

	@Before
	public void before() {
		components = IntegrationTestComponents.INSTANCE;
	}

	@Test
	public void buildfile__with_dependencies_in_root__when_cursor_is_after_dependencies_bracket() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test1-dependencies-block-inside-root.gradle");
		int offset = text.indexOf("dependencies {");

		/* execute */
		Set<Proposal> proposals = components.getGradleDSLProposalFactory().createProposals(offset,components.buildContentProvider(text,offset));
		/* test */
		/* @formatter:off*/
		assertThat(proposals).
			hasAtLeastOneProposal().
			assertProposalHavingLabel("add(String configurationName, Object dependencyNotation, Closure configureClosure)").
				hasDescription().
				assertDone();
		/* @formatter:on*/
	}
	
	@Test
	public void buildfile__empty_offset_is_0() {
		/* prepare */
		String text = "";
		int offset = 0;

		/* execute */
		Set<Proposal> proposals = components.getGradleDSLProposalFactory().createProposals(offset,components.buildContentProvider(text,offset));
		/* test */
		/* @formatter:off*/
		assertThat(proposals).
			hasAtLeastOneProposal().
			assertProposalHavingLabel("dependencies(Closure configureClosure)").
				hasDescription().
				hasTemplate("dependencies {\n    $cursor\n}").
				assertDone();
		/* @formatter:on*/
	}
	
	private String loadTextFromIntegrationTestFile(String testFileName) {
		String text = components.loadTestFile("integration/" + testFileName);
		assertNotNull("testcase corrupt", text);
		return text;
	}
	
}