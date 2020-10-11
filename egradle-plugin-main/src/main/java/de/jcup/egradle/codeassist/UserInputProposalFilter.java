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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class UserInputProposalFilter {

    /**
     * Filter given proposals by content provider information
     * 
     * @param proposals
     * @param contentProvider
     * @return set of proposals, never <code>null</code>
     */
    public Set<Proposal> filter(Set<Proposal> proposals, ProposalFactoryContentProvider contentProvider) {
        if (proposals == null || proposals.isEmpty()) {
            return Collections.emptySet();
        }
        /* we got proposals, so filter unusable ones: */
        String entered = contentProvider.getEditorSourceEnteredAtCursorPosition();
        Set<Proposal> result = filterAndSetupProposals(proposals, entered);
        return result;
    }

    /**
     * Filter given proposals
     * 
     * @param proposals
     * @param entered   relevant code, already entered by user
     * @return
     */
    Set<Proposal> filterAndSetupProposals(Set<Proposal> proposals, String entered) {
        if (StringUtils.isEmpty(entered)) {
            /* no relavant code entered */
            return new LinkedHashSet<>(proposals);
        }
        String enteredLowerCased = entered.toLowerCase();
        Set<Proposal> filteredResult = new LinkedHashSet<>();
        for (Proposal proposal : proposals) {
            String label = proposal.getLabel();
            String codeLowerCased = label.toLowerCase();
            if (codeLowerCased.indexOf(enteredLowerCased) != -1) {
                filteredResult.add(proposal);
            }
        }
        return filteredResult;

    }
}
