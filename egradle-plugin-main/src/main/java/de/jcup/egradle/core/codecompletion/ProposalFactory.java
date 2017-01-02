package de.jcup.egradle.core.codecompletion;

import java.util.List;

public interface ProposalFactory {

	/**
	 * Creates proposals for code when cursor is at given offset
	 * @param offset
	 * @param contentProvider provides content for proposals
	 * @return list of proposals, never <code>null</code>
	 */
	public List<Proposal> createProposals(int offset, ProposalFactoryContentProvider contentProvider);
}