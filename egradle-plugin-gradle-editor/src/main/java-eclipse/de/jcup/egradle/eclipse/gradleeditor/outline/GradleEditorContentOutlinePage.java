package de.jcup.egradle.eclipse.gradleeditor.outline;

import static de.jcup.egradle.eclipse.gradleeditor.preferences.EGradleEditorPreferenceConstants.*;
import static de.jcup.egradle.eclipse.gradleeditor.preferences.EGradleEditorPreferences.*;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
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

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.eclipse.api.EclipseDevelopmentSettings;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.gradleeditor.Activator;
import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;
import de.jcup.egradle.eclipse.gradleeditor.outline.GradleEditorOutlineContentProvider.ModelType;
public class GradleEditorContentOutlinePage extends ContentOutlinePage implements IDoubleClickListener {
	
	private static ImageDescriptor IMG_DESC_LINKED = EGradleUtil.createImageDescriptor("/icons/outline/synced.png",Activator.PLUGIN_ID);
	private static ImageDescriptor IMG_DESC_NOT_LINKED = EGradleUtil.createImageDescriptor("/icons/outline/sync_broken.png",Activator.PLUGIN_ID);
	
	private GradleEditorOutlineContentProvider contentProvider;
	private boolean dirty;
	private DelayedDocumentListener documentListener;
	private GradleEditor gradleEditor;
	private boolean ignoreNextSelectionEvents;
	private GradleEditorOutlineLabelProvider labelProvider;
	private boolean linkingWithEditorEnabled;
	private Object monitor = new Object();
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

		ShowGradleOutlineModelAction showGradleOutlineModelAction = new ShowGradleOutlineModelAction();
		ShowGroovyFullAntlrModelAction showGroovyFullAntlrModelAction = new ShowGroovyFullAntlrModelAction();
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
		if (EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_MENUS){
			viewMenuManager.add(showGroovyFullAntlrModelAction);
			viewMenuManager.add(showGradleOutlineModelAction);
		}
		viewMenuManager.add(new Separator("treeGroup")); //$NON-NLS-1$
		viewMenuManager.add(expandAllAction);
		viewMenuManager.add(collapseAllAction);
		viewMenuManager.add(toggleLinkingAction);
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		if (linkingWithEditorEnabled){
			return; // already handled by single click
		}
		openSelectedTreeItemInEditor(event.getSelection());
	}

	public void ignoreNextSelectionEvents(boolean ignore) {
	
	}

	public void onEditorCaretMoved(int caretOffset) {
		if (!linkingWithEditorEnabled) {
			return;
		}
		ignoreNextSelectionEvents = true;
		Item item = contentProvider.tryToFindByOffset(caretOffset);
		if (item != null) {
			getTreeViewer().expandToLevel(item, AbstractTreeViewer.ALL_LEVELS);
			getTreeViewer().setSelection(new StructuredSelection(item));
		}
		ignoreNextSelectionEvents = false;
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
			if (firstElement instanceof Item) {
				Item item = (Item) firstElement;
				int offset = item.getOffset();
				int length = item.getLength();
				gradleEditor.selectAndReveal(offset, length);
			}
		}
	}

	private IDocument setTreeViewerDocument() {
		IDocumentProvider documentProvider = gradleEditor.getDocumentProvider();
		IDocument document = documentProvider.getDocument(gradleEditor.getEditorInput());
		getTreeViewer().setInput(document);
		return document;
	}

	private abstract class ChangeModelTypeAction extends Action {

		protected ChangeModelTypeAction() {
			setText("Reload as:" + changeTo());
		}

		@Override
		public void run() {
			contentProvider.setModelType(changeTo());
			getTreeViewer().refresh();
		}

		protected abstract ModelType changeTo();
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
			job.schedule(1500);
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

	private class ShowGroovyFullAntlrModelAction extends ChangeModelTypeAction {

		@Override
		protected ModelType changeTo() {
			return ModelType.GROOVY_FULL_ANTLR;
		}

	}
	

	private class ToggleLinkingAction extends Action {
		

		private ToggleLinkingAction() {
			linkingWithEditorEnabled = EDITOR_PREFERENCES.getBooleanPreference(P_LINK_OUTLINE_WITH_EDITOR);
			setDescription("link with editor");
			initImage();
			initText();
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

		private void initImage() {
			setImageDescriptor(linkingWithEditorEnabled ? IMG_DESC_LINKED : IMG_DESC_NOT_LINKED);
		}

		private void initText() {
			setText(linkingWithEditorEnabled ? "Click to unlink from editor" : "Click to link with editor");
		}

	}

	private class ShowGradleOutlineModelAction extends ChangeModelTypeAction {

		@Override
		protected ModelType changeTo() {
			return ModelType.GRADLE;
		}

	}

}
