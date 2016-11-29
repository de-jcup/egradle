package de.jcup.egradle.eclipse.ui;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.eclipse.api.ColorManager;
import de.jcup.egradle.eclipse.execution.validation.RootProjectValidationAdapter;
import de.jcup.egradle.eclipse.preferences.EGradleCallType;

public class RootProjectConfigUIDelegateTestMain {

	/**
	 * Just for direct simple UI testing
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ColorManager.setStandalone(new ColorManager());
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Shell");
		shell.setSize(200, 200);
		shell.open();

		RootProjectConfigUIDelegate configComposite = new RootProjectConfigUIDelegate(new RootProjectValidationAdapter() {
			@Override
			public void addFieldEditor(FieldEditor field) {
			}
		}) {
			@Override
			protected Image getValidationButtonImage() {
				return null;
			}
		};
		configComposite.debug = true;

		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 5;
		layout.verticalSpacing = 5;
		shell.setLayout(layout);

		shell.setSize(new Point(800, 800));
		configComposite.createConfigUI(shell);
		if (configComposite.debug) {
			shell.setBackground(new Color(display, new RGB(255, 0, 0)));
		}

		configComposite.setGlobalJavaHomePath("java home path");
		configComposite.setRootProjectPath("root project path");
		String gradleCallTypeID = EGradleCallType.WINDOWS_GRADLE_WRAPPER.getId();
		configComposite.setGradleCallTypeId(gradleCallTypeID);

		shell.layout();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
