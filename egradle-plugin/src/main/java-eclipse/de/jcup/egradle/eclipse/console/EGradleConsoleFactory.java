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

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;

public class EGradleConsoleFactory implements IConsoleFactory{
	public static EGradleConsoleFactory INSTANCE = new EGradleConsoleFactory();

	@Override
	public void openConsole() {
			IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
			EGradleConsole console = getConsole();
		    consoleManager.showConsoleView( console );
	}
	
	

	public EGradleConsole getConsole() {
		return findConsole("EGradle Console");
	}

	private EGradleConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++) {
			if (name.equals(existing[i].getName())) {
				return (EGradleConsole) existing[i];
			}

		}
		// no console found, so create a new one
		EGradleConsole myConsole = createConsole(name, conMan);
		return myConsole;
	}



	private EGradleConsole createConsole(String name, IConsoleManager conMan) {
		EGradleConsole myConsole = new EGradleConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

}
