/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.egradle.eclipse.ui;

import java.util.ArrayList;
import java.util.List;

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

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.text.CommentBlockToggler;
import de.jcup.egradle.core.text.StatusMessageSupport;
import de.jcup.egradle.core.text.TextLine;
import de.jcup.egradle.core.util.TextUtil;
import de.jcup.egradle.eclipse.util.ColorManager;
import de.jcup.egradle.eclipse.util.ColorUtil;
import de.jcup.egradle.eclipse.util.EclipseDevelopmentSettings;
import de.jcup.egradle.eclipse.util.EclipseUtil;

public abstract class AbstractGroovyBasedEditor extends TextEditor implements StatusMessageSupport, IResourceChangeListener, IExtendedEditor {

    protected AbstractGroovyBasedEditorOutlineContentProvider contentProvider;
    protected DelayedDocumentListener documentListener;
    protected boolean ignoreNextCaretMove;
    protected int lastCaretPosition;
    protected Object monitor = new Object();
    protected AbstractGroovyBasedContentOutlinePage outlinePage;
    protected boolean refreshOutlineInProgress;
    private SourceViewerDecorationSupport additionalSourceViewerSupport;
    private String bgColor;

    private GroovyBracketsSupport bracketMatcher = new GroovyBracketsSupport();
    private String fgColor;

    private boolean quickOutlineOpened;

    public AbstractGroovyBasedEditor() {
        setSourceViewerConfiguration(createSourceViewerConfiguration());

        contentProvider = createOutlineContentProvider();
        outlinePage = createContentOutlinePage();

        documentListener = new DelayedDocumentListener();
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        Control adapter = getAdapter(Control.class);
        if (adapter instanceof StyledText) {
            StyledText text = (StyledText) adapter;
            text.addCaretListener(new EditorCaretListener());
        }

        activateEditorContext();

        contentProvider.clearModelCache();

        installAdditionalSourceViewerSupport();

        StyledText styledText = getSourceViewer().getTextWidget();
        styledText.addKeyListener(new GroovyBracketInsertionCompleter(this, getPreferences()));

        /*
         * register as resource change listener to provide marker change listening
         */
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

        setTitleImageInitial();
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if (IExtendedEditor.class.equals(adapter)) {
            return (T) this;
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
        if (ITreeContentProvider.class.equals(adapter) || AbstractGroovyBasedEditorOutlineContentProvider.class.equals(adapter)) {
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

    public String getBackGroundColorAsWeb() {
        ensureColorsFetched();
        return bgColor;
    }

    public GroovyBracketsSupport getBracketMatcher() {
        return bracketMatcher;
    }

    public IDocument getDocument() {
        return getDocumentProvider().getDocument(getEditorInput());
    }

    public String getForeGroundColorAsWeb() {
        ensureColorsFetched();
        return fgColor;
    }

    public Item getItemAt(int offset) {
        if (contentProvider == null) {
            return null;
        }
        Item item = contentProvider.tryToFindByOffset(offset);
        return item;
    }

    public Item getItemAtCarretPosition() {
        return getItemAt(lastCaretPosition);
    }

    /**
     * Jumps to the matching bracket.
     */
    public void gotoMatchingBracket() {

        bracketMatcher.gotoMatchingBracket(this);
    }

    public void handleColorSettingsChanged() {
        // done like in TextEditor for spelling
        ISourceViewer viewer = getSourceViewer();
        SourceViewerConfiguration configuration = getSourceViewerConfiguration();
        if (viewer instanceof ISourceViewerExtension2) {
            ISourceViewerExtension2 viewerExtension2 = (ISourceViewerExtension2) viewer;
            viewerExtension2.unconfigure();
            if (configuration instanceof ExtendedSourceViewerConfiguration) {
                ExtendedSourceViewerConfiguration gconf = (ExtendedSourceViewerConfiguration) configuration;
                gconf.updateTextScannerDefaultColorToken();
            }
            viewer.configure(configuration);
        }
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

    /**
     * Opens quick outline
     */
    public void openQuickOutline() {
        synchronized (monitor) {
            if (quickOutlineOpened) {
                /*
                 * already opened - this is in future the anker point for ctrl+o+o...
                 */
                return;
            }
            quickOutlineOpened = true;
        }
        Shell shell = getEditorSite().getShell();
        AbstractGroovyBasedQuickOutline dialog = createQuickOutlineDialog(shell);
        IDocument document = getDocumentProvider().getDocument(getEditorInput());
        dialog.setInput(document);
        dialog.open();
        synchronized (monitor) {
            quickOutlineOpened = false;
        }
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
                     * Why not using item.getLength() ? Because would makes full selection. Why not
                     * using item.getName().getLength() ? Because can differ to editor part! so...
                     * get first word at item position
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

    /**
     * Rebuilds outline to current document model
     */
    public void rebuildOutline() {
        refreshOutlineInProgress = true;
        internalRebuildOutline();
    }

    public void resourceChanged(IResourceChangeEvent event) {
        if (isMarkerChangeForThisEditor(event)) {
            int severity = getSeverity();

            setTitleImageDependingOnSeverity(severity);
        }
    }

    @Override
    public void selectAndReveal(int start, int length) {
        super.selectAndReveal(start, length);
        if (EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_TEXTS) {
            setStatusLineMessage("DEBUG:selected range: start=" + start + ", length=" + length);
        }
    }

    public void setErrorMessage(String message) {
        super.setStatusLineErrorMessage(message);
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

        CommentBlockToggler toggler = new CommentBlockToggler();
        List<TextLine> originLines = new ArrayList<>();
        try {
            /* do comment /uncomment */
            for (int i = startLine; i <= endLine; i++) {
                IRegion info;
                info = doc.getLineInformation(i);
                int offset = info.getOffset();

                String line = doc.get(info.getOffset(), info.getLength());
                originLines.add(new TextLine(offset, line));

            }
            List<TextLine> convertedLines = toggler.toggle(originLines);
            for (int i = 0; i < originLines.size(); i++) {
                TextLine origin = originLines.get(i);
                TextLine converted = convertedLines.get(i);
                doc.replace(converted.getOffset(), origin.getLength(), converted.getContent());

            }
        } catch (BadLocationException e) {
            /* ignore and do nothing */
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

    @Override
    protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
        // @formatter:off
		super.configureSourceViewerDecorationSupport(support);
		// @formatter:on
    }

    protected abstract AbstractGroovyBasedContentOutlinePage createContentOutlinePage();

    protected abstract IDocumentProvider resolveFileStoreEditorInputProvider();

    protected abstract AbstractGroovyBasedEditorOutlineContentProvider createOutlineContentProvider();

    protected abstract de.jcup.egradle.eclipse.ui.AbstractGroovyBasedQuickOutline createQuickOutlineDialog(Shell shell);

    protected abstract SourceViewerConfiguration createSourceViewerConfiguration();

    protected abstract IDocumentProvider resolveStandardEditorInputProvider();

    @Override
    protected void doSetInput(IEditorInput input) throws CoreException {
        setDocumentProvider(resolveDocumentProvider(input));
        super.doSetInput(input);
        IDocument document = getDocument();
        if (document == null) {
            getLogSupport().logWarning("No document available for given input:" + input);
            return;
        }
        document.addDocumentListener(documentListener);
        outlinePage.inputChanged(document);
    }

    protected abstract ColorManager getColorManager();

    protected abstract String getEditorInstanceRulerContextId();

    protected abstract String getEditorInstanceContextId();

    protected abstract String getPluginId();

    @Override
    protected void handleEditorInputChanged() {
        super.handleEditorInputChanged();
    }

    @Override
    protected void initializeEditor() {
        super.initializeEditor();
        setEditorContextMenuId(getEditorInstanceContextId());
        setRulerContextMenuId(getEditorInstanceRulerContextId());
    }

    protected void internalRebuildOutline() {
        EclipseUtil.safeAsyncExec(new Runnable() {
            public void run() {
                IDocument document = getDocument();
                outlinePage.inputChanged(document);
                refreshOutlineInProgress = false;
            }

        });
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

    void setTitleImageDependingOnSeverity(int severity) {
        EclipseUtil.getSafeDisplay().asyncExec(() -> {
            if (severity == IMarker.SEVERITY_ERROR) {
                setTitleImage(EclipseUtil.getImage(getEditorIconPathOnError(), getPluginId()));
            } else {
                setTitleImage(EclipseUtil.getImage(getEditorIconPath(), getPluginId()));
            }
        });
    }

    protected abstract String getEditorIconPath();

    protected abstract String getEditorIconPathOnError();

    private void activateEditorContext() {
        IContextService contextService = getSite().getService(IContextService.class);
        if (contextService != null) {
            contextService.activateContext(getEditorInstanceContextId());
        }
    }

    private IDocumentProvider resolveDocumentProvider(IEditorInput input) {
        if (input instanceof FileStoreEditorInput) {
            return resolveFileStoreEditorInputProvider();
        } else {
            return resolveStandardEditorInputProvider();
        }
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
             * TODO ATR, 03.02.2017: there should be an easier approach to get editors back
             * and foreground, without syncexec
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

    /**
     * Installs an additional source viewer support which uses editor preferences
     * instead of standard text preferences. If standard source viewer support would
     * be set with editor preferences all standard preferences would be lost or had
     * to be reimplmented. To avoid this another source viewer support is
     * installed...
     */
    private void installAdditionalSourceViewerSupport() {

        additionalSourceViewerSupport = new SourceViewerDecorationSupport(getSourceViewer(), getOverviewRuler(), getAnnotationAccess(), getSharedColors());
        additionalSourceViewerSupport.setCharacterPairMatcher(bracketMatcher);
        additionalSourceViewerSupport.setMatchingCharacterPainterPreferenceKeys(getPreferences().getP_EDITOR_MATCHING_BRACKETS_ENABLED().getId(),
                getPreferences().getP_EDITOR_MATCHING_BRACKETS_COLOR().getId(), getPreferences().getP_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION().getId(),
                getPreferences().getP_EDITOR_ENCLOSING_BRACKETS().getId());

        IPreferenceStore preferenceStoreForDecorationSupport = getPreferences().getPreferenceStore();
        additionalSourceViewerSupport.install(preferenceStoreForDecorationSupport);
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
     * Set initial title image dependent on current marker severity. This will mark
     * error icon on startup time which is not handled by resource change handling,
     * because having no change...
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

    class DelayedDocumentListener implements IDocumentListener {

        private long lastDocumentChangeTimeStamp;
        private WaitForNoMoreDocumentChangesAndUpdateOutlineRunnable r = new WaitForNoMoreDocumentChangesAndUpdateOutlineRunnable();

        public void dispose() {

            if (r != null) {
                r.dispose();
            }
        }

        @Override
        public void documentAboutToBeChanged(DocumentEvent event) {
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

        void refreshOutline() {
            synchronized (monitor) {
                refreshOutlineInProgress = true;
            }
            internalRebuildOutline();
        }

        private class WaitForNoMoreDocumentChangesAndUpdateOutlineRunnable implements Runnable {
            private boolean disposed;
            private boolean waitingForFurtherDocumentChanges;

            public void dispose() {
                disposed = true;

            }

            public boolean isListeningToFurtherDocumentChanges() {
                synchronized (monitor) {
                    return waitingForFurtherDocumentChanges;
                }
            }

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

        }

    }

    class EditorCaretListener implements CaretListener {

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

}