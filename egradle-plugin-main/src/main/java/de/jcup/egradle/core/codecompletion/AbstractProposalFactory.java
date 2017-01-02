package de.jcup.egradle.core.codecompletion;

import java.util.Collections;
import java.util.List;

/**
 * Abstract proposal factory implementation as base for others
 * @author albert
 *
 */
public abstract class AbstractProposalFactory implements ProposalFactory {
	
	public final List<Proposal> createProposals(int offset, ProposalFactoryContentProvider contentProvider) {
		if (contentProvider==null){
			throw new IllegalArgumentException("proposal factorycontent provider may not be null!");
		}
		if (offset<0){
			return Collections.emptyList();
		}
		List<Proposal> result = createProposalsImpl(offset,contentProvider);
		if (result==null){
			return Collections.emptyList();
		}
		return result;
	}
	
	/**
	 * Create proposals implementation
	 * @param offset never negative
	 * @param contentProvider - is not <code>null</code> here
	 * @return created proposals or <code>null</code>
	 */
	protected abstract List<Proposal> createProposalsImpl(int offset, ProposalFactoryContentProvider contentProvider) ;
}
