package de.jcup.egradle.eclipse.ide;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import de.jcup.egradle.eclipse.ide.execution.validation.RootProjectValidationAdapter;
import de.jcup.egradle.eclipse.ide.ui.RootProjectConfigUIDelegate;

public class EGradleProjectPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

	private RootProjectConfigUIDelegate configUIDelegate;

	public EGradleProjectPropertyPage() {
		configUIDelegate = new RootProjectConfigUIDelegate(new RootProjectValidationAdapter() {
			@Override
			public void addFieldEditor(FieldEditor field) {
			}
		});
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite configComposite = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 5;
		layout.verticalSpacing = 5;		
		
		configComposite.setLayout(layout);
		configUIDelegate.createConfigUI(configComposite);
		
		return configComposite;
	}

}
