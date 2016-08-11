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
 package de.jcup.egradle.eclipse.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import de.jcup.egradle.eclipse.Activator;

public class EGradleLaunchConfigurationMainTab extends AbstractLaunchConfigurationTab {

	public static final String PROPERTY_ARGUMENTS = "arguments";
	public static final String PROPERTY_PROJECTNAME = "projectName";
	private Text argumentsField;
	private Text projectNameField;

	@Override
	public void createControl(Composite parent) {
		parent.setLayout(new FillLayout());

		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		GridLayout gridLayout = new GridLayout(2, false);
		composite.setLayout(gridLayout);
		
		
		GridData labelGridData = new GridData();
		labelGridData.horizontalAlignment = GridData.FILL;
		labelGridData.verticalAlignment = GridData.BEGINNING;
		labelGridData.grabExcessHorizontalSpace = false;
		labelGridData.grabExcessVerticalSpace = false;
		
		GridData projectFieldGridData = new GridData();
		projectFieldGridData.horizontalAlignment = GridData.FILL;
		projectFieldGridData.verticalAlignment = GridData.BEGINNING;
		projectFieldGridData.grabExcessHorizontalSpace = true;
		projectFieldGridData.grabExcessVerticalSpace = false;

		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.grabExcessVerticalSpace = true;
		
		Label label1 = new Label(composite, SWT.NULL);
		label1.setText("Project: ");
		label1.setLayoutData(labelGridData);

		projectNameField = new Text(composite, SWT.BORDER);
		projectNameField.setLayoutData(projectFieldGridData);
		projectNameField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		projectNameField.setToolTipText("Enter gradle project name here - or keep empty for root project.");

		Label label = new Label(composite, SWT.NULL);
		label.setText("Program arguments: ");
		label.setLayoutData(labelGridData);


		argumentsField = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		argumentsField.setLayoutData(gridData2);
		argumentsField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		argumentsField.setToolTipText("Enter gradle commands here.");
		composite.pack();

	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			argumentsField.setText(configuration.getAttribute(PROPERTY_ARGUMENTS, ""));
			projectNameField.setText(configuration.getAttribute(PROPERTY_PROJECTNAME, ""));
		} catch (CoreException e) {
			throw new IllegalStateException("cannot init", e);
		}

	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		IProject project = getProject();
		if (project!=null){
			configuration.setAttribute(PROPERTY_PROJECTNAME, project.getName());
		}
	}

	/**
	 * Try to resolve the project
	 * @return project or <code>null</code>
	 */
	private IProject getProject() {
		IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		if (page != null) {
			ISelection selection = page.getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection ss = (IStructuredSelection) selection;
				if (!ss.isEmpty()) {
					Object obj = ss.getFirstElement();
					if (obj instanceof IProject) {
						return (IProject) obj;
					}
					if (obj instanceof IResource) {
						IProject pro = ((IResource) obj).getProject();
						return pro;

					}
				}
			}
			// IEditorPart part = page.getActiveEditor();
			// if (part != null) {
			// IEditorInput input = part.getEditorInput();
			// return (IJavaElement) input.getAdapter(IJavaElement.class);
			// }
		}
		return null;
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(PROPERTY_ARGUMENTS, argumentsField.getText());
		configuration.setAttribute(PROPERTY_PROJECTNAME, projectNameField.getText());
	}

	@Override
	public String getName() {
		return "Gradle";
	}

}
