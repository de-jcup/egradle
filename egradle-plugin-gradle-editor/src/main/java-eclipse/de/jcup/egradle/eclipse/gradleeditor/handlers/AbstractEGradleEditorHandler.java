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
 package de.jcup.egradle.eclipse.gradleeditor.handlers;

import org.eclipse.ui.IEditorPart;

import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;
import de.jcup.egradle.eclipse.ui.AbstractActiveEditorHandler;
import de.jcup.egradle.eclipse.ui.AbstractGroovyBasedEditor;

public abstract class AbstractEGradleEditorHandler extends AbstractActiveEditorHandler {

	/**
	 * Execute something by using gradle editor instance
	 * @param gradleEditor - never <code>null</code>
	 */
	protected abstract void executeOnGradleEditor(GradleEditor gradleEditor);

	protected void executeOnActiveEditor(IEditorPart editor) {
		if (editor instanceof AbstractGroovyBasedEditor){
			executeOnGradleEditor((GradleEditor) editor);
		}
	}

}