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
import java.util.List;
import java.util.Set;

/**
 * Abstract proposal factory implementation as base for others
 * 
 * @author albert
 *
 */
abstract class AbstractProposalFactory implements FilterableProposalFactory {

    private boolean ignoreGetterOrSetter;

    public void setFilterGetterAndSetter(boolean ignoreGetterOrSetter) {
        this.ignoreGetterOrSetter = ignoreGetterOrSetter;
    }

    protected boolean isIgnoreGetterOrSetter() {
        return ignoreGetterOrSetter;
    }

    /**
     * Creates proposals for given offset.
     */
    public final Set<Proposal> createProposals(int offset, ProposalFactoryContentProvider contentProvider) {
        if (contentProvider == null) {
            throw new IllegalArgumentException("proposal factorycontent provider may not be null!");
        }
        if (offset < 0) {
            return Collections.emptySet();
        }
        Set<Proposal> result = createProposalsImpl(offset, contentProvider);
        if (result == null) {
            return Collections.emptySet();
        }
        return result;
    }

    /**
     * Create proposals implementation. Returned proposals are depending on given
     * offset. Factory will always try to return only clever values means, only
     * values which are possible at current context of given index position! At this
     * time there must be no filtering to already given text, this is done later in
     * {@link #filterAndSetupProposals(List, int, ProposalFactoryContentProvider)}!
     * 
     * @param offset          never negative
     * @param contentProvider - is not <code>null</code> here
     * @return created proposals or <code>null</code>
     */
    protected abstract Set<Proposal> createProposalsImpl(int offset, ProposalFactoryContentProvider contentProvider);

}
