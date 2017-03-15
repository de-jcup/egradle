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
package de.jcup.egradle.eclipse.gradleeditor.outline;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.progress.UIJob;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.ItemTextMatcher;
import de.jcup.egradle.eclipse.api.EclipseDevelopmentSettings;
import de.jcup.egradle.eclipse.gradleeditor.Activator;
import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;
import de.jcup.egradle.eclipse.ui.AbstractQuickDialog;

/**
 * This dialog is inspired by: <a href=
 * "https://github.com/eclipse/eclipse.jdt.ui/blob/master/org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/text/AbstractInformationControl.java">org.eclipse.jdt.internal.ui.text.AbstractInformationControl</a>
 * and <a href=
 * "https://github.com/eclipse/eclipse.jdt.ui/blob/master/org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/text/JavaOutlineInformationControl.java">org.eclipse.jdt.internal.ui.text.JavaOutlineInformationControl</a>
 * 
 * @author Albert Tregnaghi
 *
 */
public class QuickOutlineDialog extends AbstractQuickDialog implements IDoubleClickListener {

	private static final int MIN_WIDTH = 400;
	private static final int MIN_HEIGHT = 300;

	private static final String TITLE = "EGradle quick outline";
	private static final boolean DO_SHOW_DIALOG = SHOW_DIALOG_MENU;
	private static final int DEFAULT_X = 600;
	private static final int DEFAULT_Y = 400;

	private GradleEditor gradleEditor;
	private Object input;

	private Object monitor = new Object();

	private Text text;
	private TreeViewer treeViewer;
	private String currentUsedFilterText;
	private ITreeContentProvider contentProvider;
	private ItemTextViewerFilter textFilter;
	private ItemTextMatcher matcher;

	/**
	 * Creates a quick outline dialog
	 * 
	 * @param adaptable
	 *            an adapter which should be able to provide a tree content
	 *            provider and gradle editor. If gradle editor is not set a
	 *            selected item will only close the dialog but do not select
	 *            editor parts..
	 * @param parent
	 *            shell to use is null the outline will have no content! If the
	 *            gradle editor is null location setting etc. will not work.
	 *            <br>
	 *            <br>
	 */
	public QuickOutlineDialog(IAdaptable adaptable, Shell parent) {
		super(parent, PopupDialog.INFOPOPUPRESIZE_SHELLSTYLE, GRAB_FOCUS, PERSIST_SIZE, PERSIST_BOUNDS, DO_SHOW_DIALOG,
				SHOW_PERSIST_ACTIONS, TITLE, null);
		this.gradleEditor = adaptable.getAdapter(GradleEditor.class);
		this.contentProvider = adaptable.getAdapter(ITreeContentProvider.class);
		if (contentProvider == null) {
			contentProvider = new FallbackOutlineContentProvider();
		}
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		ISelection selection = event.getSelection();
		openSelectionAndCloseDialog(selection);
	}

	private void openSelectionAndCloseDialog(ISelection selection) {
		openSelection(selection);
		close();
	}

	private void openSelection(ISelection selection) {
		if (selection == null) {
			return;
		}
		if (gradleEditor == null) {
			if (EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_LOGGING){
				System.out.println("No editor available, would select:" + selection);
			}
			return;
		}
		/*
		 * select part in editor - grab focus not necessary, because this will
		 * close quick outline dialog as well, so editor will get focus back
		 */
		gradleEditor.openSelectedTreeItemInEditor(selection, false);
	}

	/**
	 * Set input to show
	 * 
	 * @param input
	 */
	public void setInput(Object input) {
		this.input = input;
	}

	@Override
	protected void beforeRunEventLoop() {
		treeViewer.setInput(input);

		text.setFocus();

		if (gradleEditor == null) {
			return;
		}
		Item item = gradleEditor.getItemAtCarretPosition();
		if (item == null) {
			return;
		}
		StructuredSelection startSelection = new StructuredSelection(item);
		treeViewer.setSelection(startSelection, true);
	}

	@Override
	protected boolean canHandleShellCloseEvent() {
		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		boolean isWin32 = Util.isWindows();
		GridLayoutFactory.fillDefaults().extendedMargins(isWin32 ? 0 : 3, 3, 2, 2).applyTo(composite);

		GradleEditorOutlineLabelProvider labelProvider = new GradleEditorOutlineLabelProvider();

		int style = SWT.NONE;
		Tree tree = new Tree(composite, SWT.SINGLE | (style & ~SWT.MULTI));
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = tree.getItemHeight() * 12;

		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		tree.setLayoutData(gridData);

		treeViewer = new TreeViewer(tree);
		treeViewer.setContentProvider(contentProvider);

		/* filter */
		textFilter = new ItemTextViewerFilter();
		matcher = new ItemTextMatcher();
		textFilter.setMatcher(matcher);
		treeViewer.setFilters(textFilter);

		tree.setLayoutData(gridData);

		treeViewer.setContentProvider(contentProvider);
		treeViewer.addDoubleClickListener(this);
		treeViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(labelProvider));

		return composite;
	}

	@Override
	protected Control createInfoTextArea(Composite parent) {
		return super.createInfoTextArea(parent);
	}

	@Override
	protected Control createTitleControl(Composite parent) {
		text = new Text(parent, SWT.NONE);

		GridData textLayoutData = new GridData();
		textLayoutData.horizontalAlignment = GridData.FILL;
		textLayoutData.verticalAlignment = GridData.FILL;
		textLayoutData.grabExcessHorizontalSpace = true;
		textLayoutData.grabExcessVerticalSpace = false;
		textLayoutData.horizontalSpan = 2;

		text.setLayoutData(textLayoutData);

		text.addKeyListener(new FilterKeyListener());

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(DO_SHOW_DIALOG ? 1 : 2, 1)
				.applyTo(text);

		return text;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		Activator activator = Activator.getDefault();
		if (activator == null) {
			return null;
		}
		return activator.getDialogSettings();
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		IDialogSettings dialogSettings = getDialogSettings();
		if (dialogSettings == null) {
			/* no dialog settings available, so fall back to min settings */
			return new Point(DEFAULT_X, DEFAULT_Y);
		}
		return super.getInitialLocation(initialSize);
	}

	@Override
	protected Point getInitialSize() {
		IDialogSettings dialogSettings = getDialogSettings();
		if (dialogSettings == null) {
			/* no dialog settings available, so fall back to min settings */
			return new Point(MIN_WIDTH, MIN_HEIGHT);
		}
		Point point = super.getInitialSize();
		if (point.x < MIN_WIDTH) {
			point.x = MIN_WIDTH;
		}
		if (point.y < MIN_HEIGHT) {
			point.y = MIN_HEIGHT;
		}
		return point;
	}

	@Override
	protected boolean hasInfoArea() {
		return super.hasInfoArea();
	}

	protected void rebuildFilterTextPattern() {
		if (text == null) {
			return;
		}
		if (text.isDisposed()) {
			return;
		}
		String filterText = text.getText();
		if (filterText == null) {
			if (currentUsedFilterText == null) {
				/* same as before */
				return;
			}
		} else if (filterText.equals(currentUsedFilterText)) {
			/* same as before */
			return;
		}

		matcher.setFilterText(filterText);

		currentUsedFilterText = filterText;

	}

	private class FilterKeyListener extends KeyAdapter {
		private boolean dirty;

		@Override
		public void keyPressed(KeyEvent event) {
			if (event.keyCode == SWT.ARROW_DOWN) {
				Tree tree = treeViewer.getTree();
				if (tree.isDisposed()) {
					return;
				}
				if (tree.isFocusControl()) {
					return;
				}
				tree.setFocus();
				return;
			}
			if (event.character == '\r') {
				ISelection selection = treeViewer.getSelection();
				openSelectionAndCloseDialog(selection);
				return;
			}
			boolean allowedChar = false;
			allowedChar = allowedChar || event.character == '*';
			allowedChar = allowedChar || event.character == '(';
			allowedChar = allowedChar || event.character == ')';
			allowedChar = allowedChar || Character.isJavaIdentifierPart(event.character);
			allowedChar = allowedChar || Character.isWhitespace(event.character);
			if (!allowedChar) {
				event.doit = false;
				return;
			}
			if (treeViewer == null) {
				return;
			}

		}

		@Override
		public void keyReleased(KeyEvent e) {
			String filterText = text.getText();
			if (filterText != null) {
				if (filterText.equals(currentUsedFilterText)) {
					/*
					 * same text, occurs when only cursor keys used etc. avoid
					 * flickering
					 */
					return;
				}
			}
			synchronized (monitor) {
				if (dirty) {
					return;
				}
				dirty = true;
			}

			UIJob job = new UIJob("Rebuild egradle quick outline") {

				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {
					try {
						rebuildFilterTextPattern();
						if (treeViewer.getControl().isDisposed()) {
							return Status.CANCEL_STATUS;
						}
						treeViewer.refresh();
						if (matcher.hasFilterPattern()) {
							/*
							 * something was entered into filter - so results
							 * must be expanded:
							 */
							treeViewer.expandAll();
							selectFirstMaching();
						}
					} catch (RuntimeException e) {
						/* ignore */
					}
					dirty = false;
					return Status.OK_STATUS;
				}
			};
			job.schedule(400);
		}

		protected void selectFirstMaching() {
			selectfirstMatching(getTreeContentProvider().getElements(null));
		}

		private boolean selectfirstMatching(Object[] elements) {
			if (elements == null) {
				return false;
			}
			for (int i = 0; i < elements.length; i++) {
				Object element = elements[i];
				if (Boolean.TRUE.equals(textFilter.isMatching(element))) {
					StructuredSelection selection = new StructuredSelection(element);
					treeViewer.setSelection(selection, true);
					return true;
				}
				ITreeContentProvider contentProvider = getTreeContentProvider();
				Object[] children = contentProvider.getChildren(element);
				boolean selectionDone = selectfirstMatching(children);
				if (selectionDone) {
					return true;
				}

			}
			return false;
		}

		private ITreeContentProvider getTreeContentProvider() {
			return contentProvider;
		}
	}

}
