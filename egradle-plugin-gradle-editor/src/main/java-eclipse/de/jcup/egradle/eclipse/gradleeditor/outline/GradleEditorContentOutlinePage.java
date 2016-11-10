package de.jcup.egradle.eclipse.gradleeditor.outline;

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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.token.parser.DebugUtil;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;

public class GradleEditorContentOutlinePage extends ContentOutlinePage {

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
		viewer.setLabelProvider(labelProvider);
		viewer.addSelectionChangedListener(this);

		IDocument document = setTreeViewerDocument();
		document.addDocumentListener(documentListener);
		
		
		IActionBars actionBars= getSite().getActionBars();
		registerActionBars(actionBars);
		
//		IToolBarManager toolBarManager= actionBars.getToolBarManager();

		IMenuManager viewMenuManager= actionBars.getMenuManager();
		viewMenuManager.add(new Separator("EndFilterGroup")); //$NON-NLS-1$

		toggleLinkingAction= new ToggleLinkingAction();
		toggleLinkingAction.setActionDefinitionId(IWorkbenchCommandConstants.NAVIGATE_TOGGLE_LINK_WITH_EDITOR);
		viewMenuManager.add(toggleLinkingAction);

	}

	private void registerActionBars(IActionBars actionBars) {
		// TODO Auto-generated method stub
		
	}

	private IDocument setTreeViewerDocument() {
		DebugUtil.trace("set tree document");
		IDocumentProvider documentProvider = gradleEditor.getDocumentProvider();
		IDocument document = documentProvider.getDocument(gradleEditor.getEditorInput());
		getTreeViewer().setInput(document);
		return document;
	}
	private boolean linkingWithEditorEnabled;

	public class ToggleLinkingAction extends Action {

		
		public ToggleLinkingAction() {
			linkingWithEditorEnabled = false;// FIXME ATR, 10.11.2016: use preference...
			setDescription("link with editor");
			initImage();
			initText();
		}

		private void initImage() {
			setImageDescriptor(EGradleUtil.createSharedImageDescriptor(linkingWithEditorEnabled ? ISharedImages.IMG_ELCL_SYNCED : ISharedImages.IMG_ELCL_SYNCED_DISABLED));
			
		}

		@Override
		public void run() {
			linkingWithEditorEnabled=!linkingWithEditorEnabled;
			// FIXME ATR, 10.11.2016: change preference...
			initText();
			initImage();
		}
		
		private void initText(){
			setText(linkingWithEditorEnabled ? "Unlink" : "Link");
		}

	}

	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		super.selectionChanged(event);
		if (!linkingWithEditorEnabled){
			return;
		}
		
		if (ignoreNextSelectionEvents){
			return;
		}
		ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object firstElement = ss.getFirstElement();
			if (firstElement instanceof Item) {
				Item item = (Item) firstElement;
				int offset = item.getOffset();
				int length = item.getLength();
				// /* FIXME ATR, 6.11.2016: remove the print when works...*/
				// gElement.print();
				gradleEditor.selectAndReveal(offset, length);
			}
		}
	}

	public void onEditorCaretMoved(int caretOffset) {
		if (! linkingWithEditorEnabled){
			return;
		}
		Item item = contentProvider.tryToFindByOffset(caretOffset);
		if (item != null) {
			ignoreNextSelectionEvents=true;
			getTreeViewer().expandToLevel(item, AbstractTreeViewer.ALL_LEVELS);
			getTreeViewer().setSelection(new StructuredSelection(item));
			ignoreNextSelectionEvents=false;
		}
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

}
