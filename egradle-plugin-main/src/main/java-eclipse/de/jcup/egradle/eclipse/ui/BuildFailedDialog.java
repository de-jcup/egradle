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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class BuildFailedDialog extends Dialog {

	private Label bannerImageIcon;

	private String description;

	private Text detailsText;

	private Image backgroundImage;

	private Image titleImage;

	public BuildFailedDialog(Shell parentShell, Image titleImage, Image backgroundImage, String description) {
		super(parentShell);
		this.description = description;
		this.backgroundImage = backgroundImage;
		this.titleImage = titleImage;
		// turn off close icon at window - so user must use OK
		setShellStyle(getShellStyle() & ~SWT.CLOSE);
	}
	

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		createBanner(container);
		createDetails(container);
		return area;
	}

	private void createBanner(Composite container) {
		GridData dataBanner = new GridData();
		dataBanner.grabExcessHorizontalSpace = true;
		dataBanner.horizontalAlignment = GridData.FILL;

		bannerImageIcon = new Label(container, SWT.NONE);
		bannerImageIcon.setImage(backgroundImage);
		bannerImageIcon.setLayoutData(dataBanner);
	}

	private void createDetails(Composite container) {
		if (description == null) {
			return;
		}
		GridData dataLastName = new GridData();
		dataLastName.grabExcessHorizontalSpace = true;
		dataLastName.horizontalAlignment = GridData.FILL;
		detailsText = new Text(container, SWT.NO_BACKGROUND | SWT.READ_ONLY | SWT.MULTI | SWT.NO_FOCUS);
		detailsText.setLayoutData(dataLastName);
		detailsText.setEnabled(false);
		detailsText.setText(description);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("EGradle build failed");
		newShell.setImage(titleImage);
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	protected boolean canHandleShellCloseEvent() {
		return false;
	}

	/**
	 * Just for direct simple UI testing
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Image backgroundImage = new Image(display, "./icons/gradle-build-failed.png");
		Image titleImage = new Image(display, "./icons/gradle-og.png");
		Shell shell = new Shell(display);
		shell.setText("Shell");
		shell.setSize(200, 200);
		shell.open();

		BuildFailedDialog dialog = new BuildFailedDialog(shell, titleImage, backgroundImage,
				"a very long description about a build not workign\n\nTWo lines later");
		dialog.open();

		BuildFailedDialog dialog2 = new BuildFailedDialog(shell, titleImage, backgroundImage, null);
		dialog2.open();
	}

}