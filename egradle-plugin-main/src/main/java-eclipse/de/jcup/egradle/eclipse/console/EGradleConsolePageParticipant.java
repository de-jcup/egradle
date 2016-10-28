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
package de.jcup.egradle.eclipse.console;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;

import de.jcup.egradle.eclipse.Activator;

public class EGradleConsolePageParticipant implements IConsolePageParticipant {
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public void activated() {
	}

	@Override
	public void deactivated() {
	}

	@Override
	public void dispose() {
		Activator.getDefault().removeViewerWithPageParticipant(this);
	}

	@Override
	public void init(IPageBookViewPage page, IConsole console) {
		Control control = page.getControl();
		if (control instanceof StyledText) {
			/* connect only to EGRADLE consoles */

			/*
			 * only process consoles with EGradle in name are handled or the
			 * dedicated EGradle console
			 */
			String type = console.getType();
			String name = console.getName();

			boolean needsEGradleStyling = false;
			needsEGradleStyling = needsEGradleStyling || console instanceof EGradleSystemConsole;
			needsEGradleStyling = needsEGradleStyling
					|| ("org.eclipse.debug.ui.ProcessConsoleType".equals(type) && name.indexOf("EGradle") != -1);

			if (!needsEGradleStyling) {
				return;
			}
			/* Add EGradle process style listener to viewer */
			StyledText viewer = (StyledText) control;
			EGradleConsoleStyleListener myListener = new EGradleConsoleStyleListener();
			viewer.addLineStyleListener(myListener);
			
			Activator.getDefault().addViewer(viewer, this);
		}
	}
}