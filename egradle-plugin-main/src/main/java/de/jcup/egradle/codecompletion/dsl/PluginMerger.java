package de.jcup.egradle.codecompletion.dsl;

import java.util.Set;

public interface PluginMerger<T extends Type, P extends Plugin> {

	/**
	 * Merges meta information from plugins into given type
	 * @param type
	 * @param plugins
	 */
	public void merge(T type, Set<P> plugins);
	
	
}
