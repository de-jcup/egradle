package de.jcup.egradle.core.codecompletion;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.jcup.egradle.core.api.ErrorHandler;
import de.jcup.egradle.core.codecompletion.SourceCodeInsertionSupport.InsertionData;
import de.jcup.egradle.core.codecompletion.XMLProposalDataModel.PreparationException;
import de.jcup.egradle.core.codecompletion.XMLProposalDataModel.XMLProposalContainer;
import de.jcup.egradle.core.codecompletion.XMLProposalDataModel.XMLProposalElement;
import de.jcup.egradle.core.codecompletion.XMLProposalDataModel.XMLProposalValue;
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
 *   			  path=""
 *   			  path="allProjects"
 *   			  path="project"
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
	/* FIXME ATR, 11.01.2017: change xml : use closure instead of element */
	/* FIXME ATR, 11.01.2017: change xml : use instruction instead of value */
	/* FIXME ATR, 11.01.2017: https://docs.gradle.org/current/dsl/ integrate, or write generator */
	/* FIXME ATR, 11.01.2017: create issue for gradles own build system for documentation to create xml proposal files automatically
	 * see https://github.com/gradle/gradle/tree/master/subprojects/docs
	 * see https://github.com/gradle/gradle/blob/master/buildSrc/src/main/groovy/org/gradle/build/docs/dsl/source/ExtractDslMetaDataTask.groovy
	 *  */
	/* FIXME ATR, 11.01.2017: fork gradle project and adopt doc subproject of gradle for generator */
	/* FIXME ATR, 11.01.2017: use gradle versions in template pathes (very important for generated xml proposals!)*/
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
		Model outlineModel = contentProvider.getModel();
		if (outlineModel==null){
			return null;
		}
		Item parentItem = outlineModel.getParentItemAt(offset);
		if (parentItem==null){
			return null;
		}
		List<XMLProposalDataModel> models = provider.getDataModels();
		if (models==null){
			return null;
		}
		if (models.isEmpty()){
			return null;
		}
		String textBeforeColumn = contentProvider.getLineTextBeforeCursorPosition()+1;
		Set<Proposal> proposals = new LinkedHashSet<>();
		
		String itemPath = itemPathCreator.createPath(parentItem);
		
		for (XMLProposalDataModel model: models){
			if (model==null){
				continue;
			}
			try {
				Set<XMLProposalContainer> possibleParentElements = model.getContainersByPath(itemPath);
				for (XMLProposalContainer possibleParent: possibleParentElements){
					appendProposals(possibleParent, proposals,textBeforeColumn);
				}
			} catch (PreparationException e) {
				if (errorHandler!=null){
					errorHandler.handleError("Was not able to prepare model:"+model.getId(), e);
				}
			}
			
		}
		return proposals;
	}
	
	/* FIXME ATR, 11.01.2017: in some circumstances the code completion did not work any more - must be tested in UI  */
	/* FIXME albert,06.01.2017: solve problem of cursor inside item and not at end! code completion maybe destroys item! */
	SourceCodeInsertionSupport insertSupport = new SourceCodeInsertionSupport();
	
	private void appendProposals(XMLProposalContainer possibleParent, Set<Proposal> proposals, String textBeforeColumn) {
		/* elements */
		/* FIXME albert,06.01.2017: what about types ? */
		/* FIXME albert,06.01.2017: implement duplicate entries (max amount ) */
		/* FIXME ATR, 11.01.2017: implementation change - be aware about using generated groovy dsl xml only! */
		List<XMLProposalElement> children = possibleParent.getElements();
		for (XMLProposalElement child: children){
			XMLProposalImpl proposal = new XMLProposalImpl();
			InsertionData insertData = insertSupport.prepareInsertionString(child.getCode(),textBeforeColumn);
			proposal.setCode(insertData.sourceCode);
			proposal.setName(child.getName());
			proposal.setDescription(child.getDescription());
			proposal.setCursorPos(insertData.cursorOffset);
			proposals.add(proposal);
		}
		/* values */
		List<XMLProposalValue> values = possibleParent.getValues();
		for (XMLProposalValue value: values){
			XMLProposalImpl proposal = new XMLProposalImpl();
			InsertionData insertData = insertSupport.prepareInsertionString(value.getCode(),textBeforeColumn);
			proposal.setCode(insertData.sourceCode);
			proposal.setName(value.getName());
			proposal.setDescription(value.getDescription());
			proposal.setCursorPos(insertData.cursorOffset);
			proposals.add(proposal);
		}
	}
	
	/**
	 * Extra proposals class for xml variant
	 * @author albert
	 *
	 */
	private class XMLProposalImpl extends AbstractProposalImpl{

		
		
	}

}
