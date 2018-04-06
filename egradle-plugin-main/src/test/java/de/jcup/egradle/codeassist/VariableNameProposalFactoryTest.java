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

import java.io.ByteArrayInputStream;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.model.groovyantlr.AbstractGroovyModelBuilder;
import de.jcup.egradle.core.model.groovyantlr.GradleModelBuilder;

public class VariableNameProposalFactoryTest {

	private VariableNameProposalFactory factoryToTest;
	private ProposalFactoryContentProvider mockedContentProvider;

	@Before
	public void before() {
		factoryToTest = new VariableNameProposalFactory();
		mockedContentProvider = Mockito.mock(ProposalFactoryContentProvider.class);
	}

	@Test
	public void created_proposals_contains_variable_name() throws Exception {
		/* prepare */
		String code = "file = new File()\n\n";
		// a little bit ugly- test depends on gradle model builder, but too much
		// to test otherwise and this is also a good cross
		// check - so we do this this way...
		AbstractGroovyModelBuilder b = new GradleModelBuilder(new ByteArrayInputStream(code.getBytes()));
		Model model = b.build(null);
		when(mockedContentProvider.getModel()).thenReturn(model);

		int index = code.length();

		/* execute */
		Set<Proposal> proposals = factoryToTest.createProposals(index, mockedContentProvider);

		/* test */
		assertNotNull(proposals);
		assertEquals(1, proposals.size());
		Proposal proposal = proposals.iterator().next();
		assertEquals("file", proposal.getLabel());
	}
}
