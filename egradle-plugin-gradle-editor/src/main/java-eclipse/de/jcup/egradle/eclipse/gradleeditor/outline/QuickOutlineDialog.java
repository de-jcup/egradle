package de.jcup.egradle.eclipse.gradleeditor.outline;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

import de.jcup.egradle.core.api.Matcher;
import de.jcup.egradle.core.model.Item;
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

	private Pattern filterPattern;

	private GradleEditor gradleEditor;
	private Object input;

	private Object monitor = new Object();
	private Pattern PATTERN_DEREGEX_ASTERISK = Pattern.compile("\\*");
	private Pattern PATTERN_DEREGEX_DOT = Pattern.compile("\\.");
	private Text text;
	private TreeViewer treeViewer;
	private String currentUsedFilterText;
	private ITreeContentProvider contentProvider;
	private ItemTextViewerFilter textFilter;

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
	 * 			<br>
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
			System.out.println("selected:" + selection);
			return;
		}
		gradleEditor.openSelectedTreeItemInEditor(selection);
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
		textFilter.setMatcher(new OutlineTextMatcher());
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
		filterPattern = null;
		if (text == null) {
			return;
		}
		if (text.isDisposed()) {
			return;
		}
		String filterText = text.getText();
		if (filterText == null) {
			return;
		}
		filterText = filterText.trim();
		if (filterText.length() == 0) {
			return;
		}
		/*
		 * make user entry not being a regular expression but simple wild card
		 * handled
		 */
		// change "bla*" to "bla.*"
		// change "bla." to "bla\."
		String newPattern = filterText;
		if (!newPattern.endsWith("*")) {
			newPattern += "*";
		}
		newPattern = PATTERN_DEREGEX_ASTERISK.matcher(newPattern).replaceAll(".*");
		newPattern = PATTERN_DEREGEX_DOT.matcher(newPattern).replaceAll("\\.");

		try {
			filterPattern = Pattern.compile(newPattern, Pattern.CASE_INSENSITIVE);
			currentUsedFilterText = filterText;
		} catch (PatternSyntaxException e) {
			/* ignore - filterPattern is now null, fall back is used */
		}

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
						if (filterPattern != null) {
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
			// /* select the first part where the matcher matches - so return
			// will use this*/
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
					System.out.println("selection done:" + element);
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

	private class OutlineTextMatcher implements Matcher<String> {

		@Override
		public boolean matches(String itemText) {
			if (itemText == null) {
				return false;
			}

			if (filterPattern == null) {
				/* simple fall back ... */
				String filterText = text.getText();
				if (filterText == null) {
					return true;
				}
				filterText = filterText.trim();
				if (filterText.length() == 0) {
					return true;
				}

				if (itemText.indexOf(filterText) != -1) {
					return true;
				} else {
					return false;
				}
			}

			/* filter pattern set */
			boolean filterPatternMatches = filterPattern.matcher(itemText).matches();
			return filterPatternMatches;
		}
	}

}
