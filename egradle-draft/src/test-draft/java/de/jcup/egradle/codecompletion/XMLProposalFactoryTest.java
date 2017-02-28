package de.jcup.egradle.codecompletion;

import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.codecompletion.Proposal;
import de.jcup.egradle.codecompletion.ProposalFactoryContentProvider;
import de.jcup.egradle.codecompletion.XMLProposalDataModel;
import de.jcup.egradle.codecompletion.XMLProposalDataModelProvider;
import de.jcup.egradle.codecompletion.XMLProposalFactory;
import de.jcup.egradle.codecompletion.XMLProposalDataModel.XMLProposalData;
import de.jcup.egradle.codecompletion.XMLProposalDataModel.XMLProposalElement;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;

public class XMLProposalFactoryTest {

	private XMLProposalDataModelProvider mockedDataModelProvider;
	private XMLProposalFactory factoryToTest;
	private ProposalFactoryContentProvider mockedContentProvider;
	
	
	@Before
	public void before(){
		mockedDataModelProvider= mock(XMLProposalDataModelProvider.class);
		mockedContentProvider=mock(ProposalFactoryContentProvider.class);
		
		
		factoryToTest = new XMLProposalFactory(mockedDataModelProvider);
	}
	
	@Test
	public void when_data_model_provider_returns_null_proposals_is_empty_list() {
		/* prepare */
		when(mockedDataModelProvider.getDataModels()).thenReturn(null);
		
		
		/* create */
		Set<Proposal> proposals = factoryToTest.createProposals(0, mockedContentProvider);

		/* test */
		assertNotNull(proposals);
		assertEquals(0,proposals.size());
	}
	
	@Test
	public void when_cursor_at_root_and_model_contains_parent_with_child_only_parent_is_in_proposal() throws Exception {
		/* prepare */
		XMLProposalDataModel model = createParent1Child1Model();
		
		when(mockedDataModelProvider.getDataModels()).thenReturn(singletonList(model));
		
		Model mockedOutlineModel = mock(Model.class);
		when(mockedOutlineModel.getParentItemOf(0)).thenReturn(new Item());
		when(mockedContentProvider.getModel()).thenReturn(mockedOutlineModel);
		
		/* execute */
		Set<Proposal> proposals = factoryToTest.createProposals(0, mockedContentProvider);

		/* test */
		assertNotNull(proposals);
		assertEquals(1,proposals.size());
		
		Proposal p = proposals.iterator().next();
		assertNotNull(p);
		assertEquals("parent1", p.getName());
	}

	private XMLProposalDataModel createParent1Child1Model() {
		XMLProposalDataModel model = new XMLProposalDataModel();
		XMLProposalData proposalData = new XMLProposalData();
		XMLProposalElement parent1Element = new XMLProposalElement();
		parent1Element.name="parent1";
		parent1Element.description="parent1Description";
		
		XMLProposalElement parent1Child1 = new XMLProposalElement();
		parent1Child1.name="parent1Child1";
		parent1Child1.description="parent1Child1Description";
		
		parent1Element.getElements().add(parent1Child1);
		proposalData.getElements().add(parent1Element);
		
		model.getProposals().add(proposalData);
		return model;
	}

}
