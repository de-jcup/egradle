/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UserInputProposalFilterTest {

	private ProposalFactoryContentProvider mockedContentProvider;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private UserInputProposalFilter filterToTest;

	@Before
	public void before() {
		filterToTest = new UserInputProposalFilter();
		mockedContentProvider = mock(ProposalFactoryContentProvider.class);
	}

	@Test
	public void when_ile_was_already_entered_and_proposal_impl_says_file_xile__file_and_xile_are_returned() {
		/* prepare */
		when(mockedContentProvider.getEditorSourceEnteredAtCursorPosition()).thenReturn("ile");

		Set<Proposal> list = new LinkedHashSet<>();
		Proposal mockedProposal1 = mock(Proposal.class);
		when(mockedProposal1.getLabel()).thenReturn("file");

		Proposal mockedProposal2 = mock(Proposal.class);
		when(mockedProposal2.getLabel()).thenReturn("xile");

		list.add(mockedProposal1);
		list.add(mockedProposal2);

		/* execute */
		Set<Proposal> proposals = filterToTest.filter(list, mockedContentProvider);

		/* test */
		assertNotNull(proposals);
		assertEquals(2, proposals.size());
	}

	@Test
	public void when_ole_was_already_entered_and_proposal_impl_says_file_xile_an_empty_list_is_returned() {
		/* prepare */
		when(mockedContentProvider.getEditorSourceEnteredAtCursorPosition()).thenReturn("ole");

		Set<Proposal> list = new LinkedHashSet<>();
		Proposal mockedProposal1 = mock(Proposal.class);
		when(mockedProposal1.getLabel()).thenReturn("file");

		Proposal mockedProposal2 = mock(Proposal.class);
		when(mockedProposal2.getLabel()).thenReturn("xile");

		list.add(mockedProposal1);
		list.add(mockedProposal2);

		/* execute */
		Set<Proposal> proposals = filterToTest.filter(list, mockedContentProvider);

		/* test */
		assertNotNull(proposals);
		assertEquals(0, proposals.size());
	}

	@Test
	public void when_file_was_already_entered_and_proposal_impl_says_file_xile__only_file_is_returned() {
		/* prepare */
		when(mockedContentProvider.getEditorSourceEnteredAtCursorPosition()).thenReturn("file");

		Set<Proposal> list = new LinkedHashSet<>();
		Proposal mockedProposal1 = mock(Proposal.class);
		when(mockedProposal1.getLabel()).thenReturn("file");

		Proposal mockedProposal2 = mock(Proposal.class);
		when(mockedProposal2.getLabel()).thenReturn("xile");

		list.add(mockedProposal1);
		list.add(mockedProposal2);

		/* execute */
		Set<Proposal> proposals = filterToTest.filter(list, mockedContentProvider);

		/* test */
		assertNotNull(proposals);
		assertEquals(1, proposals.size());
	}

	@Test
	public void when_fi_was_already_entered_and_proposal_impl_says_file_xile_and_affiliate_only_file_and_affiliate_are_returned() {
		/* prepare */
		when(mockedContentProvider.getEditorSourceEnteredAtCursorPosition()).thenReturn("fi");

		Set<Proposal> list = new LinkedHashSet<>();
		Proposal mockedProposal1 = mock(Proposal.class);
		when(mockedProposal1.getLabel()).thenReturn("file");

		Proposal mockedProposal2 = mock(Proposal.class);
		when(mockedProposal2.getLabel()).thenReturn("xile");

		Proposal mockedProposal3 = mock(Proposal.class);
		when(mockedProposal3.getLabel()).thenReturn("affiliate");

		list.add(mockedProposal1);
		list.add(mockedProposal2);
		list.add(mockedProposal3);

		/* execute */
		Set<Proposal> proposals = filterToTest.filter(list, mockedContentProvider);

		/* test */
		assertNotNull(proposals);
		assertTrue(proposals.contains(mockedProposal1));
		assertFalse(proposals.contains(mockedProposal2));
		assertTrue(proposals.contains(mockedProposal3));
		assertEquals(2, proposals.size());
	}

}
