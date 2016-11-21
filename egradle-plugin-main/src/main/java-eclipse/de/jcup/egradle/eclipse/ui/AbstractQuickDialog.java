package de.jcup.egradle.eclipse.ui;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.eclipse.api.EGradleUtil;

/**
 * Abstract implementation for quick dialogs. Clicking out of quick dialog will close the dialog
 * @author Albert Tregnaghi
 *
 */
public abstract class AbstractQuickDialog extends PopupDialog {

	protected static final boolean GRAB_FOCUS = true;
	protected static final boolean PERSIST_NO_SIZE = false;
	protected static final boolean PERSIST_SIZE = true;
	protected static final boolean PERSIST_NO_BOUNDS = false;
	protected static final boolean SHOW_NO_DIALOG_MENU = false;
	protected static final boolean SHOW_NO_PERSIST_ACTIONS = false;

	public AbstractQuickDialog(Shell parent, int shellStyle, boolean takeFocusOnOpen, boolean persistSize,
			boolean persistLocation, boolean showDialogMenu, boolean showPersistActions, String titleText,
			String infoText) {
		super(parent, shellStyle, takeFocusOnOpen, persistSize, persistLocation, showDialogMenu, showPersistActions,
				titleText, infoText);
	}

	@Override
	 public final int open() {
		int value = super.open();
		beforeRunEventLoop();
		runEventLoop(getShell());
		return value;
	}

	protected void beforeRunEventLoop() {
		
	}

	private void runEventLoop(Shell loopShell) {
		Display display;
		if (getShell() == null) {
			display = Display.getCurrent();
		} else {
			display = loopShell.getDisplay();
		}
	
		while (loopShell != null && !loopShell.isDisposed()) {
			try {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			} catch (Throwable e) {
				EGradleUtil.log(e);
			}
		}
		if (!display.isDisposed()) {
			display.update();
		}
	}

	@Override
	protected boolean canHandleShellCloseEvent() {
		return true;
	}

}