package de.jcup.egradle.codecompletion.dsl;

import java.io.IOException;
import java.util.Set;

public interface DSLFileLoader {

	/**
	 * Loads DSL type for given full type name
	 * @param name name of type
	 * @return type
	 * @throws IOException
	 */
	Type loadType(String name) throws IOException;

	Set<Plugin> loadPlugins() throws IOException;
	
}
