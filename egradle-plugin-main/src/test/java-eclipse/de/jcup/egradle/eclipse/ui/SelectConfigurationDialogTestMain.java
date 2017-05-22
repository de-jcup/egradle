/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
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
		
		SelectConfigurationDialog selectConfigurationDialog = new SelectConfigurationDialog(s);
		String configuration = selectConfigurationDialog.open();
		System.out.println("configuration selected:"+configuration);
		d.dispose();
	}

	public static void main(String[] argv) {
		new SelectConfigurationDialogTestMain();
	}
}
