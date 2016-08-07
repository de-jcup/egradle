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
