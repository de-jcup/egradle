package de.jcup.egradle.core.codecompletion;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

public class AbstractProposalFactoryTest {

	private TestAbstractProposalFactory factoryToTest;
	private ProposalFactoryContentProvider mockedContentProvider;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void before(){
		factoryToTest=new TestAbstractProposalFactory();
		mockedContentProvider = Mockito.mock(ProposalFactoryContentProvider.class);
	}
	
	@Test
	public void proposals_is_an_empty_list_even_when_implementation_returns_null(){
		/* prepare */
		int index = 1;
		factoryToTest.fakeCreationResult=null;
		
		/* execute */
		List<Proposal> proposals = factoryToTest.createProposals(index,mockedContentProvider);
		
		/* test */
		assertNotNull(proposals);
		assertEquals(0, proposals.size());
	}
	
	@Test
	public void throws_illegal_argument_exception_when_contentprovider_is_null(){
		/* prepare */
		mockedContentProvider = null;
		int index = 1;
		
		/* test -later*/
		expectedException.expect(IllegalArgumentException.class);
		
		/* execute */
		factoryToTest.createProposals(index,mockedContentProvider);
	}
	
	@Test
	public void proposals_is_an_empty_list_when_index_negative_one_even_when_implementation_returns_a_filled_list(){
		/* prepare */
		int index = -1;
		factoryToTest.fakeCreationResult=Collections.singletonList(Mockito.mock(ProposalImpl.class));
		/* execute */
		List<Proposal> proposals = factoryToTest.createProposals(index,mockedContentProvider);
		
		/* test */
		assertNotNull(proposals);
		assertEquals(0, proposals.size());
	}
	
	private class TestAbstractProposalFactory extends AbstractProposalFactory{

		private List<Proposal> fakeCreationResult;


		@Override
		protected List<Proposal> createProposalsImpl(int offset, ProposalFactoryContentProvider contentProvider) {
			return fakeCreationResult;
		}
		
	}
}
