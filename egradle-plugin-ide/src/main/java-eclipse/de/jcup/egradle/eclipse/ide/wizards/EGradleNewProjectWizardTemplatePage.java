package de.jcup.egradle.eclipse.ide.wizards;


import java.util.List;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.ui.SWTFactory;
import de.jcup.egradle.template.FileStructureTemplate;

public class EGradleNewProjectWizardTemplatePage extends WizardPage {

	private Text descriptionText;
	private FileStructureTemplate selectedTemplate;

	public EGradleNewProjectWizardTemplatePage() {
		super("template");

		setTitle("Gradle project template");
		setImageDescriptor(IDEUtil.createImageDescriptor("icons/egradle-banner_64x64.png"));
		setDescription("Select new project FileStructureTemplateto use");
	}

	@Override
	public void createControl(Composite p) {
		Composite parent = SWTFactory.createComposite(p, 2, SWT.BEGINNING, SWT.FILL);
		
		GridData parentLayoutData = new GridData();
		parentLayoutData.horizontalAlignment = SWT.FILL;
		parentLayoutData.verticalAlignment = SWT.FILL;
		parentLayoutData.grabExcessHorizontalSpace = true;
		parentLayoutData.grabExcessVerticalSpace = true;
		parentLayoutData.verticalSpan = 2;
		parentLayoutData.horizontalSpan = 1;
		parentLayoutData.minimumHeight = 50;
		parentLayoutData.heightHint = 100;
		
		/* type group */
		Group typeGroup = new Group(parent, SWT.NONE);
		GridData typeGroupLayoutData = new GridData(SWT.LEFT, SWT.TOP, true, false);
		typeGroup.setText("Init type");
		typeGroup.setLayoutData(typeGroupLayoutData);

		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = true;
		rowLayout.pack = false;
		rowLayout.justify = false;
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginLeft = 5;
		rowLayout.marginTop = 5;
		rowLayout.marginRight = 5;
		rowLayout.marginBottom = 5;
		rowLayout.spacing = 5;

		typeGroup.setLayout(rowLayout);

		/* description group*/
		Group descriptionGroup = new Group(parent, SWT.NONE);
		GridData descriptionGroupLayoutData = new GridData(SWT.LEFT, SWT.FILL, true, true);
		descriptionGroup.setText("Description");
		descriptionGroup.setLayoutData(descriptionGroupLayoutData);
		descriptionGroup.setLayout(new GridLayout());
		
		
		/* content */
		descriptionText = new Text(descriptionGroup, SWT.WRAP| SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.READ_ONLY);
		
		GridData descriptionTextLayoutData = new GridData();
		descriptionTextLayoutData.horizontalAlignment = SWT.FILL;
		descriptionTextLayoutData.verticalAlignment = SWT.FILL;
		descriptionTextLayoutData.grabExcessHorizontalSpace = true;
		descriptionTextLayoutData.grabExcessVerticalSpace = true;
		descriptionTextLayoutData.verticalSpan = 2;
		descriptionTextLayoutData.horizontalSpan = 1;
		descriptionTextLayoutData.minimumHeight = 50;
		descriptionTextLayoutData.heightHint = 100;
		
		descriptionText.setLayoutData(descriptionTextLayoutData);
		
		initTypeButtons(typeGroup);

		// required to avoid an error in the system
		setControl(typeGroup);
		setPageComplete(false);

	}

	void initTypeButtons(Composite parent) {
		List<FileStructureTemplate> templates = IDEUtil.getNewProjectTemplates();
		
		for (FileStructureTemplate template: templates) {
			initTypeButton(parent, template);
		}
	}

	void initTypeButton(Composite parent, FileStructureTemplate template) {
		final Button button = SWTFactory.createRadioButton(parent, template.getName());
		button.setData(template);

		RowData layoutData = new RowData();
		button.setLayoutData(layoutData);

		SelectionListener listener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedTemplate = (FileStructureTemplate) button.getData();
				
				descriptionText.setText(selectedTemplate.getDescription());
			}
		};
		button.addSelectionListener(listener);

		if (template.equals(getSelectedTemplate())) {
			button.setSelection(true);
		}
	}

	public FileStructureTemplate getSelectedTemplate() {
		return selectedTemplate;
	}
}
