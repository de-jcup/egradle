package de.jcup.egradle.eclipse.ide.wizards;

import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.ui.SWTFactory;
import de.jcup.egradle.ide.NewProjectContext;
import de.jcup.egradle.template.FileStructureTemplate;

public class EGradleNewProjectWizardSelectTemplatePage extends WizardPage {

	private Text descriptionText;
	private NewProjectContext context;
	private List<FileStructureTemplate> templates;
	private org.eclipse.swt.widgets.List templateList;

	public EGradleNewProjectWizardSelectTemplatePage(NewProjectContext context) {
		super("template");
		this.context = context;
		setTitle("Gradle project template");
		setImageDescriptor(IDEUtil.createImageDescriptor("icons/egradle-banner_64x64.png"));
		setDescription("Select new project FileStructureTemplateto use");
		templates = IDEUtil.getNewProjectTemplates();
	}

	@Override
	public void createControl(Composite p) {
		Composite parent = SWTFactory.createComposite(p, 1, SWT.BEGINNING, SWT.FILL);

		GridData layoutData = new GridData();
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.minimumHeight = 50;
		layoutData.heightHint = 200;

		templateList = new org.eclipse.swt.widgets.List(parent, SWT.SCROLL_PAGE);
		templateList.setLayoutData(layoutData);
		
		descriptionText = SWTFactory.createText(parent, SWT.WRAP | SWT.MULTI | SWT.BORDER |  SWT.READ_ONLY, SWT.FILL);
		
		layoutData = new GridData();
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.minimumHeight = 50;
		layoutData.heightHint = 80;
		descriptionText.setLayoutData(layoutData);
		
		initTemplateComponent(parent);

		// required to avoid an error in the system
		setControl(parent);
		setPageComplete(true);

	}

	void initTemplateComponent(Composite parent) {
		if (templates.size() == 0) {
			return;
		}
		String[] items = new String[templates.size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = templates.get(i).getName();
		}

		templateList.setFont(parent.getFont());
		templateList.setItems(items);
		
		if (context.getSelectedTemplate() == null) {
			templateList.select(0);
			handleTemplateSelection(0);
		}

		SelectionListener listener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = templateList.getSelectionIndex();
				handleTemplateSelection(index);
			}

		};
		templateList.addSelectionListener(listener);
	}

	private void handleTemplateSelection(int index) {
		FileStructureTemplate selectedTemplate = templates.get(index);
		context.setSelectedTemplate(selectedTemplate);
		descriptionText.setText(selectedTemplate.getDescription());
	}

}
