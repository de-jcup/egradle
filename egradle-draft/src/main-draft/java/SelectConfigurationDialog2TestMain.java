package de.jcup.egradle.eclipse.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SelectConfigurationDialog2TestMain {

	Display d;

	Shell s;

	SelectConfigurationDialog2TestMain() {
		d = new Display();
		s = new Shell(d);
		s.setSize(250, 250);
		s.setText("A select configuration dialog test");
		
		SelectConfigurationDialog2 selectConfigurationDialog = new SelectConfigurationDialog2(s,"Select the test configuration");
		List<String> configurations = new ArrayList<>();
		configurations.add("compile");
		configurations.add("testCompile");
		configurations.add("runtime");
		configurations.add("testRuntime");
		
		selectConfigurationDialog.setInput(configurations);
		selectConfigurationDialog.open();
		String configuration = selectConfigurationDialog.getConfigurationResult(); 
		System.out.println("configuration selected:"+configuration);
		d.dispose();
	}

	
	
	public static void main(String[] argv) {
		new SelectConfigurationDialog2TestMain();
	}
}
