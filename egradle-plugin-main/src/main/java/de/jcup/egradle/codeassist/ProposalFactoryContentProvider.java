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

import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.core.model.Model;

/**
 * Provides content necessary for proposal factories
 * @author albert
 *
 */
public interface ProposalFactoryContentProvider {

	/**
	 * @return model
	 */
	public Model getModel();

	/**
	 * @return relevant editor source code at given cursor position or <code>null</code>
	 */
	public String getEditorSourceEnteredAtCursorPosition();

	/**
	 * 
	 * @return offset of first character in given line or -1
	 */
	public int getOffsetOfFirstCharacterInLine();

	/**
	 * @return column of given offset
	 */
	public String getLineTextBeforeCursorPosition();

	public GradleFileType getFileType();

}
