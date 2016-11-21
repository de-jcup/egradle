package de.jcup.egradle.eclipse.gradleeditor.outline;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;
import de.jcup.egradle.eclipse.ui.AbstractQuickDialog;

public class QuickOutlineDialog extends AbstractQuickDialog implements IDoubleClickListener {

	private static final String TITLE = "EGradle quick outline";
	private static final String INFOTEXT = null;// "Enter your gradle tasks
												// (press enter to execute)";
	private IDialogSettings dialogSettings;

	/**
	 * inspired by: org.eclipse.jdt.internal.ui.text.AbstractInformationControl
	 * (https://github.com/eclipse/eclipse.jdt.ui/blob/master/org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/text/AbstractInformationControl.java)
	 * and org.eclipse.jdt.internal.ui.text.JavaOutlineInformationControl
	 * (https://github.com/eclipse/eclipse.jdt.ui/blob/master/org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/text/JavaOutlineInformationControl.java)
	 * @param editor 
	 * @param parent 
	 */
	public QuickOutlineDialog(GradleEditor editor, Shell parent) {
		super(parent, PopupDialog.INFOPOPUPRESIZE_SHELLSTYLE, GRAB_FOCUS, PERSIST_NO_SIZE, PERSIST_NO_BOUNDS,
				SHOW_NO_DIALOG_MENU, SHOW_NO_PERSIST_ACTIONS, TITLE, INFOTEXT);
		this.gradleEditor = editor;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		// if (dialogSettings==null){
		// dialogSettings= new DialogSettings("egradle.quickoutline");
		// dialogSettings.put(IDialogSettings.I, value);
		// }
		return dialogSettings;
	}

	public String getValue() {
		return inputText;
	}

	@Override
	protected Point getInitialSize() {
		if (true){
			return new Point (800,800);
		}
		return super.getInitialSize();
	}

	private String inputText;
	private GradleEditor gradleEditor;
	private TreeViewer treeViewer;
	private Object input;

	@Override
	protected Control createTitleControl(Composite parent) {
		Text text = new Text(parent, SWT.NONE);
		Font terminalFont = JFaceResources.getTextFont();
		text.setFont(terminalFont);

		GridData textLayoutData = new GridData();
		textLayoutData.horizontalAlignment = GridData.FILL;
		textLayoutData.verticalAlignment = GridData.FILL;
		textLayoutData.grabExcessHorizontalSpace = true;
		textLayoutData.grabExcessVerticalSpace = false;
		textLayoutData.horizontalSpan = 2;

		text.setLayoutData(textLayoutData);

		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent event) {
				if (event.character == '\r') {
					inputText = text.getText();
					close();
				}
			}
		});
//		return super.createTitleControl(parent);
		return text;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		boolean isWin32 = Util.isWindows();
		GridLayoutFactory.fillDefaults().extendedMargins(isWin32 ? 0 : 3, 3, 2, 2).applyTo(composite);

		GradleEditorOutlineLabelProvider labelProvider = new GradleEditorOutlineLabelProvider();
//		parent.setBackground(labelProvider.getBackground(ColorConstants.RED));

		int style = SWT.NONE;
		Tree tree = new Tree(composite, SWT.SINGLE | (style & ~SWT.MULTI));
		GridData gd = new GridData(GridData.FILL_BOTH);
//		gd.heightHint = tree.getItemHeight() * 12;
		gd.verticalAlignment=SWT.TOP;
		gd.grabExcessVerticalSpace=true;
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		tree.setLayoutData(gd);

		treeViewer = new TreeViewer(tree);
		/* TODO ATR, 22.11.2016 add filters */
		tree.setLayoutData(gd);

		IContentProvider contentProvider = new GradleEditorOutlineContentProvider(gradleEditor);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.addDoubleClickListener(this);
		treeViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(labelProvider));

		return composite;
	}
	
	@Override
	protected Control createInfoTextArea(Composite parent) {
		// TODO Auto-generated method stub
		return super.createInfoTextArea(parent);
	}

	@Override
	protected boolean hasInfoArea() {
		// TODO Auto-generated method stub
		return super.hasInfoArea();
	}
	
	@Override
	protected boolean canHandleShellCloseEvent() {
		return true;
	}

	@Override
	protected void beforeRunEventLoop() {
		treeViewer.setInput(input);
	}

	public void setInput(Object input) {
		this.input = input;
	}

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

	@Override
	public void doubleClick(DoubleClickEvent event) {
		if (gradleEditor != null) {
			gradleEditor.openSelectedTreeItemInEditor(event.getSelection());
		} else {
			System.out.println("selected:" + event.getSelection());
		}
		close();
	}

}
