package de.jcup.egradle.eclipse.gradleeditor.outline;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class QuickOutlineDialogTestMain {

	/**
	 * Just for direct simple UI testing
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Shell");
		shell.setSize(200, 200);
		shell.open();

		GradleEditorOutlineContentProvider provider = new GradleEditorOutlineContentProvider(null);

		IAdaptable adapter = new IAdaptable() {

			@SuppressWarnings("unchecked")
			@Override
			public <T> T getAdapter(Class<T> adapter) {
				if (ITreeContentProvider.class.equals(adapter)) {
					return (T) provider;
				}
				return null;
			}
		};
		QuickOutlineDialog dialog = new QuickOutlineDialog(adapter, shell);
		dialog.setInput("dependencies{\n" + "testCompile library.junit\n" + "testCompile library.mockito_all\n" + "}");
		dialog.open();

		display.dispose();

	}

}
