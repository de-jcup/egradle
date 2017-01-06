package de.jcup.egradle.core.codecompletion;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.jcup.egradle.core.api.ErrorHandler;
import de.jcup.egradle.core.codecompletion.XMLProposalDataModel.PreparationException;
import de.jcup.egradle.core.codecompletion.XMLProposalDataModel.XMLProposalContainer;
import de.jcup.egradle.core.codecompletion.XMLProposalDataModel.XMLProposalElement;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;

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

	private ItemPathCreator itemPathCreator = new ItemPathCreator();
	private XMLProposalDataModelProvider provider;
	private ErrorHandler errorHandler;
	
	public XMLProposalFactory(XMLProposalDataModelProvider dataModelProvider) {
		if (dataModelProvider==null){
			throw new IllegalArgumentException("data model provider may not be null!");
		}
		this.provider=dataModelProvider;
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
	
	@Override
	public Set<Proposal> createProposalsImpl(int offset, ProposalFactoryContentProvider contentProvider) {
		/* FIXME albert,04.01.2017: keep on implementing*/
		/* FIXME albert,04.01.2017: integrate implementation / use it...*/
		Model outlineModel = contentProvider.getModel();
		if (outlineModel==null){
			return null;
		}
		Item item = outlineModel.getItemAt(offset);
		if (item==null){
			/* FIXME albert,06.01.2017: check is current item null when on root element?!?!? */
			return null;
		}
		List<XMLProposalDataModel> models = provider.getDataModels();
		if (models==null){
			return null;
		}
		if (models.isEmpty()){
			return null;
		}
		
		Set<Proposal> proposals = new LinkedHashSet<>();
		
		String itemPath = itemPathCreator.createPath(item);
		
		for (XMLProposalDataModel model: models){
			if (model==null){
				continue;
			}
			try {
				model.ensurePrepared();

				Set<XMLProposalContainer> possibleParentElements = model.getContainersByPath(itemPath);
				for (XMLProposalContainer possibleParent: possibleParentElements){
					appendProposals(possibleParent, proposals);
				}
			} catch (PreparationException e) {
				if (errorHandler!=null){
					errorHandler.handleError("Was not able to prepare model:"+model.getId(), e);
				}
			}
			
		}
		return proposals;
	}
	/* FIXME albert,06.01.2017: solve problem of cursor inside item and not at end! code completion maybe destroys item! */

	private void appendProposals(XMLProposalContainer possibleParent, Set<Proposal> proposals) {
		List<XMLProposalElement> children = possibleParent.getElements();
		for (XMLProposalElement child: children){
			XMLProposalImpl proposal = new XMLProposalImpl();
			proposal.setCode(child.getName());
			proposal.setName(child.getName());
			proposal.setDescription(child.getDescription());
			/* FIXME albert,06.01.2017: what about types ? */
			/* FIXME albert,06.01.2017: implement duplicate entries (max amount ) */
			proposals.add(proposal);
		}
	}
	
	
	private class XMLProposalImpl extends AbstractProposalImpl{
		
	}

}
