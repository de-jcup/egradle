package de.jcup.egradle.eclipse.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.core.util.History;

public class QuickLaunchDialogTestUI {

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
		History<String> history = new History<>(10);
		history.add("tasks");
		history.add("--version");
		history.add("--stop");
		QuickLaunchDialog dialog = new QuickLaunchDialog(shell, history);
		dialog.open();
		String input = dialog.getValue();
		System.out.println("input was:"+input);
		display.dispose();

	}
}
