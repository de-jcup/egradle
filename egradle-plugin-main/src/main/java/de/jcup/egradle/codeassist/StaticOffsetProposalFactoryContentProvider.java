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

import static org.apache.commons.lang3.Validate.*;

import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.core.ModelProvider;
import de.jcup.egradle.core.TextProvider;
import de.jcup.egradle.core.TextProviderException;
import de.jcup.egradle.core.model.Model;

/**
 * This class represents a provider which does only support data for same
 * offsets. So on every start of a new proposal session a new provider must be
 * created.
 * 
 * @author Albert Tregnaghi
 *
 */
public class StaticOffsetProposalFactoryContentProvider implements ProposalFactoryContentProvider {
	private String relevant;
	private ModelProvider modelProvider;
	private TextProvider textProvider;
	private RelevantCodeCutter codeCutter;
	private int length;
	private int offsetOfFirstCharacterInLine;
	private int offset;
	private GradleFileType fileType;
	private String lineTextBeforeCursorPosition;

	public StaticOffsetProposalFactoryContentProvider(GradleFileType fileType, ModelProvider modelProvider,
			TextProvider textProvider, RelevantCodeCutter codeCutter, int offset)
			throws ProposalFactoryContentProviderException {
		notNull(fileType, "'fileType' may not be null");
		notNull(modelProvider, "'modelProvider' may not be null");
		notNull(textProvider, "'textProvider' may not be null");
		notNull(codeCutter, "'codeCutter' may not be null");

		this.fileType = fileType;
		this.offset = offset;
		this.modelProvider = modelProvider;
		this.textProvider = textProvider;
		this.codeCutter = codeCutter;
		try {
			offsetOfFirstCharacterInLine = textProvider.getLineOffset(offset);
			this.length = offset - offsetOfFirstCharacterInLine;
			this.lineTextBeforeCursorPosition = textProvider.getText(offsetOfFirstCharacterInLine, length);
		} catch (TextProviderException e) {
			throw new ProposalFactoryContentProviderException("Cannot get line text before cursor position", e);
		}
	}

	@Override
	public Model getModel() {
		return modelProvider.getModel();
	}

	@Override
	public String getEditorSourceEnteredAtCursorPosition() {
		/*
		 * the content provider is only used one time per cursor offset - so we
		 * simply cache the relevant calculation iniside internal string to
		 * speed up...
		 */
		if (relevant == null) {
			String code = textProvider.getText();
			relevant = codeCutter.getRelevantCode(code, offset);
		}
		return relevant;

	}

	@Override
	public int getOffsetOfFirstCharacterInLine() {
		return offsetOfFirstCharacterInLine;
	}

	@Override
	public String getLineTextBeforeCursorPosition() {
		return lineTextBeforeCursorPosition;
	}

	@Override
	public GradleFileType getFileType() {
		return fileType;
	}
}
