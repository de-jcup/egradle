package de.jcup.egradle.eclipse.console;

import org.eclipse.ui.console.MessageConsoleStream;

import de.jcup.egradle.core.process.ProcessOutputHandler;

public class EGradleConsoleProcessOutputHandler implements ProcessOutputHandler{

	private MessageConsoleStream messageStream;

	public EGradleConsoleProcessOutputHandler(){
		this.messageStream = EGradleConsoleFactory.INSTANCE.getConsole().newMessageStream();
	}
	
	@Override
	public void output(String line) {
		messageStream.println(line);
	}

}
