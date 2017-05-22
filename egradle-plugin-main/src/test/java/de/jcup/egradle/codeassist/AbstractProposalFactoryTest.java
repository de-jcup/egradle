/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.egradle.codeassist;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AbstractProposalFactoryTest {

	private TestAbstractProposalFactory factoryToTest;
	private ProposalFactoryContentProvider mockedContentProvider;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void before(){
		factoryToTest=new TestAbstractProposalFactory();
		mockedContentProvider = mock(ProposalFactoryContentProvider.class);
	}

	@Test
	public void no_initial_filtering__when_file_was_already_entered_and_proposal_impl_says_file_xile_an_empty_list_is_returned(){
		/* prepare */
		when(mockedContentProvider.getEditorSourceEnteredAtCursorPosition()).thenReturn("file");
		
		Set<Proposal> list = new LinkedHashSet<>();
		Proposal mockedProposal1 = mock(Proposal.class);
		when(mockedProposal1.getCode()).thenReturn("file");
		
		Proposal mockedProposal2= mock(Proposal.class);
		when(mockedProposal2.getCode()).thenReturn("xile");
		
		list.add(mockedProposal1);
		list.add(mockedProposal2);
		factoryToTest.fakeCreationResult=list;
		
		/* execute */
		Set<Proposal> proposals = factoryToTest.createProposals(1 ,mockedContentProvider);
		
		/* test */
		assertNotNull(proposals);
		assertEquals(2,proposals.size());
	}
	
	@Test
	public void no_initial_filtering__when_fi_was_already_entered_and_proposal_impl_says_file_xile_and_affiliate_only_file_and_affiliate_are_returned(){
		/* prepare */
		when(mockedContentProvider.getEditorSourceEnteredAtCursorPosition()).thenReturn("fi");
		
		Set<Proposal> list = new LinkedHashSet<>();
		Proposal mockedProposal1 = mock(Proposal.class);
		when(mockedProposal1.getCode()).thenReturn("file");
		
		Proposal mockedProposal2= mock(Proposal.class);
		when(mockedProposal2.getCode()).thenReturn("xile");
		
		Proposal mockedProposal3= mock(Proposal.class);
		when(mockedProposal3.getCode()).thenReturn("affiliate");

		list.add(mockedProposal1);
		list.add(mockedProposal2);
		list.add(mockedProposal3);
		factoryToTest.fakeCreationResult=list;
		
		/* execute */
		Set<Proposal> proposals = factoryToTest.createProposals(1 ,mockedContentProvider);
		
		/* test */
		assertNotNull(proposals);
		assertTrue(proposals.contains(mockedProposal1));
		assertTrue(proposals.contains(mockedProposal2));
		assertTrue(proposals.contains(mockedProposal3));
		assertEquals(3,proposals.size());
	}
	
	
	@Test
	public void proposals_is_an_empty_list_even_when_implementation_returns_null(){
		/* prepare */
		int index = 1;
		factoryToTest.fakeCreationResult=null;
		
		/* execute */
		Set<Proposal> proposals = factoryToTest.createProposals(index,mockedContentProvider);
		
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
		factoryToTest.fakeCreationResult=Collections.singleton(mock(ItemProposalImpl.class));
		/* execute */
		Set<Proposal> proposals = factoryToTest.createProposals(index,mockedContentProvider);
		
		/* test */
		assertNotNull(proposals);
		assertEquals(0, proposals.size());
	}
	
	private class TestAbstractProposalFactory extends AbstractProposalFactory{

		private Set<Proposal> fakeCreationResult;


		@Override
		protected Set<Proposal> createProposalsImpl(int offset, ProposalFactoryContentProvider contentProvider) {
			return fakeCreationResult;
		}
		
	}
}
