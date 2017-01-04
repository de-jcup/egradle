package de.jcup.egradle.core.codecompletion;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

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
		/* we got proposals, so filter unusable ones:*/
		String entered = contentProvider.getEditorSourceEnteredAt(offset);
		result = filterAndSetupProposals(result, entered);
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


	/**
	 * Filter given proposals
	 * @param proposals
	 * @param entered relevant code, already entered by user
	 * @return
	 */
	protected Set<Proposal> filterAndSetupProposals(Set<Proposal> proposals, String entered){
		if (StringUtils.isEmpty(entered)){
			/* no relavant code entered*/
			return new LinkedHashSet<>(proposals);
		}
		Set<Proposal> filteredResult = new LinkedHashSet<>();
		for (Proposal proposal: proposals){
			String code = proposal.getCode();
			if (code.indexOf(entered)!=-1){
				if (code.equals(entered)){
					// already complete entered, so ignore!
					/* FIXME albert,04.01.2017: rethink about this behaviour! */
					continue;
				}
				filteredResult.add(proposal);
			}
		}
		return filteredResult;
		
	}
}
