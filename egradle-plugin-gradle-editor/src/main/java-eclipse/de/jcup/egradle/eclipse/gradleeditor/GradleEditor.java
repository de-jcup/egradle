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
package de.jcup.egradle.eclipse.gradleeditor;

import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.jcup.egradle.eclipse.api.ColorManager;
import de.jcup.egradle.eclipse.gradleeditor.document.GradleDocumentProvider;
import de.jcup.egradle.eclipse.gradleeditor.outline.GradleEditorContentOutlinePage;


public class GradleEditor extends TextEditor {

	private ColorManager colorManager;
	private GradleEditorContentOutlinePage outlinePage;

	public GradleEditor() {
		colorManager = ColorManager.create();
		setSourceViewerConfiguration(new GradleSourceViewerConfiguration(colorManager));
		setDocumentProvider(new GradleDocumentProvider());
	}
	
	@Override
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
	
	public void setSelectedRange(int offset, int length){
		getSourceViewer().setSelectedRange(offset, length);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		  if (IContentOutlinePage.class.equals(adapter)) {
			  if (outlinePage == null) {
		        	 outlinePage = new GradleEditorContentOutlinePage(this);
		      }
			  return (T) outlinePage;
		  }
		return super.getAdapter(adapter);
	}
	
}
