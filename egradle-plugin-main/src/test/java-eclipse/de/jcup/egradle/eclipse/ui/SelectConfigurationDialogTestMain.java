package de.jcup.egradle.eclipse.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SelectConfigurationDialogTestMain {

	Display d;

	Shell s;

	SelectConfigurationDialogTestMain() {
		d = new Display();
		s = new Shell(d);
		s.setSize(250, 250);
		s.setText("A select configuration dialog test");
		
		String configuration = new SelectConfigurationDialog(s).open();
		System.out.println("configuration selected:"+configuration);
		d.dispose();
	}

	public static void main(String[] argv) {
		new SelectConfigurationDialogTestMain();
	}
}
