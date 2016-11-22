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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.eclipse.api.ColorManager;
import de.jcup.egradle.eclipse.gradleeditor.document.GradleDocumentProvider;
import de.jcup.egradle.eclipse.gradleeditor.outline.GradleEditorContentOutlinePage;
import de.jcup.egradle.eclipse.gradleeditor.outline.QuickOutlineDialog;

public class GradleEditor extends TextEditor {

	private GradleEditorContentOutlinePage outlinePage;

	/** The COMMAND_ID of this editor as defined in plugin.xml */
	public static final String EDITOR_ID = "org.egradle.editors.GradleEditor";

	/** The COMMAND_ID of the editor context menu */
	public static final String EDITOR_CONTEXT_MENU_ID = EDITOR_ID + ".context";

	/** The COMMAND_ID of the editor ruler context menu */
	public static final String EDITOR_RULER_CONTEXT_MENU_ID = EDITOR_CONTEXT_MENU_ID + ".ruler";

	public GradleEditor() {
		setSourceViewerConfiguration(new GradleSourceViewerConfiguration(getColorManager()));
		setDocumentProvider(new GradleDocumentProvider());
	}

	private ColorManager getColorManager() {
		return Activator.getDefault().getColorManager();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		Control adapter = getAdapter(Control.class);
		if (adapter instanceof StyledText) {
			StyledText text = (StyledText) adapter;
			text.addCaretListener(new GradleEditorCaretListener());
		}
		
		activateGradleEditorContext();

	}

	private void activateGradleEditorContext() {
		IContextService contextService = (IContextService)PlatformUI.getWorkbench()
				.getService(IContextService.class);
		if (contextService!=null){
			contextService.activateContext("org.egradle.editors.GradleEditor.context");
		}
	}

	public void openQuickOutline() {
		Shell shell = getEditorSite().getShell();
		QuickOutlineDialog dialog = new QuickOutlineDialog(GradleEditor.this, shell);
		IDocument document = getDocumentProvider().getDocument(getEditorInput());
		dialog.setInput(document);
		dialog.open();
	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId(EDITOR_CONTEXT_MENU_ID);
		setRulerContextMenuId(EDITOR_RULER_CONTEXT_MENU_ID);
	}

	@Override
	public void selectAndReveal(int start, int length) {
		super.selectAndReveal(start, length);
		setStatusLineMessage("selected range: start=" + start + ", length=" + length);
	}

	@Override
	public void dispose() {
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

	private class GradleEditorCaretListener implements CaretListener {

		@Override
		public void caretMoved(CaretEvent event) {
			if (event == null) {
				return;
			}
			setStatusLineMessage("caret moved:" + event.caretOffset);
			if (outlinePage == null) {
				return;
			}
			outlinePage.onEditorCaretMoved(event.caretOffset);
		}

	}

	public void openSelectedTreeItemInEditor(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object firstElement = ss.getFirstElement();
			if (firstElement instanceof Item) {
				Item item = (Item) firstElement;
				int offset = item.getOffset();
				int length = item.getLength();
				selectAndReveal(offset, length);
			}
		}
	}
}
