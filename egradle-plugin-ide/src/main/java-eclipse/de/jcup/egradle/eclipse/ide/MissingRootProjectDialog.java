/*
 * Copyright 2017 Albert Tregnaghi
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
 package de.jcup.egradle.eclipse.ide;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import de.jcup.egradle.eclipse.util.EclipseUtil;

public class MissingRootProjectDialog extends MessageDialog {

	private static final String DE_JCUP_EGRADLE_ECLIPSE_PLUGIN_MAIN = "/de.jcup.egradle.eclipse.plugin.main/";
	private String detailmessage;

	public MissingRootProjectDialog(Shell parentShell, String detailmessage) {
		super(parentShell, "EGradle root project missing", null, detailmessage, MessageDialog.ERROR, 0);
		this.detailmessage = detailmessage;
	}

	@Override
	protected Control createMessageArea(Composite composite) {
		Image image = getImage();
		if (image != null) {
			imageLabel = new Label(composite, SWT.NULL);
			image.setBackground(imageLabel.getBackground());
			imageLabel.setImage(image);
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(imageLabel);
		}
		if (message != null) {
			Link link = new Link(composite, getMessageLabelStyle());
			StringBuilder sb = new StringBuilder();
			sb.append(detailmessage);
			sb.append("\n\n");
			sb.append("You can do following:\n\n");
			sb.append("- For existing projects execute <a>Change EGradle root project</a>\n");
			sb.append("- When project not in workspace use <a>EGradle import wizard</a>\n");
			sb.append("- Or define root project path in <a>gradle setup preferences</a>\n");
			link.setText(sb.toString());
			link.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if ("Change EGradle root project".equals(e.text)) {
						showInHelp("html/user_guide/setup.html#change_rootproject_by_contextmenu");
					} else if ("EGradle import wizard".equals(e.text)) {
						showInHelp("html/user_guide/import.html#import_by_importer");
					} else {
						IDEUtil.openGradleSetupPage();
					}
					close();
				}

			});
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
					.hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT)
					.applyTo(link);
		}
		return composite;
	}

	private void showInHelp(String target) {
		EclipseUtil.safeAsyncExec(new Runnable() {

			@Override
			public void run() {
				String href = DE_JCUP_EGRADLE_ECLIPSE_PLUGIN_MAIN + target;
				PlatformUI.getWorkbench().getHelpSystem().displayHelpResource(href);

			}
		});
	}
}