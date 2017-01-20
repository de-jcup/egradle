package de.jcup.egradle.codecompletion;

import java.util.Set;
import java.util.TreeSet;

import de.jcup.egradle.codecompletion.dsl.CodeBuilder;
import de.jcup.egradle.codecompletion.dsl.Method;
import de.jcup.egradle.codecompletion.dsl.Property;
import de.jcup.egradle.codecompletion.dsl.Type;

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
		for (Property property: identifiedType.getProperties()){
			ModelProposal p = new ModelProposal();
			p.type=ModelProposalType.PROPERTY;
			p.setName(property.getName());
			p.setType(identifiedType.getName());
			p.setCode(builder.createClosure(property));
			/* FIXME ATR, 13.01.2017:  reuse code from xml proposal factory  */
//			p.setCursorPos(cursorOffset);
			p.setDescription(property.getDescription());
			proposals.add(p);
		}
		for (Method method: identifiedType.getMethods()){
			ModelProposal p = new ModelProposal();
			p.type=ModelProposalType.METHOD;
			p.setName(method.getName());
			p.setType(identifiedType.getName());
			p.setCode(builder.createClosure(method));
			/* FIXME ATR, 13.01.2017:  reuse code from xml proposal factory  */
//			p.setCursorPos(cursorOffset);
			p.setDescription(method.getDescription());
			proposals.add(p);
		}
		/* FIXME ATR, 13.01.2017: keep on implementing */
		return proposals;
	}
	
	public enum ModelProposalType{
		METHOD,
		PROPERTY,
	}
	
	public class ModelProposal extends AbstractProposalImpl{
		
		private ModelProposalType type;
		
		
		public boolean isMethod(){
			return ModelProposalType.METHOD.equals(type);
		}
		
		public boolean isProperty(){
			return ModelProposalType.PROPERTY.equals(type);
		}
	}
}
