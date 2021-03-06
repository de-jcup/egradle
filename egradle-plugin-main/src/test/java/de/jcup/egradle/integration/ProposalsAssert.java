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
package de.jcup.egradle.integration;

import static org.junit.Assert.*;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.AbstractProposalImpl;
import de.jcup.egradle.codeassist.GradleDSLProposalFactory.ModelProposal;
import de.jcup.egradle.codeassist.Proposal;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.gradle.estimation.GradleLanguageElementEstimater.EstimationResult;

public class ProposalsAssert {

    public static ProposalsAssert assertThat(Set<Proposal> proposals) {
        if (proposals == null) {
            fail("hover proposals is null!");
        }
        return new ProposalsAssert(proposals);
    }

    private Set<Proposal> proposals;

    private ProposalsAssert(Set<Proposal> proposals) {
        this.proposals = proposals;
    }

    public ProposalAssert containsAtLeastOneProposal() {
        assertFalse("No proposals found!", proposals.isEmpty());
        return new ProposalAssert(proposals.iterator().next());
    }

    public ProposalAssert containsProposalWithLabel(String label) {
        for (Proposal proposal : proposals) {
            String name = proposal.getLabel();
            if (name.equals(label)) {
                return new ProposalAssert(proposal);
            }
        }
        /* failure! so build information */
        StringBuilder message = new StringBuilder();
        message.append("no proposal avaialble having label:").append(label).append("\nBut did contain:\n");
        for (Proposal proposal : proposals) {
            String name = proposal.getLabel();
            message.append("  -").append(name).append('\n');
        }
        fail(message.toString());
        throw new IllegalArgumentException("Junit test must have failed already!");
    }

    public class ProposalAssert {

        private Proposal proposal;

        public ProposalAssert(Proposal proposal) {
            this.proposal = proposal;
        }

        public ProposalsAssert and() {
            return ProposalsAssert.this;
        }

        public ProposalAssert whichHasDescription() {
            String description = proposal.getDescription();
            if (StringUtils.isBlank(description)) {
                fail("description is blank!");
            }
            return this;
        }

        public ProposalAssert hasTemplate(String expectedCode) {
            if (!(proposal instanceof AbstractProposalImpl)) {
                throw new IllegalArgumentException("proposal is not AbstractProposalImpl... so this is not testable here! ");
            }
            AbstractProposalImpl apo = (AbstractProposalImpl) proposal;
            assertEquals(expectedCode, apo.getLazyBuilder().getTemplate());
            return this;
        }

        public ProposalAssert whichHasReasonType(String typeNameAsString) {
            if (!(proposal instanceof ModelProposal)) {
                throw new IllegalArgumentException("proposal is not ModelProposal... so this is not testable here! ");
            }
            ModelProposal mp = (ModelProposal) proposal;
            EstimationResult reason = mp.getReason();
            assertNotNull("Reason is null! for given proposal!", reason);
            Type type = reason.getElementType();
            assertNotNull("Reasons element type is null! for given proposal!", type);
            String foundTypeName = type.getName();
            assertEquals("Unexepcted type as reason found!", typeNameAsString, foundTypeName);
            return this;
        }
    }

}
