package de.jcup.egradle.core.process;

public interface WorkingDirectoryProvider {
	/**
	 * Returns the working directory where gradle call is executed
	 * @return working directory
	 */
	public String getWorkingDirectory();
}
