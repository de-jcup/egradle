package de.jcup.egradle.codecompletion.dsl;

import java.io.IOException;

public interface DSLFileLoader {

	/**
	 * Loads DSL type for given full type name
	 * @param name
	 * @return type
	 * @throws IOException
	 */
	Type load(String name) throws IOException;

}
