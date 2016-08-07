package de.jcup.egradle.core.process;

public class SystemOutOutputHandler implements ProcessOutputHandler{

	@Override
	public void output(String line) {
		System.out.println(line);
	}

}
