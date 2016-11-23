package de.jcup.egradle.eclipse.gradleeditor.outline;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.progress.UIJob;

import de.jcup.egradle.core.api.Matcher;
import de.jcup.egradle.eclipse.gradleeditor.Activator;
import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;
import de.jcup.egradle.eclipse.ui.AbstractQuickDialog;

public class QuickOutlineDialog extends AbstractQuickDialog implements IDoubleClickListener {

	private static final String INFOTEXT = null;// "Enter your gradle tasks
	private static final String TITLE = "EGradle quick outline";
	private static final boolean DO_SHOW_DIALOG = SHOW_DIALOG_MENU;
	

	/**
	 * Just for direct simple UI testing
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Shell");
		shell.setSize(200, 200);
		shell.open();

		QuickOutlineDialog dialog = new QuickOutlineDialog(null, shell);
		dialog.setInput("dependencies{\n" + "testCompile library.junit\n" + "testCompile library.mockito_all\n" + "}");
		dialog.open();
		String input = dialog.getValue();
		System.out.println("input was:" + input);
		display.dispose();

	}

	// (press enter to execute)";
	private IDialogSettings dialogSettings;

	private Pattern filterPattern;

	private GradleEditor gradleEditor;

	private Object input;

	private String inputText;

	private Object monitor = new Object();
	private Pattern PATTERN_DEREGEX_ASTERISK = Pattern.compile("\\*");
	private Pattern PATTERN_DEREGEX_DOT = Pattern.compile("\\.");
	private Text text;
	private TreeViewer treeViewer;
	private String currentUsedFilterText;

	/**
	 * inspired by: org.eclipse.jdt.internal.ui.text.AbstractInformationControl
	 * (https://github.com/eclipse/eclipse.jdt.ui/blob/master/org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/text/AbstractInformationControl.java)
	 * and org.eclipse.jdt.internal.ui.text.JavaOutlineInformationControl
	 * (https://github.com/eclipse/eclipse.jdt.ui/blob/master/org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/text/JavaOutlineInformationControl.java)
	 * 
	 * @param editor
	 * @param parent
	 */
	public QuickOutlineDialog(GradleEditor editor, Shell parent) {
		super(parent, PopupDialog.INFOPOPUPRESIZE_SHELLSTYLE, GRAB_FOCUS, PERSIST_SIZE, PERSIST_BOUNDS,
				DO_SHOW_DIALOG, SHOW_PERSIST_ACTIONS, TITLE, INFOTEXT);
		this.gradleEditor = editor;
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		if (gradleEditor != null) {
			gradleEditor.openSelectedTreeItemInEditor(event.getSelection());
		} else {
			System.out.println("selected:" + event.getSelection());
		}
		close();
	}

	public String getValue() {
		return inputText;
	}

	public void setInput(Object input) {
		this.input = input;
	}

	@Override
	protected void beforeRunEventLoop() {
		treeViewer.setInput(input);
		text.setFocus();
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

		/* filter */
		ItemTextViewerFilter textFilter = new ItemTextViewerFilter();
		textFilter.setMatcher(new OutlineTextMatcher());
		treeViewer.setFilters(textFilter);

		tree.setLayoutData(gridData);

		IContentProvider contentProvider = new GradleEditorOutlineContentProvider(gradleEditor);
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
		
		
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true,
				false).span(DO_SHOW_DIALOG ? 1 : 2, 1).applyTo(text);

		
		return text;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		Activator activator = Activator.getDefault();
		if (activator!=null){
			return activator.getDialogSettings();
		}
		return dialogSettings;
	}

	@Override
	protected Point getInitialSize() {
		Activator activator = Activator.getDefault();
		if (activator==null){
			return new Point(600,600);
		}
		return super.getInitialSize();
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
			if (event.character == '\r') {
				inputText = text.getText();
				close();
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
					/* same text, occurs when only cursor keys used etc. avoid flickering*/
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
							/* something was entered into filter - so results must be expanded:*/	
							treeViewer.expandAll();
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
