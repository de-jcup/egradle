package de.jcup.egradle.test.integregation;

import static org.junit.Assert.*;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.AbstractProposalImpl;
import de.jcup.egradle.codeassist.Proposal;
import de.jcup.egradle.test.integregation.ProposalsAssert.ProposalAssert;

public class ProposalsAssert {

	public static ProposalsAssert assertThat(Set<Proposal> proposals){
		if (proposals==null){
			fail("hover proposals is null!");
		}
		return new ProposalsAssert(proposals);
	}

	private Set<Proposal> proposals;
	
	private ProposalsAssert(Set<Proposal> proposals){
		this.proposals=proposals;
	}

	public ProposalsAssert hasAtLeastOneProposal() {
		assertFalse("No proposals found!", proposals.isEmpty());
		return this;
	}

	public ProposalAssert assertProposalHavingLabel(String label) {
		for (Proposal proposal: proposals){
			String name = proposal.getLabel();
			if (name.equals(label)){
				return new ProposalAssert(proposal);
			}
		}
		fail("no proposal avaialble having label:"+label);
		throw new IllegalArgumentException("Junit test must have failed already!");
	}
	
	
	public class ProposalAssert{

		private Proposal proposal;

		public ProposalAssert(Proposal proposal) {
			this.proposal=proposal;
		}
		
		public ProposalsAssert assertDone(){
			return ProposalsAssert.this;
		}

		public ProposalAssert hasDescription() {
			String description = proposal.getDescription();
			if (StringUtils.isBlank(description)){
				fail("description is blank!");
			}
			return this;
		}

		public ProposalAssert hasTemplate(String expectedCode) {
			if (! (proposal instanceof AbstractProposalImpl)){
				throw new IllegalArgumentException("proposal is not AbstractProposalImpl... so this is not testable here! ");
			}
			AbstractProposalImpl apo = (AbstractProposalImpl) proposal;
			assertEquals(expectedCode, apo.getLazyBuilder().getTemplate());
			return this;
		}
	}
	
}
