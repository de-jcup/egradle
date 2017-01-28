package de.jcup.egradle.codeassist.dsl;

public interface Reason {
	/**
	 * Returns the plugin reason
	 * @return plugin or <code>null</code> if not from plugin
	 */
	public Plugin getPlugin();
}
