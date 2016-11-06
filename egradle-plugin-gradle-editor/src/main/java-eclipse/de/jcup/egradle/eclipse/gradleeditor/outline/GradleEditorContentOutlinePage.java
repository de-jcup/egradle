package de.jcup.egradle.eclipse.gradleeditor.outline;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import de.jcup.egradle.core.parser.Token;
import de.jcup.egradle.core.parser.DebugUtil;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;

public class GradleEditorContentOutlinePage extends ContentOutlinePage {

	private GradleEditor gradleEditor;
	private GradleEditorOutlineContentProvider contentProvider;
	private GradleEditorOutlineLabelProvider labelProvider;
	private DelayedDocumentListener documentListener;

	public GradleEditorContentOutlinePage(GradleEditor gradleEditor) {
		this.gradleEditor = gradleEditor;
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		contentProvider = new GradleEditorOutlineContentProvider();
		labelProvider = new GradleEditorOutlineLabelProvider();
		documentListener = new DelayedDocumentListener();

		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(labelProvider);
		viewer.addSelectionChangedListener(this);

		IDocument document = setTreeViewerDocument();
		document.addDocumentListener(documentListener);
	}

	private IDocument setTreeViewerDocument(){
		DebugUtil.trace("set tree document");
		IDocumentProvider documentProvider = gradleEditor.getDocumentProvider();
		IDocument document = documentProvider.getDocument(gradleEditor.getEditorInput());
		getTreeViewer().setInput(document);
		return document;
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		super.selectionChanged(event);
		ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection){
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object firstElement = ss.getFirstElement();
			if (firstElement instanceof Token){
				Token gElement = (Token) firstElement;
				int offset = gElement.getOffset();
				int length = gElement.getLength();
				/* FIXME ATR, 6.11.2016: remove the print when works...*/
				gElement.print();
				gradleEditor.selectAndReveal(offset, length);
			}
		}
	}
	
	private Object monitor = new Object();
	private boolean dirty;
	
	
	private class DelayedDocumentListener implements IDocumentListener{

		@Override
		public void documentAboutToBeChanged(DocumentEvent event) {
		}

		@Override
		public void documentChanged(DocumentEvent event) {
			synchronized (monitor) {
				if (dirty){
					/* already marked as dirty, nothing to do*/
					return;
				}
				dirty=true;
			}
			/* we use a job to refresh the outline delayed and only when still dirty. This is to avoid too many updates
			 * inside the outline - otherwise every char entered at keyboard will reload complete AST ...
			 */
			Job job = new Job("update gradle editor outline") {
				
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					synchronized (monitor) {
						if (!dirty){
							/* already cleaned up by another job*/
							return Status.CANCEL_STATUS;
						}
						dirty=false;
					}
					EGradleUtil.safeAsyncExec(new Runnable(){
						public void run(){
							GradleEditorContentOutlinePage.this.setTreeViewerDocument();
						}
					});
					return Status.OK_STATUS;
				}
			};
			job.schedule(1000);
		}
		
	}
	
}
