package de.jcup.egradle.codecompletion;

import java.util.List;

/**
 * A provider to support {@link XMLProposalDataModel}. Does also 
 * caching reload etc.
 * 
 * @author albert
 *
 */
public interface XMLProposalDataModelProvider {
	
	/**
	 * Returns a list of xml proposal data models. If the models are reloaded or 
	 * cached depends on implementation.
	 * @return list with data models, never <code>null</code>
	 */
	public List<XMLProposalDataModel> getDataModels();
	
	

}
