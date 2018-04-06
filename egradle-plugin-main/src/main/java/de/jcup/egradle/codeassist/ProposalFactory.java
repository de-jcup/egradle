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

import java.util.Set;

public interface ProposalFactory {

	/**
	 * Creates proposals for code when cursor is at given offset. The factory
	 * implementation must be clever enough to create only interesting parts for
	 * given offset!
	 * 
	 * @param offset
	 *            given offset in code. When offset is negative the result will
	 *            always be an empty list!
	 * @param contentProvider
	 *            provides content for proposals
	 * @return list of proposals, never <code>null</code>
	 */
	public Set<Proposal> createProposals(int offset, ProposalFactoryContentProvider contentProvider);
}