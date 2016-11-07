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

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		Control adapter = getAdapter(Control.class);
		if (adapter instanceof StyledText){
			StyledText text = (StyledText) adapter;
			text.addCaretListener(new GradleEditorCaretListener());
		}
		
	}
	
	@Override
	protected void initializeEditor() {
		super.initializeEditor();
	}
	
	@Override
	public void selectAndReveal(int start, int length) {
		super.selectAndReveal(start, length);
		setStatusLineMessage("selected range: start="+start+", length="+length);
	}
	
	@Override
	public void dispose() {
		colorManager.dispose();
		super.dispose();
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
	
	private class GradleEditorCaretListener implements CaretListener{

		@Override
		public void caretMoved(CaretEvent event) {
			setStatusLineMessage("caret moved:"+event.caretOffset);
		}
		
	}
	
}
