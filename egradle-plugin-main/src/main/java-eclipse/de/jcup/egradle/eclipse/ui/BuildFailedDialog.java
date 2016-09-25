package de.jcup.egradle.eclipse.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.jcup.egradle.eclipse.api.EGradleUtil;

public class BuildFailedDialog extends Dialog {

	private Label bannerImageIcon;

	private String description;

	private Text detailsText;

	public BuildFailedDialog(Shell parentShell, String description) {
		super(parentShell);
		this.description = description;
	}

	@Override
	public void create() {
		super.create();
		// setTitle("Build failed");
		// setMessage(description, IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
//		Composite area = (Composite) super.createDialogArea(parent);
		Composite area = parent;
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);
		Button button = new Button(container, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		button.setText("Press me");
		
		createBanner(container);
		createDetails(container);
		return area;
	}

	private void createBanner(Composite container) {
		GridData dataBanner = new GridData();
		dataBanner.grabExcessHorizontalSpace = true;
		dataBanner.horizontalAlignment = GridData.FILL;

		Image image = EGradleUtil.getImage("icons/gradle-banner-image.png");
		bannerImageIcon = new Label(container, SWT.NONE);
		bannerImageIcon.setImage(image);
		bannerImageIcon.setLayoutData(dataBanner);
	}

	private void createDetails(Composite container) {
		GridData dataLastName = new GridData();
		dataLastName.grabExcessHorizontalSpace = true;
		dataLastName.horizontalAlignment = GridData.FILL;
		detailsText = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		detailsText.setLayoutData(dataLastName);

		detailsText.setText(description);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setImage(EGradleUtil.getImage("icons/gradle-og.gif"));
		newShell.setText("EGradle build failed");
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

}