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

import static de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorPreferenceConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.util.GradleStringTransformer;
import de.jcup.egradle.core.util.SimpleMapStringTransformer;
import de.jcup.egradle.core.util.TextUtil;
import de.jcup.egradle.eclipse.gradleeditor.document.GradleFileDocumentProvider;
import de.jcup.egradle.eclipse.gradleeditor.document.GradleTextFileDocumentProvider;
import de.jcup.egradle.eclipse.gradleeditor.outline.GradleEditorContentOutlinePage;
import de.jcup.egradle.eclipse.gradleeditor.outline.GradleEditorOutlineContentProvider;
import de.jcup.egradle.eclipse.gradleeditor.outline.QuickOutlineDialog;
import de.jcup.egradle.eclipse.openapi.BuildVariablesProvider;
import de.jcup.egradle.eclipse.openapi.BuildVariablesProviderRegistry;
import de.jcup.egradle.eclipse.util.ColorManager;
import de.jcup.egradle.eclipse.util.ColorUtil;
import de.jcup.egradle.eclipse.util.EclipseDevelopmentSettings;
import de.jcup.egradle.eclipse.util.EclipseUtil;

public class GradleEditor extends TextEditor implements StatusMessageSupport, IResourceChangeListener {

	/** The COMMAND_ID of this editor as defined in plugin.xml */
	public static final String EDITOR_ID = "org.egradle.editors.GradleEditor";
	/** The COMMAND_ID of the editor context menu */
	public static final String EDITOR_CONTEXT_MENU_ID = EDITOR_ID + ".context";
	/** The COMMAND_ID of the editor ruler context menu */
	public static final String EDITOR_RULER_CONTEXT_MENU_ID = EDITOR_CONTEXT_MENU_ID + ".ruler";

	private GradleEditorContentOutlinePage outlinePage;

	private DelayedDocumentListener documentListener;

	private GradleEditorOutlineContentProvider contentProvider;

	private Object monitor = new Object();

	private boolean quickOutlineOpened;

	private int lastCaretPosition;

	private boolean refreshOutlineInProgress;

	private GradleBracketsSupport bracketMatcher = new GradleBracketsSupport();

	private boolean ignoreNextCaretMove;
	private GradleFileType cachedGradleFileType;
	private SourceViewerDecorationSupport additionalSourceViewerSupport;

	public GradleEditor() {
		setSourceViewerConfiguration(new GradleSourceViewerConfiguration(this));

		contentProvider = new GradleEditorOutlineContentProvider(this);
		outlinePage = new GradleEditorContentOutlinePage(this);
		documentListener = new DelayedDocumentListener();

	}

	public void resourceChanged(IResourceChangeEvent event) {
		if (isMarkerChangeForThisEditor(event)) {
			int severity = getSeverity();

			setTitleImageDependingOnSeverity(severity);
		}
	}

	void setTitleImageDependingOnSeverity(int severity) {
		if (severity == IMarker.SEVERITY_ERROR) {
			setTitleImage(EclipseUtil.getImage("icons/gradle-editor-with-error.png", EditorActivator.PLUGIN_ID));
		} else {
			setTitleImage(EclipseUtil.getImage("icons/gradle-editor.png", EditorActivator.PLUGIN_ID));
		}
	}

	private int getSeverity() {
		IEditorInput editorInput = getEditorInput();
		if (editorInput == null) {
			return IMarker.SEVERITY_INFO;
		}
		try {
			final IResource resource = ResourceUtil.getResource(editorInput);
			if (resource == null) {
				return IMarker.SEVERITY_INFO;
			}
			int severity = resource.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			return severity;
		} catch (CoreException e) {
			// Might be a project that is not open
		}
		return IMarker.SEVERITY_INFO;
	}

	private boolean isMarkerChangeForThisEditor(IResourceChangeEvent event) {
		IResource resource = ResourceUtil.getResource(getEditorInput());
		if (resource == null) {
			return false;
		}
		IPath path = resource.getFullPath();
		if (path == null) {
			return false;
		}
		IResourceDelta eventDelta = event.getDelta();
		if (eventDelta == null) {
			return false;
		}
		IResourceDelta delta = eventDelta.findMember(path);
		if (delta == null) {
			return false;
		}
		boolean isMarkerChangeForThisResource = (delta.getFlags() & IResourceDelta.MARKERS) != 0;
		return isMarkerChangeForThisResource;
	}

	public void setErrorMessage(String message) {
		super.setStatusLineErrorMessage(message);
	}

	public GradleBracketsSupport getBracketMatcher() {
		return bracketMatcher;
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

		contentProvider.clearModelCache();

		installAdditionalSourceViewerSupport();

		StyledText styledText = getSourceViewer().getTextWidget();
		styledText.addKeyListener(new GradleBracketInsertionCompleter(this));

		/*
		 * register as resource change listener to provide marker change
		 * listening
		 */
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

		setTitleImageInitial();
	}

	/**
	 * Set initial title image dependent on current marker severity. This will
	 * mark error icon on startup time which is not handled by resource change
	 * handling, because having no change...
	 */
	private void setTitleImageInitial() {
		IResource resource = resolveResource();
		if (resource != null) {
			try {
				int maxSeverity = resource.findMaxProblemSeverity(null, true, IResource.DEPTH_INFINITE);
				setTitleImageDependingOnSeverity(maxSeverity);
			} catch (CoreException e) {
				/* ignore */
			}
		}
	}

	/**
	 * Resolves resource from current editor input.
	 * 
	 * @return file resource or <code>null</code>
	 */
	private IResource resolveResource() {
		IEditorInput input = getEditorInput();
		if (!(input instanceof IFileEditorInput)) {
			return null;
		}
		return ((IFileEditorInput) input).getFile();
	}

	/**
	 * Installs an additional source viewer support which uses editor
	 * preferences instead of standard text preferences. If standard source
	 * viewer support would be set with editor preferences all standard
	 * preferences would be lost or had to be reimplmented. To avoid this
	 * another source viewer support is installed...
	 */
	private void installAdditionalSourceViewerSupport() {

		additionalSourceViewerSupport = new SourceViewerDecorationSupport(getSourceViewer(), getOverviewRuler(),
				getAnnotationAccess(), getSharedColors());
		additionalSourceViewerSupport.setCharacterPairMatcher(bracketMatcher);
		additionalSourceViewerSupport.setMatchingCharacterPainterPreferenceKeys(
				P_EDITOR_MATCHING_BRACKETS_ENABLED.getId(), P_EDITOR_MATCHING_BRACKETS_COLOR.getId(),
				P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION.getId(), P_EDITOR_ENCLOSING_BRACKETS.getId());

		IPreferenceStore preferenceStoreForDecorationSupport = EditorUtil.getPreferences().getPreferenceStore();
		additionalSourceViewerSupport.install(preferenceStoreForDecorationSupport);
	}

	@Override
	public void dispose() {
		super.dispose();

		if (additionalSourceViewerSupport != null) {
			additionalSourceViewerSupport.dispose();
		}
		if (bracketMatcher != null) {
			bracketMatcher.dispose();
			bracketMatcher = null;
		}
		if (documentListener != null) {
			documentListener.dispose();
		}

		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	public String getBackGroundColorAsWeb() {
		ensureColorsFetched();
		return bgColor;
	}

	public String getForeGroundColorAsWeb() {
		ensureColorsFetched();
		return fgColor;
	}

	private void ensureColorsFetched() {
		if (bgColor == null || fgColor == null) {

			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer == null) {
				return;
			}
			StyledText textWidget = sourceViewer.getTextWidget();
			if (textWidget == null) {
				return;
			}

			/*
			 * TODO ATR, 03.02.2017: there should be an easier approach to get
			 * editors back and foreground, without syncexec
			 */
			EclipseUtil.getSafeDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					bgColor = ColorUtil.convertToHexColor(textWidget.getBackground());
					fgColor = ColorUtil.convertToHexColor(textWidget.getForeground());
				}
			});
		}

	}

	private String bgColor;
	private String fgColor;

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (GradleEditor.class.equals(adapter)) {
			return (T) this;
		}
		if (GradleFileType.class.equals(adapter)) {
			return (T) getGradleFileType();
		}
		if (GradleStringTransformer.class.equals(adapter)) {
			return (T) getGradleStringTransformer();
		}
		if (ColorManager.class.equals(adapter)) {
			return (T) getColorManager();
		}
		if (IFile.class.equals(adapter)) {
			IEditorInput input = getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFileEditorInput feditorInput = (IFileEditorInput) input;
				return (T) feditorInput.getFile();
			}
			return null;
		}
		if (IContentOutlinePage.class.equals(adapter)) {
			return (T) outlinePage;
		}
		if (ITreeContentProvider.class.equals(adapter) || GradleEditorOutlineContentProvider.class.equals(adapter)) {
			return (T) contentProvider;
		}
		if (Model.class.equals(adapter)) {
			return (T) contentProvider.getModel();
		}
		if (ISourceViewer.class.equals(adapter)) {
			return (T) getSourceViewer();
		}
		if (StatusMessageSupport.class.equals(adapter)) {
			return (T) this;
		}
		return super.getAdapter(adapter);
	}

	private GradleFileType getGradleFileType() {
		if (cachedGradleFileType != null) {
			return cachedGradleFileType;
		}
		IEditorInput editorInput = getEditorInput();
		if (editorInput == null) {
			return null;
		}
		String name = editorInput.getName();
		if (name == null) {
			return null;
		}
		if (!name.endsWith(".gradle")) {
			cachedGradleFileType = GradleFileType.UNKNOWN;
			return cachedGradleFileType;
		}
		/* It is a gradle file... */
		if (name.equals("settings.gradle")) {
			cachedGradleFileType = GradleFileType.GRADLE_SETTINGS_SCRIPT;
		} else if (name.equals("init.gradle")) {
			/*
			 * We do not check if USER_HOME/.gradle/init.d/ or for
			 * GRADLE_HOME/init.d/... The files are inside workspace and so we
			 * only support init.gradle - for 100% correct variant description
			 * see https://docs.gradle.org/current/userguide/init_scripts.html
			 */
			cachedGradleFileType = GradleFileType.GRADLE_INIT_SCRIPT;
		} else {
			/* nothing special - must be init script */
			cachedGradleFileType = GradleFileType.GRADLE_BUILD_SCRIPT;
		}
		return cachedGradleFileType;
	}

	@Override
	protected void handleEditorInputChanged() {
		cachedGradleFileType = null;
		super.handleEditorInputChanged();
	}

	private GradleStringTransformer transformer;

	private GradleStringTransformer getGradleStringTransformer() {
		if (transformer == null) {
			BuildVariablesProvider provider = BuildVariablesProviderRegistry.getProvider();

			Map<String, String> map = null;
			if (provider != null) {
				map = provider.getVariables();
			}
			if (map == null) {
				map = new HashMap<>();
			}
			transformer = new SimpleMapStringTransformer(map);
		}
		return transformer;
	}

	public Item getItemAtCarretPosition() {
		return getItemAt(lastCaretPosition);
	}

	public Item getItemAt(int offset) {
		if (contentProvider == null) {
			return null;
		}
		Item item = contentProvider.tryToFindByOffset(offset);
		return item;
	}

	/**
	 * Jumps to the matching bracket.
	 */
	public void gotoMatchingBracket() {

		bracketMatcher.gotoMatchingBracket(this);
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
		QuickOutlineDialog dialog = new QuickOutlineDialog(this, shell, "Quick outline");
		IDocument document = getDocumentProvider().getDocument(getEditorInput());
		dialog.setInput(document);
		dialog.open();
		synchronized (monitor) {
			quickOutlineOpened = false;
		}
	}

	/**
	 * Get document text - safe way.
	 * 
	 * @return string, never <code>null</code>
	 */
	String getDocumentText() {
		IDocument doc = getDocument();
		if (doc == null) {
			return "";
		}
		return doc.get();
	}

	public void openSelectedTreeItemInEditor(ISelection selection, boolean grabFocus) {
		openSelectedTreeItemInEditor(selection, grabFocus, false);
	}

	public void openSelectedTreeItemInEditor(ISelection selection, boolean grabFocus, boolean fullSelection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object firstElement = ss.getFirstElement();
			if (firstElement instanceof Item) {
				Item item = (Item) firstElement;
				int offset = item.getOffset();
				int length = 0;
				if (fullSelection) {
					length = item.getLength();
				} else {
					/*
					 * Why not using item.getLength() ? Because would makes full
					 * selection. Why not using item.getName().getLength() ?
					 * Because can differ to editor part! so... get first word
					 * at item position
					 */
					length = TextUtil.getLettersOrDigitsAt(offset, getDocumentText()).length();
				}
				if (length == 0) {
					/* absolute fall back variant - but should never happen */
					length = 1;
				}
				ignoreNextCaretMove = true;
				selectAndReveal(offset, length);
				if (grabFocus) {
					setFocus();
				}
			}
		}
	}

	@Override
	public void selectAndReveal(int start, int length) {
		super.selectAndReveal(start, length);
		if (EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_TEXTS) {
			setStatusLineMessage("DEBUG:selected range: start=" + start + ", length=" + length);
		}
	}

	@Override
	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
		// @formatter:off
		super.configureSourceViewerDecorationSupport(support);
		// @formatter:on
	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		setDocumentProvider(createDocumentProvider(input));
		super.doSetInput(input);
		IDocument document = getDocument();
		if (document == null) {
			EditorUtil.logWarning("No document available for given input:" + input);
			return;
		}
		document.addDocumentListener(documentListener);
		outlinePage.inputChanged(document);
	}

	private IDocumentProvider createDocumentProvider(IEditorInput input) {
		if (input instanceof FileStoreEditorInput) {
			return new GradleTextFileDocumentProvider();
		} else {
			return new GradleFileDocumentProvider();
		}
	}

	public IDocument getDocument() {
		return getDocumentProvider().getDocument(getEditorInput());
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		if (site == null) {
			return;
		}
		IWorkbenchPage page = site.getPage();
		if (page == null) {
			return;
		}

		// workaround to show action set for block mode etc.
		// https://www.eclipse.org/forums/index.php/t/366630/
		page.showActionSet("org.eclipse.ui.edit.text.actionSet.presentation");

	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId(EDITOR_CONTEXT_MENU_ID);
		setRulerContextMenuId(EDITOR_RULER_CONTEXT_MENU_ID);
	}

	private void activateGradleEditorContext() {
		IContextService contextService = getSite().getService(IContextService.class);
		if (contextService != null) {
			contextService.activateContext("org.egradle.editors.GradleEditor.context");
		}
	}

	private ColorManager getColorManager() {
		return EditorActivator.getDefault().getColorManager();
	}

	private class DelayedDocumentListener implements IDocumentListener {

		private WaitForNoMoreDocumentChangesAndUpdateOutlineRunnable r = new WaitForNoMoreDocumentChangesAndUpdateOutlineRunnable();
		private long lastDocumentChangeTimeStamp;

		@Override
		public void documentAboutToBeChanged(DocumentEvent event) {
		}

		public void dispose() {

			if (r != null) {
				r.dispose();
			}
		}

		@Override
		public void documentChanged(DocumentEvent event) {
			synchronized (monitor) {
				lastDocumentChangeTimeStamp = System.currentTimeMillis();
				if (refreshOutlineInProgress) {
					/*
					 * already marked as refreshOutlineInProgress, nothing to do
					 */
					return;
				}
			}
			if (!r.isListeningToFurtherDocumentChanges()) {
				Thread t = new Thread(r, "waiting-for-keyboard-events");
				t.start();
			}
		}

		private class WaitForNoMoreDocumentChangesAndUpdateOutlineRunnable implements Runnable {
			private boolean waitingForFurtherDocumentChanges;
			private boolean disposed;

			@Override
			public void run() {
				waitingForFurtherDocumentChanges = true;

				while (!disposed) {
					try {
						Thread.sleep(100);
						long timeBetweenDocumentChanges = System.currentTimeMillis() - lastDocumentChangeTimeStamp;
						if (timeBetweenDocumentChanges > 300) {
							break;
						}

					} catch (InterruptedException e) {
						/* ignore */
					}
				}
				/* no longer waiting - do refresh */

				if (!disposed) {
					refreshOutline();
				}
				waitingForFurtherDocumentChanges = false;
			}

			public void dispose() {
				disposed = true;

			}

			public boolean isListeningToFurtherDocumentChanges() {
				synchronized (monitor) {
					return waitingForFurtherDocumentChanges;
				}
			}

		}

		void refreshOutline() {
			synchronized (monitor) {
				refreshOutlineInProgress = true;
			}
			internalRebuildOutline();
		}

	}

	/**
	 * Rebuilds outline to current document model
	 */
	public void rebuildOutline() {
		refreshOutlineInProgress = true;
		internalRebuildOutline();
	}

	private void internalRebuildOutline() {
		EclipseUtil.safeAsyncExec(new Runnable() {
			public void run() {
				IDocument document = getDocument();
				outlinePage.inputChanged(document);
				refreshOutlineInProgress = false;
			}

		});
	}

	private class GradleEditorCaretListener implements CaretListener {

		@Override
		public void caretMoved(CaretEvent event) {
			if (event == null) {
				return;
			}
			lastCaretPosition = event.caretOffset;
			if (EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_TEXTS) {
				setStatusLineMessage("caret moved:" + event.caretOffset);
			}
			if (ignoreNextCaretMove) {
				ignoreNextCaretMove = false;
				return;
			}
			if (outlinePage == null) {
				return;
			}
			outlinePage.onEditorCaretMoved(event.caretOffset);
		}

	}

	public void handleColorSettingsChanged() {
		// done like in TextEditor for spelling
		ISourceViewer viewer = getSourceViewer();
		SourceViewerConfiguration configuration = getSourceViewerConfiguration();
		if (viewer instanceof ISourceViewerExtension2) {
			ISourceViewerExtension2 viewerExtension2 = (ISourceViewerExtension2) viewer;
			viewerExtension2.unconfigure();
			if (configuration instanceof GradleSourceViewerConfiguration) {
				GradleSourceViewerConfiguration gconf = (GradleSourceViewerConfiguration) configuration;
				gconf.updateTextScannerDefaultColorToken();
			}
			viewer.configure(configuration);
		}
	}

	/**
	 * Toggles comment of current selected lines
	 */
	public void toggleComment() {
		ISelection selection = getSelectionProvider().getSelection();
		if (!(selection instanceof TextSelection)) {
			return;
		}
		IDocumentProvider dp = getDocumentProvider();
		IDocument doc = dp.getDocument(getEditorInput());
		TextSelection ts = (TextSelection) selection;
		int startLine = ts.getStartLine();
		int endLine = ts.getEndLine();

		/* do comment /uncomment */
		for (int i = startLine; i <= endLine; i++) {
			IRegion info;
			try {
				info = doc.getLineInformation(i);
				int offset = info.getOffset();
				String line = doc.get(info.getOffset(), info.getLength());
				StringBuilder foundCode = new StringBuilder();
				StringBuilder whitespaces = new StringBuilder();
				for (int j = 0; j < line.length(); j++) {
					char ch = line.charAt(j);
					if (Character.isWhitespace(ch)) {
						if (foundCode.length() == 0) {
							whitespaces.append(ch);
						}
					} else {
						foundCode.append(ch);
					}
					if (foundCode.length() > 1) {
						break;
					}
				}
				int whitespaceOffsetAdd = whitespaces.length();
				if ("//".equals(foundCode.toString())) {
					/* comment before */
					doc.replace(offset + whitespaceOffsetAdd, 2, "");
				} else {
					/* not commented */
					doc.replace(offset, 0, "//");
				}

			} catch (BadLocationException e) {
				/* ignore and continue */
				continue;
			}

		}
		/* reselect */
		int selectionStartOffset;
		try {
			selectionStartOffset = doc.getLineOffset(startLine);
			int endlineOffset = doc.getLineOffset(endLine);
			int endlineLength = doc.getLineLength(endLine);
			int endlineLastPartOffset = endlineOffset + endlineLength;
			int length = endlineLastPartOffset - selectionStartOffset;

			ISelection newSelection = new TextSelection(selectionStartOffset, length);
			getSelectionProvider().setSelection(newSelection);
		} catch (BadLocationException e) {
			/* ignore */
		}
	}

}
