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
//Send questions, comments, bug reports, etc. to the authors:

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.Geometry;
//Rob Warner (rwarner@interspatial.com)
//Robert Harris (rbrt_harris@yahoo.com)
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.eclipse.MainActivator;

/**
 * This class demonstrates how to create your own dialog classes. It allows
 * users to input a String
 */
public class SelectConfigurationDialog extends Dialog {
	private String message;
	private String input;
	private Image titleImage;
	private Shell shell;
	private boolean okPressed;
	/**
	 * The dialog settings key name for stored dialog x location.
	 */
	private static final String DIALOG_ORIGIN_X = "DIALOG_X_ORIGIN"; //$NON-NLS-1$

	/**
	 * The dialog settings key name for stored dialog y location.
	 */
	private static final String DIALOG_ORIGIN_Y = "DIALOG_Y_ORIGIN"; //$NON-NLS-1$

	/**
	 * InputDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 */
	public SelectConfigurationDialog(Shell parent) {
		// Pass the default styles here
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
	}

	/**
	 * InputDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 */
	public SelectConfigurationDialog(Shell parent, int style) {
		// Let users override the default styles
		super(parent, style);
		setText("Configuration selection");
		setMessage("Please select configuration or leave empty for all:");
	}

	/**
	 * Gets the message
	 * 
	 * @return String
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message
	 * 
	 * @param message
	 *            the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the input
	 * 
	 * @return String
	 */
	public String getInput() {
		return input;
	}

	/**
	 * Sets the input
	 * 
	 * @param input
	 *            the new input
	 */
	public void setInput(String input) {
		this.input = input;
	}

	public void setTitleImage(Image titleImage) {
		this.titleImage = titleImage;
	}

	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public String open() {
		// Create the dialog window
		shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		if (titleImage != null) {
			shell.setImage(titleImage);
		}

		createContents(shell);
		shell.pack();

		Display display = shell.getDisplay();

		shell.setLocation(getInitialLocation(shell.getSize()));

		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				persistLocation();
			}

		});

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// Return the entered value, or null
		if (! okPressed){
			return null;
		}
		
		return input;
	}
	
	private void persistLocation() {
		if (shell==null || shell.isDisposed()){
			return;
		}
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			Point shellLocation = shell.getLocation();
			Shell parent = getParent();
			if (parent != null) {
				Point parentLocation = parent.getLocation();
				shellLocation.x -= parentLocation.x;
				shellLocation.y -= parentLocation.y;
			}
			String prefix = getClass().getName();
			settings.put(prefix + DIALOG_ORIGIN_X, shellLocation.x);
			settings.put(prefix + DIALOG_ORIGIN_Y, shellLocation.y);
		}
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell
	 *            the dialog window
	 */
	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(2, true));

		// Show the message
		Label label = new Label(shell, SWT.NONE);
		label.setText(message);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		final Combo combo = new Combo(shell, SWT.NONE);
		combo.setBounds(50, 50, 150, 65);
		String items[] = { "", "compile", "testCompile", "runtime", "testRuntime" };
		combo.setItems(items);

		if (input != null) {
			for (int i = 0; i < items.length; i++) {
				if (input.equals(items[i])) {
					combo.select(i);
					break;
				}
			}
		}
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		combo.setLayoutData(data);

		// Create the OK button and add a handler
		// so that pressing it will set input
		// to the entered value
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		data = new GridData(GridData.FILL_HORIZONTAL);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				input = combo.getText();
				okPressed=true;
				shell.close();
			}
		});

		// Create the cancel button and add a handler
		// so that pressing it will set input to null
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		data = new GridData(GridData.FILL_HORIZONTAL);
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				input = null;
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
	}

	/**
	 * Returns the initial location to use for the shell. The default
	 * implementation centers the shell horizontally (1/2 of the difference to
	 * the left and 1/2 to the right) and vertically (1/3 above and 2/3 below)
	 * relative to the parent shell, or display bounds if there is no parent
	 * shell.
	 *
	 * @param initialSize
	 *            the initial size of the shell, as returned by
	 *            <code>getInitialSize</code>.
	 * @return the initial location of the shell
	 */
	protected Point getInitialLocation(Point initialSize) {
		Point result = getPersistedLocation();
		if (result != null) {
			return result;
		}
		return calculateCenterOnCurrentDisplay(initialSize);
	}

	private Point calculateCenterOnCurrentDisplay(Point initialSize) {
		if (shell==null || shell.isDisposed()){
			return new Point(0,0);
		}
		/* fall back implemenrtation for unpersisted location */
		Composite parent = getParent();
		Monitor monitor = null;

		if (parent == null) {
			monitor = shell.getDisplay().getPrimaryMonitor();
		} else {
			monitor = parent.getMonitor();
		}

		Rectangle monitorBounds = monitor.getClientArea();
		Point centerPoint;
		if (parent != null) {
			centerPoint = Geometry.centerPoint(parent.getBounds());
		} else {
			centerPoint = Geometry.centerPoint(monitorBounds);
		}

		return new Point(centerPoint.x - (initialSize.x / 2), Math.max(monitorBounds.y, Math
				.min(centerPoint.y - (initialSize.y * 2 / 3), monitorBounds.y + monitorBounds.height - initialSize.y)));
	}

	private Point getPersistedLocation() {
		if (shell==null || shell.isDisposed()){
			return null;
		}
		Point result = null;
		IDialogSettings dialogSettings = getDialogSettings();
		if (dialogSettings == null) {
			return null;
		}
		try {
			int x = dialogSettings.getInt(getClass().getName() + DIALOG_ORIGIN_X);
			int y = dialogSettings.getInt(getClass().getName() + DIALOG_ORIGIN_Y);
			result = new Point(x, y);
			// The coordinates were stored relative to the parent shell.
			// Convert to display coordinates.
			Shell parentShell = getParent();
			if (parentShell != null) {
				Point parentLocation = parentShell.getLocation();
				result.x += parentLocation.x;
				result.y += parentLocation.y;
			}
		} catch (NumberFormatException e) {
		}
		return result;
	}

	private IDialogSettings getDialogSettings() {
		MainActivator mainActivator = MainActivator.getDefault();
		if (mainActivator == null) {
			return null;
		}
		IDialogSettings dialogSettings = mainActivator.getDialogSettings();
		return dialogSettings;
	}

}