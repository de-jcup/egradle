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

import de.jcup.egradle.codeassist.SourceCodeInsertionSupport.InsertionData;

public abstract class AbstractLazyCodeBuilder implements LazyCodeBuilder {

	private static SourceCodeInsertionSupport insertSupport = new SourceCodeInsertionSupport();
	private String code;
	private String template;
	private int cursorPos;

	public String getCode(AbstractProposalImpl proposal, String textBeforeColumn) {
		ensureData(proposal, textBeforeColumn);
		return code;
	}

	private void ensureData(AbstractProposalImpl proposal, String textBeforeColumn) {
		ensureTemplate();
		if (code == null) {
			InsertionData insertData = insertSupport.prepareInsertionString(template, textBeforeColumn);

			this.cursorPos = insertData.cursorOffset;
			this.code = insertData.sourceCode;

			if (code == null) {
				code = "";
			}
		}

	}
	

	public String getTemplate() {
		ensureTemplate();
		return template;
	}
	
	private void ensureTemplate() {
		if (template==null){
			template=createTemplate();
			if (template==null){
				template="";
			}
		}
	}

	protected abstract String createTemplate();

	@Override
	public int getCursorPos(AbstractProposalImpl proposal, String textBeforeColumn) {
		ensureData(proposal, textBeforeColumn);
		return cursorPos;
	}

}
