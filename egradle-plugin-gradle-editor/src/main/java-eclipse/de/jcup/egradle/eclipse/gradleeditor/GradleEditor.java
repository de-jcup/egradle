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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.eclipse.api.ColorManager;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.gradleeditor.document.GradleDocumentProvider;
import de.jcup.egradle.eclipse.gradleeditor.outline.GradleEditorContentOutlinePage;
import de.jcup.egradle.eclipse.gradleeditor.outline.GradleEditorOutlineContentProvider;
import de.jcup.egradle.eclipse.gradleeditor.outline.QuickOutlineDialog;
import de.jcup.egradle.eclipse.gradleeditor.preferences.EGradleEditorPreferenceConstants;

public class GradleEditor extends TextEditor {

	/** The COMMAND_ID of this editor as defined in plugin.xml */
	public static final String EDITOR_ID = "org.egradle.editors.GradleEditor";
	/** The COMMAND_ID of the editor context menu */
	public static final String EDITOR_CONTEXT_MENU_ID = EDITOR_ID + ".context";
	/** The COMMAND_ID of the editor ruler context menu */
	public static final String EDITOR_RULER_CONTEXT_MENU_ID = EDITOR_CONTEXT_MENU_ID + ".ruler";

	protected final static char[] BRACKETS = { '{', '}', '(', ')', '[', ']', '<', '>' };

	/** Preference key for matching brackets. */
	protected final static String MATCHING_BRACKETS = EGradleEditorPreferenceConstants.P_EDITOR_MATCHING_BRACKETS
			.getId();

	/**
	 * Preference key for highlighting bracket at caret location.
	 * 
	 * @since 3.8
	 */
	protected final static String HIGHLIGHT_BRACKET_AT_CARET_LOCATION = EGradleEditorPreferenceConstants.P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION
			.getId();

	/**
	 * Preference key for enclosing brackets.
	 * 
	 * @since 3.8
	 */
	protected final static String HIGHLIGHT_ENCLOSING_BRACKETS = EGradleEditorPreferenceConstants.P_EDITOR_ENCLOSING_BRACKETS
			.getId();

	/** Preference key for matching brackets color. */
	protected final static String MATCHING_BRACKETS_COLOR = EGradleEditorPreferenceConstants.P_EDITOR_MATCHING_BRACKETS_COLOR
			.getId();

	/*
	 * Copy of org.eclipse.jface.text.source.DefaultCharacterPairMatcher.
	 * getOffsetAdjustment(IDocument, int, int)
	 */
	private static int getOffsetAdjustment(IDocument document, int offset, int length) {
		if (length == 0 || Math.abs(length) > 1)
			return 0;
		try {
			if (length < 0) {
				if (isOpeningBracket(document.getChar(offset))) {
					return 1;
				}
			} else {
				if (isClosingBracket(document.getChar(offset - 1))) {
					return -1;
				}
			}
		} catch (BadLocationException e) {
			// do nothing
		}
		return 0;
	}

	/*
	 * Copy of
	 * org.eclipse.jface.text.source.MatchingCharacterPainter.getSignedSelection
	 * (ISourceViewer)
	 */
	private static final IRegion getSignedSelection(ISourceViewer sourceViewer) {
		Point viewerSelection = sourceViewer.getSelectedRange();

		StyledText text = sourceViewer.getTextWidget();
		Point selection = text.getSelectionRange();
		if (text.getCaretOffset() == selection.x) {
			viewerSelection.x = viewerSelection.x + viewerSelection.y;
			viewerSelection.y = -viewerSelection.y;
		}

		return new Region(viewerSelection.x, viewerSelection.y);
	}

	private static boolean isClosingBracket(char character) {
		for (int i = 1; i < BRACKETS.length; i += 2) {
			if (character == BRACKETS[i])
				return true;
		}
		return false;
	}

	private static boolean isOpeningBracket(char character) {
		for (int i = 0; i < BRACKETS.length; i += 2) {
			if (character == BRACKETS[i])
				return true;
		}
		return false;
	}

	private GradleEditorContentOutlinePage outlinePage;

	private DelayedDocumentListener documentListener;

	private GradleEditorOutlineContentProvider contentProvider;

	private Object monitor = new Object();

	private boolean quickOutlineOpened;

	private int lastCaretPosition;

	private boolean dirty;

	private GradlePairMatcher bracketMatcher = new GradlePairMatcher(BRACKETS);

	private List<IRegion> previousSelections;

	public GradleEditor() {
		setSourceViewerConfiguration(new GradleSourceViewerConfiguration(getColorManager()));
		setDocumentProvider(new GradleDocumentProvider());
		contentProvider = new GradleEditorOutlineContentProvider(this);
		outlinePage = new GradleEditorContentOutlinePage(this);
		documentListener = new DelayedDocumentListener();

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

	@Override
	public void dispose() {
		super.dispose();
		if (bracketMatcher != null) {
			bracketMatcher.dispose();
			bracketMatcher = null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (GradleEditor.class.equals(adapter)) {
			return (T) this;
		}
		if (IContentOutlinePage.class.equals(adapter)) {
			return (T) outlinePage;
		}
		if (ITreeContentProvider.class.equals(adapter) || GradleEditorOutlineContentProvider.class.equals(adapter)) {
			return (T) contentProvider;
		}
		return super.getAdapter(adapter);
	}

	public Item getItemAtCarretPosition() {
		if (contentProvider == null) {
			return null;
		}
		Item item = contentProvider.tryToFindByOffset(lastCaretPosition);
		return item;
	}

	/**
	 * Jumps to the matching bracket.
	 */
	public void gotoMatchingBracket() {

		ISourceViewer sourceViewer = getSourceViewer();
		IDocument document = sourceViewer.getDocument();
		if (document == null)
			return;

		IRegion selection = getSignedSelection(sourceViewer);
		if (previousSelections == null)
			initializePreviousSelectionList();

		IRegion region = bracketMatcher.match(document, selection.getOffset(), selection.getLength());
		if (region == null) {
			region = bracketMatcher.findEnclosingPeerCharacters(document, selection.getOffset(), selection.getLength());
			initializePreviousSelectionList();
			previousSelections.add(selection);
		} else {
			if (previousSelections.size() == 2) {
				if (!selection.equals(previousSelections.get(1))) {
					initializePreviousSelectionList();
				}
			} else if (previousSelections.size() == 3) {
				if (selection.equals(previousSelections.get(2)) && !selection.equals(previousSelections.get(0))) {
					IRegion originalSelection = previousSelections.get(0);
					sourceViewer.setSelectedRange(originalSelection.getOffset(), originalSelection.getLength());
					sourceViewer.revealRange(originalSelection.getOffset(), originalSelection.getLength());
					initializePreviousSelectionList();
					return;
				}
				initializePreviousSelectionList();
			}
		}

		if (region == null) {
			setStatusLineErrorMessage("Can't go to matching bracket");
			sourceViewer.getTextWidget().getDisplay().beep();
			return;
		}

		int offset = region.getOffset();
		int length = region.getLength();

		if (length < 1)
			return;

		int anchor = bracketMatcher.getAnchor();
		int targetOffset = (ICharacterPairMatcher.RIGHT == anchor) ? offset + 1 : offset + length - 1;

		boolean visible = false;
		if (sourceViewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) sourceViewer;
			visible = (extension.modelOffset2WidgetOffset(targetOffset) > -1);
		} else {
			IRegion visibleRegion = sourceViewer.getVisibleRegion();
			// http://dev.eclipse.org/bugs/show_bug.cgi?id=34195
			visible = (targetOffset >= visibleRegion.getOffset()
					&& targetOffset <= visibleRegion.getOffset() + visibleRegion.getLength());
		}

		if (!visible) {
			setStatusLineErrorMessage("Matching bracket outside selected element");
			sourceViewer.getTextWidget().getDisplay().beep();
			return;
		}

		int adjustment = getOffsetAdjustment(document, selection.getOffset() + selection.getLength(),
				selection.getLength());
		targetOffset += adjustment;
		int direction = (selection.getLength() == 0) ? 0 : ((selection.getLength() > 0) ? 1 : -1);
		if (previousSelections.size() == 1 && direction < 0) {
			targetOffset++;
		}

		if (previousSelections.size() > 0) {
			previousSelections.add(new Region(targetOffset, direction));
		}
		sourceViewer.setSelectedRange(targetOffset, direction);
		sourceViewer.revealRange(targetOffset, direction);
	}

	/**
	 * Opens quick outline
	 */
	public void openQuickOutline() {
		synchronized (monitor) {
			if (quickOutlineOpened) {
				/*
				 * already opened - this is in future the anker point for
				 * ctrl+o+o...
				 */
				return;
			}
			quickOutlineOpened = true;
		}
		Shell shell = getEditorSite().getShell();
		QuickOutlineDialog dialog = new QuickOutlineDialog(this, shell);
		IDocument document = getDocumentProvider().getDocument(getEditorInput());
		dialog.setInput(document);
		dialog.open();
		synchronized (monitor) {
			quickOutlineOpened = false;
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

	@Override
	public void selectAndReveal(int start, int length) {
		super.selectAndReveal(start, length);
		/* TODO ATR: remove the status line information ?!?!? */
		setStatusLineMessage("selected range: start=" + start + ", length=" + length);
	}

	@Override
	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {

		support.setCharacterPairMatcher(bracketMatcher);
		support.setMatchingCharacterPainterPreferenceKeys(MATCHING_BRACKETS, MATCHING_BRACKETS_COLOR,
				HIGHLIGHT_BRACKET_AT_CARET_LOCATION, HIGHLIGHT_ENCLOSING_BRACKETS);

		super.configureSourceViewerDecorationSupport(support);

	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		IDocument document = getDocument();
		document.addDocumentListener(documentListener);
		outlinePage.inputChanged(document);
	}

	protected IDocument getDocument() {
		return getDocumentProvider().getDocument(getEditorInput());
	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId(EDITOR_CONTEXT_MENU_ID);
		setRulerContextMenuId(EDITOR_RULER_CONTEXT_MENU_ID);
	}

	private void activateGradleEditorContext() {
		IContextService contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
		if (contextService != null) {
			contextService.activateContext("org.egradle.editors.GradleEditor.context");
		}
	}

	private ColorManager getColorManager() {
		return Activator.getDefault().getColorManager();
	}

	private void initializePreviousSelectionList() {
		previousSelections = new ArrayList<>(3);
	}

	private class DelayedDocumentListener implements IDocumentListener {

		@Override
		public void documentAboutToBeChanged(DocumentEvent event) {
		}

		@Override
		public void documentChanged(DocumentEvent event) {
			synchronized (monitor) {
				if (dirty) {
					/* already marked as dirty, nothing to do */
					return;
				}
				dirty = true;
			}
			/*
			 * we use a job to refresh the outline delayed and only when still
			 * dirty. This is to avoid too many updates inside the outline -
			 * otherwise every char entered at keyboard will reload complete AST
			 * ...
			 */
			/*
			 * TODO ATR, 12.11.2016: while caret changes the update may not
			 * proceed, only when caret position no longer moves the update of
			 * the document has to be done
			 */
			Job job = new Job("update gradle editor outline") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					synchronized (monitor) {
						if (!dirty) {
							/* already cleaned up by another job */
							return Status.CANCEL_STATUS;
						}
						dirty = false;
					}
					EGradleUtil.safeAsyncExec(new Runnable() {
						public void run() {
							IDocument document = getDocument();
							outlinePage.inputChanged(document);
						}
					});
					return Status.OK_STATUS;
				}
			};
			job.schedule(1500);
		}

	}

	private class GradleEditorCaretListener implements CaretListener {

		@Override
		public void caretMoved(CaretEvent event) {
			if (event == null) {
				return;
			}
			lastCaretPosition = event.caretOffset;
			/* TODO ATR: remove the status line information ?!?!? */
			setStatusLineMessage("caret moved:" + event.caretOffset);
			if (outlinePage == null) {
				return;
			}
			outlinePage.onEditorCaretMoved(event.caretOffset);
		}

	}

}
