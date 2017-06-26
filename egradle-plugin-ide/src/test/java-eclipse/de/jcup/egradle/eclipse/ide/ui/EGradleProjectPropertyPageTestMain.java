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
 package de.jcup.egradle.eclipse.ide.ui;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.core.process.EGradleShellType;
import de.jcup.egradle.eclipse.ide.EGradleProjectPropertyPage;
import de.jcup.egradle.eclipse.ide.EGradleProjectPropertyPageDataSupport;
import de.jcup.egradle.eclipse.preferences.EGradleCallType;
import de.jcup.egradle.eclipse.util.ColorManager;

public class EGradleProjectPropertyPageTestMain {

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

		EGradleProjectPropertyPage toTest = new EGradleProjectPropertyPage(new TestEGradleProjectPropertyPageDataSupport());
		toTest.createControl(shell);
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 5;
		layout.verticalSpacing = 5;
		shell.setLayout(layout);

		shell.setSize(new Point(800, 800));
		
		shell.layout();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
	
	private static class TestEGradleProjectPropertyPageDataSupport implements EGradleProjectPropertyPageDataSupport{

		@Override
		public EGradleCallType getCallType() {
			return EGradleCallType.LINUX_GRADLE_WRAPPER;
		}

		@Override
		public String getRootProjectPath() {
			return "/c/dev/projects/testproject-root";
		}

		@Override
		public String getJavaHomePath() {
			return null;
		}

		@Override
		public String getGradleCallCommand() {
			return "gradlew";
		}

		@Override
		public String getGradleBinInstallFolder() {
			return "/c/dev/gradle/install";
		}

		@Override
		public EGradleShellType getShellType() {
			return EGradleShellType.BASH;
		}
		
	}
}
