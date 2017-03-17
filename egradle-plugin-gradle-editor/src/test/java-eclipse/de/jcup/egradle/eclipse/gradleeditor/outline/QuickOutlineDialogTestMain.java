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
 package de.jcup.egradle.eclipse.gradleeditor.outline;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.eclipse.api.ColorManager;

public class QuickOutlineDialogTestMain {

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
		QuickOutlineDialog dialog = new QuickOutlineDialog(adapter, shell,"Test quick outline dialog...");
		dialog.setInput("dependencies{\n" + "testCompile library.junit\n" + "testCompile library.mockito_all\n" + "}");
		dialog.open();
		display.dispose();

	}

}
