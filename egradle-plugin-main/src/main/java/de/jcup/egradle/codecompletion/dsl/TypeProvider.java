package de.jcup.egradle.codecompletion.dsl;

import de.jcup.egradle.codecompletion.CodeCompletionService;

/**
 * A provider for types
 * @author Albert Tregnaghi
 *
 */
public interface TypeProvider{

	/**
	 * Try to get a type for given name.
	 * @param name
	 * @return type or <code>null</code>
	 */
	public Type getType(String name);
}
