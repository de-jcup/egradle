package de.jcup.egradle.core.codecompletion;

import java.util.Set;
import java.util.TreeSet;

import de.jcup.egradle.core.codecompletion.model.CodeBuilder;
import de.jcup.egradle.core.codecompletion.model.Method;
import de.jcup.egradle.core.codecompletion.model.Type;

public abstract class AbstractModelProposalFactory extends AbstractProposalFactory{

	private CodeBuilder builder;


	public AbstractModelProposalFactory(CodeBuilder builder){
		if (builder==null){
			throw new IllegalArgumentException("code builder may not be null");
		}
		this.builder=builder;
	}
	
	public Set<Proposal> createProposals(Type identifiedType) {
		Set<Proposal> proposals = new TreeSet<>();
		/* build propsals */
		for (Method method: identifiedType.getMethods()){
			ModelProposal p = new ModelProposal();
			p.setName(method.getName());
			p.setType(identifiedType.getName());
			p.setCode(builder.createCode(method));
			/* FIXME ATR, 13.01.2017:  reuse code from xml proposal factory  */
//			p.setCursorPos(cursorOffset);
			p.setDescription(method.getDescription());
			proposals.add(p);
		}
		/* FIXME ATR, 13.01.2017: keep on implementing */
		return null;
	}
	
	protected class ModelProposal extends AbstractProposalImpl{
		
	}
}
