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
 package de.jcup.egradle.eclipse.gradleeditor;

import java.util.Collections;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.OpenFileAction;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;

import de.jcup.egradle.eclipse.util.EclipseUtil;

public class OpenGradleResourceDialog extends FilteredResourcesSelectionDialog {

	private static final String GRADLE_RESOURCE_DIALOG_SETTINGS = "de.jcup.egradle.eclipse.gradleeditor.OpenGradleResourceDialog";

	private static final int OPEN_WITH_ID = IDialogConstants.CLIENT_ID + 1;


	private Button openWithButton;

	/**
	 * Creates a new instance of the class.
	 *
	 * @param parentShell
	 *            the parent shell
	 * @param container
	 *            the container
	 * @param typesMask
	 *            the types mask
	 */
	public OpenGradleResourceDialog(Shell parentShell, IContainer container, int typesMask) {
		super(parentShell, true, container, typesMask);
		addListFilter(new GroovyGradleAndJavaTypeFilter());
		setHelpAvailable(false);
		setMessage("Please select an estimated type:");
		setTitle("Open potential groovy/gradle/java resource");
		
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		EditorActivator editorActivator = EditorActivator.getDefault();
		IDialogSettings dialogSettings = editorActivator.getDialogSettings();
		IDialogSettings settings = dialogSettings.getSection(GRADLE_RESOURCE_DIALOG_SETTINGS);

		if (settings == null) {
			settings = dialogSettings.addNewSection(GRADLE_RESOURCE_DIALOG_SETTINGS);
		}

		return settings;
	}
	
	@Override
	protected ItemsFilter createFilter() {
		return super.createFilter();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control control = super.createDialogArea(parent);
		getPatternControl().setEnabled(false);
		return control;
	}
	
	
	


	private class GroovyGradleAndJavaTypeFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (!(element instanceof IFile)) {
				return false;
			}
			IFile file = (IFile) element;
			String extension = file.getFileExtension();
			boolean supported = false;
			supported = supported || "gradle".equals(extension);
			supported = supported || "groovy".equals(extension);
			supported = supported || "java".equals(extension);
			// no class because this is not supported by resource dialog
			if (!supported) {
				return false;
			}
			String name = file.getName();
			String initialPattern = getInitialPattern();
			String pattern = initialPattern + "." + extension;
			if (name.equals(pattern)) {
				return true;
			}
			return false;
		}

	}

	@Override
	protected void fillContextMenu(IMenuManager menuManager) {
		super.fillContextMenu(menuManager);

		final IStructuredSelection selectedItems = getSelectedItems();
		if (selectedItems.isEmpty()) {
			return;
		}

		IWorkbenchPage activePage = getActivePage();
		if (activePage == null) {
			return;
		}

		menuManager.add(new Separator());

		// Add 'Open' menu item
		OpenFileAction openFileAction = new OpenFileAction(activePage) {
			@Override
			public void run() {
				okPressed();
			}
		};
		openFileAction.selectionChanged(selectedItems);
		if (openFileAction.isEnabled()) {
			menuManager.add(openFileAction);

			IAdaptable selectedAdaptable = getSelectedAdaptable();
			if (selectedAdaptable != null) {

				// Add 'Open With' sub-menu
				MenuManager subMenu = new MenuManager("Open with");
				OpenWithMenu openWithMenu = new ResourceOpenWithMenu(activePage, selectedAdaptable);
				subMenu.add(openWithMenu);
				menuManager.add(subMenu);
			}
		}

	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		GridLayout parentLayout = (GridLayout) parent.getLayout();
		parentLayout.makeColumnsEqualWidth = false;

		openWithButton = createDropdownButton(parent, OPEN_WITH_ID, "Open with", new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				showOpenWithMenu();
			}
		});
		setButtonLayoutData(openWithButton);

		new Label(parent, SWT.NONE).setLayoutData(new GridData(5, 0));
		parentLayout.numColumns++;

		Button okButton = createButton(parent, IDialogConstants.OK_ID, "Open", true);
		Button cancelButton = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

		GridData cancelLayoutData = (GridData) cancelButton.getLayoutData();
		GridData okLayoutData = (GridData) okButton.getLayoutData();
		int buttonWidth = Math.max(cancelLayoutData.widthHint, okLayoutData.widthHint);
		cancelLayoutData.widthHint = buttonWidth;
		okLayoutData.widthHint = buttonWidth;
	}

	private Button createDropdownButton(final Composite parent, int id, String label, MouseListener mouseListener) {
		char textEmbedding = parent.getOrientation() == SWT.LEFT_TO_RIGHT ? '\u202a' : '\u202b';
		Button button = createButton(parent, id, textEmbedding + label + '\u202c', false);
		if (Util.isMac()) {
			// Button#setOrientation(int) is a no-op on the Mac. Use a Unicode
			// BLACK DOWN-POINTING SMALL TRIANGLE.
			button.setText(button.getText() + " \u25BE"); //$NON-NLS-1$
		} else {
			int dropDownOrientation = parent.getOrientation() == SWT.LEFT_TO_RIGHT ? SWT.RIGHT_TO_LEFT
					: SWT.LEFT_TO_RIGHT;
			button.setOrientation(dropDownOrientation);
			button.setText(button.getText() + " \u25BE"); //$NON-NLS-1$
			button.addMouseListener(mouseListener);
		}
		return button;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		switch (buttonId) {
		case OPEN_WITH_ID:
			showOpenWithMenu();
			break;
		default:
			super.buttonPressed(buttonId);
		}
	}

	@Override
	protected void updateButtonsEnableState(IStatus status) {
		super.updateButtonsEnableState(status);
		if (openWithButton != null && !openWithButton.isDisposed()) {
			openWithButton.setEnabled(!status.matches(IStatus.ERROR) && getSelectedItems().size() == 1);
		}
	}

	private IAdaptable getSelectedAdaptable() {
		IStructuredSelection s = getSelectedItems();
		if (s.size() != 1) {
			return null;
		}
		Object selectedElement = s.getFirstElement();
		if (selectedElement instanceof IAdaptable) {
			return (IAdaptable) selectedElement;
		}
		return null;
	}

	private IWorkbenchPage getActivePage() {
		IWorkbenchWindow activeWorkbenchWindow = EclipseUtil.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) {
			return null;
		}
		return activeWorkbenchWindow.getActivePage();
	}

	private void showOpenWithMenu() {
		IWorkbenchPage activePage = getActivePage();
		if (activePage == null) {
			return;
		}
		IAdaptable selectedAdaptable = getSelectedAdaptable();
		if (selectedAdaptable == null) {
			return;
		}

		ResourceOpenWithMenu openWithMenu = new ResourceOpenWithMenu(activePage, selectedAdaptable);
		showMenu(openWithButton, openWithMenu);
	}

	private void showMenu(Button button, IContributionItem menuContribution) {
		Menu menu = new Menu(button);
		Point p = button.getLocation();
		p.y = p.y + button.getSize().y;
		p = button.getParent().toDisplay(p);

		menu.setLocation(p);
		menuContribution.fill(menu, 0);
		menu.setVisible(true);
	}

	private final class ResourceOpenWithMenu extends OpenWithMenu {
		private ResourceOpenWithMenu(IWorkbenchPage page, IAdaptable file) {
			super(page, file);
		}

		@Override
		protected void openEditor(IEditorDescriptor editorDescriptor, boolean openUsingDescriptor) {
			computeResult();
			setResult(Collections.EMPTY_LIST);
			close();
			super.openEditor(editorDescriptor, openUsingDescriptor);
		}
	}
}
