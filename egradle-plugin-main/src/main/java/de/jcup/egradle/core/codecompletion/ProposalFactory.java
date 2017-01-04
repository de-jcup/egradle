package de.jcup.egradle.core.codecompletion;

import java.util.List;

public interface ProposalFactory {

	/**
	 * Creates proposals for code when cursor is at given offset. The factory implementation must be clever enough 
	 * to create only interesting parts for given offset!
	 * 
	 * @param offset given offset in code. When offset is negative the result will always be an empty list!
	 * @param contentProvider provides content for proposals
	 * @return list of proposals, never <code>null</code>
	 */
	public List<Proposal> createProposals(int offset, ProposalFactoryContentProvider contentProvider);
}