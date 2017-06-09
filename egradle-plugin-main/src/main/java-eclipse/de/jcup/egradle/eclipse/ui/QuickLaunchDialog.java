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
 package de.jcup.egradle.eclipse.ui;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.core.util.History;
import de.jcup.egradle.eclipse.MainActivator;

public class QuickLaunchDialog extends AbstractQuickDialog {

	private static final String TITLE = "EGradle quick launch";
	private static final String INFOTEXT = "Enter your gradle tasks (press enter to execute)";
	private History<String> history;

	public QuickLaunchDialog(Shell parent, History<String> history)  {
		super(parent, PopupDialog.INFOPOPUPRESIZE_SHELLSTYLE, GRAB_FOCUS, PERSIST_SIZE, PERSIST_BOUNDS,
				SHOW_DIALOG_MENU, SHOW_PERSIST_ACTIONS, TITLE, INFOTEXT);
		if (history==null){
			history= new History<>(10);
		}
		this.history=history;
	}

	private String inputText;

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		boolean isWin32 = Util.isWindows();
		GridLayoutFactory.fillDefaults().extendedMargins(isWin32 ? 0 : 3, 3, 2, 2).applyTo(composite);

		List<String> list = history.toList();
		String[] items = list.toArray(new String[list.size()]);
		
		Combo comboBox = SWTFactory.createCombo(composite, SWT.NONE, 2, items);
		Font terminalFont = JFaceResources.getTextFont();
		comboBox.setFont(terminalFont);
		
		GridData textLayoutData = new GridData();
		textLayoutData.horizontalAlignment = GridData.FILL;
		textLayoutData.verticalAlignment = GridData.FILL;
		textLayoutData.grabExcessHorizontalSpace = true;
		textLayoutData.grabExcessVerticalSpace = false;
		textLayoutData.horizontalSpan = 2;
		
		comboBox.setLayoutData(textLayoutData);
		
		comboBox.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent event) {
				if (event.character == '\r' ){
					inputText = comboBox.getText();
					if (inputText!=null){
						if (! inputText.equals(history.current())){
							/* when not same as current history entry, add it to history*/
							history.add(inputText);
						}
					}
					close();
				}
			}
		});
		return composite;
	}
	
	@Override
	protected IDialogSettings getDialogSettings() {
		MainActivator mainActivator = MainActivator.getDefault();
		if (mainActivator == null) {
			return null;
		}
		return mainActivator.getDialogSettings();
	}
	
	public String getValue() {
		return inputText;
	}
	
}
