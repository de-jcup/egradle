package de.jcup.egradle.codecompletion;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.jcup.egradle.codecompletion.Proposal;
import de.jcup.egradle.codecompletion.ProposalFactoryContentProvider;
import de.jcup.egradle.codecompletion.VariableNameProposalFactory;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.model.groovyantlr.GradleModelBuilder;

public class VariableNameProposalFactoryTest {

	private VariableNameProposalFactory factoryToTest;
	private ProposalFactoryContentProvider mockedContentProvider;

	@Before
	public void before() {
		factoryToTest = new VariableNameProposalFactory();
		mockedContentProvider = Mockito.mock(ProposalFactoryContentProvider.class);
	}

	@Test
	public void created_proposals_contains_variable_name() throws Exception {
		/* prepare */
		String code = "file = new File()\n\n";
		// a little bit ugly- test depends on gradle model builder, but too much
		// to test otherwise and this is also a good cross
		// check - so we do this this way...
		GradleModelBuilder b = new GradleModelBuilder(new ByteArrayInputStream(code.getBytes()));
		Model model = b.build(null);
		when(mockedContentProvider.getModel()).thenReturn(model);

		int index = code.length();

		/* execute */
		Set<Proposal> proposals = factoryToTest.createProposals(index, mockedContentProvider);

		/* test */
		assertNotNull(proposals);
		assertEquals(1, proposals.size());
		Proposal proposal = proposals.iterator().next();
		assertEquals("file", proposal.getName());
	}
}
