package de.jcup.egradle.core.codecompletion;

import java.util.Set;

import de.jcup.egradle.core.codecompletion.model.CodeBuilder;
import de.jcup.egradle.core.codecompletion.model.Type;
import de.jcup.egradle.core.model.Item;

public class GradleDSLProposalFactory extends AbstractModelProposalFactory{
	
	public GradleDSLProposalFactory(CodeBuilder builder) {
		super(builder);
	}

	/* FIXME ATR, 13.01.2017: continue implmentation: use code completion/language model + generated gradle dsl*/
	@Override
	public Set<Proposal> createProposalsImpl(int offset, ProposalFactoryContentProvider contentProvider) {
		
		Type identifiedType = tryToIdentifyType(offset, contentProvider);
		if (identifiedType==null){
			return null;
		}
		
		Set<Proposal> proposals = super.createProposals(identifiedType);
		return proposals;
	}

	/**
	 * Different situations here to inspect:
	 * <ul>
	 * 	<li>Already entered variable and a dot (and maybe part of method etc.) so method, variables etc. of type there must be detected</li>
	 *  <li>Only entered part of something, without dot so parentItem must be used</li>
	 * </ul>
	 * @param contentProvider
	 * @return types
	 */
	private Type tryToIdentifyType(int offset, ProposalFactoryContentProvider contentProvider) {
		Item outlineItem = contentProvider.getModel().getItemAt(offset);
		 
		return null;
	}

	

}
