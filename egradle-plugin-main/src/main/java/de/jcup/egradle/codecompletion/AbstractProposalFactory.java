package de.jcup.egradle.codecompletion;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Abstract proposal factory implementation as base for others
 * @author albert
 *
 */
public abstract class AbstractProposalFactory implements ProposalFactory {
	
	public final Set<Proposal> createProposals(int offset, ProposalFactoryContentProvider contentProvider) {
		if (contentProvider==null){
			throw new IllegalArgumentException("proposal factorycontent provider may not be null!");
		}
		if (offset<0){
			return Collections.emptySet();
		}
		Set<Proposal> result = createProposalsImpl(offset,contentProvider);
		if (result==null){
			return Collections.emptySet();
		}
		return result;
	}
	
	/**
	 * Create proposals implementation. Returned proposals are depending on given offset. Factory will always
	 * try to return only clever values means, only values which are possible at current context of given index position!
	 * At this time there must be no filtering to already given text, this is done later in {@link #filterAndSetupProposals(List, int, ProposalFactoryContentProvider)}!
	 * @param offset never negative
	 * @param contentProvider - is not <code>null</code> here
	 * @return created proposals or <code>null</code>
	 */
	protected abstract Set<Proposal> createProposalsImpl(int offset, ProposalFactoryContentProvider contentProvider) ;
	
}
