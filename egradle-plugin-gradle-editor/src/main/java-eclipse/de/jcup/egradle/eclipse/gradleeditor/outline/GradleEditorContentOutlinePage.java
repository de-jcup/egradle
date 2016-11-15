package de.jcup.egradle.eclipse.gradleeditor.outline;

import static de.jcup.egradle.eclipse.gradleeditor.preferences.EGradleEditorPreferenceConstants.P_LINK_OUTLINE_WITH_EDITOR;
import static de.jcup.egradle.eclipse.gradleeditor.preferences.EGradleEditorPreferences.EDITOR_PREFERENCES;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import de.jcup.egradle.core.outline.OutlineItem;
import de.jcup.egradle.core.token.parser.DebugUtil;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.gradleeditor.Activator;
import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;
import de.jcup.egradle.eclipse.gradleeditor.outline.GradleEditorOutlineContentProvider.ModelType;
public class GradleEditorContentOutlinePage extends ContentOutlinePage implements IDoubleClickListener {

	private GradleEditor gradleEditor;
	private GradleEditorOutlineContentProvider contentProvider;
	private GradleEditorOutlineLabelProvider labelProvider;
	private DelayedDocumentListener documentListener;
	private boolean ignoreNextSelectionEvents;
	private ToggleLinkingAction toggleLinkingAction;

	public GradleEditorContentOutlinePage(GradleEditor gradleEditor) {
		this.gradleEditor = gradleEditor;
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		contentProvider = new GradleEditorOutlineContentProvider(gradleEditor);
		labelProvider = new GradleEditorOutlineLabelProvider();
		documentListener = new DelayedDocumentListener();

		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(contentProvider);
		viewer.addDoubleClickListener(this);
		viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(labelProvider));
		viewer.addSelectionChangedListener(this);

		IDocument document = setTreeViewerDocument();
		document.addDocumentListener(documentListener);

		TokenModelChangeAction tokenModelChangeAction = new TokenModelChangeAction();
		WantedModelChangeAction wantedModelChangeAction = new WantedModelChangeAction();
		GroovyFullAntlrModelChangeAction groovyFullAntlrModelChangeAction = new GroovyFullAntlrModelChangeAction();
		CollapseAllAction collapseAllAction = new CollapseAllAction();
		ExpandAllAction expandAllAction = new ExpandAllAction();
		toggleLinkingAction = new ToggleLinkingAction();
		toggleLinkingAction.setActionDefinitionId(IWorkbenchCommandConstants.NAVIGATE_TOGGLE_LINK_WITH_EDITOR);
		IActionBars actionBars = getSite().getActionBars();

		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		toolBarManager.add(expandAllAction);
		toolBarManager.add(collapseAllAction);
		toolBarManager.add(toggleLinkingAction);

		IMenuManager viewMenuManager = actionBars.getMenuManager();
		viewMenuManager.add(new Separator("EndFilterGroup")); //$NON-NLS-1$
		viewMenuManager.add(groovyFullAntlrModelChangeAction);
		viewMenuManager.add(wantedModelChangeAction);
		viewMenuManager.add(tokenModelChangeAction);
		viewMenuManager.add(new Separator("treeGroup")); //$NON-NLS-1$
		viewMenuManager.add(expandAllAction);
		viewMenuManager.add(collapseAllAction);
		viewMenuManager.add(toggleLinkingAction);
	}

	private IDocument setTreeViewerDocument() {
		DebugUtil.trace("set tree document");
		IDocumentProvider documentProvider = gradleEditor.getDocumentProvider();
		IDocument document = documentProvider.getDocument(gradleEditor.getEditorInput());
		getTreeViewer().setInput(document);
		return document;
	}

	private boolean linkingWithEditorEnabled;

	private abstract class ChangeModelTypeAction extends Action {

		protected abstract ModelType changeTo();

		protected ChangeModelTypeAction() {
			setText("Reload as:" + changeTo());
		}

		@Override
		public void run() {
			contentProvider.setModelType(changeTo());
			getTreeViewer().refresh();
		}
	}

	private class TokenModelChangeAction extends ChangeModelTypeAction {

		@Override
		protected ModelType changeTo() {
			return ModelType.TOKEN;
		}

	}

	private class WantedModelChangeAction extends ChangeModelTypeAction {

		@Override
		protected ModelType changeTo() {
			return ModelType.WANTED;
		}

	}

	private class GroovyFullAntlrModelChangeAction extends ChangeModelTypeAction {

		@Override
		protected ModelType changeTo() {
			return ModelType.GROOVY_FULL_ANTLR;
		}

	}

	private class ExpandAllAction extends Action {

		private ExpandAllAction() {
			setImageDescriptor(EGradleUtil.createImageDescriptor("/icons/outline/expandall.png", Activator.PLUGIN_ID));
			setText("Expand all");
		}

		@Override
		public void run() {
			getTreeViewer().expandAll();
		}
	}

	private class CollapseAllAction extends Action {

		private CollapseAllAction() {
			setImageDescriptor(
					EGradleUtil.createImageDescriptor("/icons/outline/collapseall.png", Activator.PLUGIN_ID));
			setText("Collapse all");
		}

		@Override
		public void run() {
			getTreeViewer().collapseAll();
		}
	}

	private class ToggleLinkingAction extends Action {

		private ToggleLinkingAction() {
			linkingWithEditorEnabled = EDITOR_PREFERENCES.getBooleanPreference(P_LINK_OUTLINE_WITH_EDITOR);
			setDescription("link with editor");
			initImage();
			initText();
		}

		private void initImage() {
			setImageDescriptor(EGradleUtil.createSharedImageDescriptor(
					linkingWithEditorEnabled ? ISharedImages.IMG_ELCL_SYNCED : ISharedImages.IMG_ELCL_SYNCED_DISABLED));

		}

		@Override
		public void run() {
			linkingWithEditorEnabled = !linkingWithEditorEnabled;
			
			/*
			 * TODO ATR, 10.11.2016: what about updating - when now linked the
			 * outline view selection should be updated...
			 */
			initText();
			initImage();
		}

		private void initText() {
			setText(linkingWithEditorEnabled ? "Unlink" : "Link");
		}

	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		super.selectionChanged(event);
		if (!linkingWithEditorEnabled) {
			return;
		}

		if (ignoreNextSelectionEvents) {
			return;
		}
		ISelection selection = event.getSelection();
		openSelectedTreeItemInEditor(selection);
	}

	private void openSelectedTreeItemInEditor(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object firstElement = ss.getFirstElement();
			if (firstElement instanceof OutlineItem) {
				OutlineItem outlineItem = (OutlineItem) firstElement;
				int offset = outlineItem.getOffset();
				int length = outlineItem.getLength();
				gradleEditor.selectAndReveal(offset, length);
			}
		}
	}

	public void onEditorCaretMoved(int caretOffset) {
		if (!linkingWithEditorEnabled) {
			return;
		}
		ignoreNextSelectionEvents = true;
		OutlineItem outlineItem = contentProvider.tryToFindByOffset(caretOffset);
		if (outlineItem != null) {
			getTreeViewer().expandToLevel(outlineItem, AbstractTreeViewer.ALL_LEVELS);
			getTreeViewer().setSelection(new StructuredSelection(outlineItem));
		}
		ignoreNextSelectionEvents = false;
	}

	private Object monitor = new Object();
	private boolean dirty;

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
			/* TODO ATR, 12.11.2016: parser errors should not destroy former model - or at least show "parser errors" or something inside outline */
			/* TODO ATR, 12.11.2016: while caret changes the update may not proceed, only when caret position no longer moves the update of the document has to be done */
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
							GradleEditorContentOutlinePage.this.setTreeViewerDocument();
						}
					});
					return Status.OK_STATUS;
				}
			};
			job.schedule(1000);
		}

	}

	public void ignoreNextSelectionEvents(boolean ignore) {

	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		if (linkingWithEditorEnabled){
			return; // already handled by single click
		}
		openSelectedTreeItemInEditor(event.getSelection());
	}

}
