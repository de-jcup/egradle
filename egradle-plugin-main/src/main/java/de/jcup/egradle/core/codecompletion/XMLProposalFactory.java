package de.jcup.egradle.core.codecompletion;

import java.util.Set;

/**
 * A special proposal factory which will create proposals by current position inside outline model and
 * xml information.<br><br>
 * 
 * A example (not real just a meta model:)
 * 
 * <br><br>
 * <pre>
 * xml
 *   model id="gradle.default"
 *   	proposal
 *   		context
 *   			root
 *   			  path="."
 *   			  path=".allProjects"
 *   			  path=".project"
 *   		element="repositories"
 *   			description="Defines repositories available inside the current context"
 *   			proposal value="mavenLocal()" max="1"
 *   				description="use your local maven repostory"
 *   			proposal value="mavenCentral()" max="1"
 *   		value ="apply plugin: '$cursor'"
 *   		value ="apply from: '$cursor'"
 *   
 *   		proposal value="group = "$cursor"			
 *   		
 *   		element="dependencies"
 *   			value="testCompile $cursor"
 *   				description="xxx"
 *   			value="testCompile"
 *   
 *   			
 *   			
 * </pre>
 * @see XMLProposalDataModel
 * @author albert
 *
 */
public class XMLProposalFactory extends AbstractProposalFactory{

	private XMLProposalDataModelProvider provider;

	public XMLProposalFactory(XMLProposalDataModelProvider provider) {
		if (provider==null){
			throw new IllegalArgumentException("data model provider may not be null!");
		}
		this.provider=provider;
	}
	
	@Override
	public Set<Proposal> createProposalsImpl(int offset, ProposalFactoryContentProvider contentProvider) {
		/* FIXME albert,04.01.2017: implement + use XMLProposalDataModel */
		
		return null;
	}

}
