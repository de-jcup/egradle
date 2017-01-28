package de.jcup.egradle.codecompletion.dsl;

import java.util.Set;

public interface Plugin {

	public String getId();
	
	public String getDescription();
	
	/**
	 * Returns a set of extensions, never <code>null</code>
	 * @return set of extensions, never <code>null</code>
	 */
	public Set<TypeExtension> getExtensions();
}
