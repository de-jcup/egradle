package de.jcup.egradle.core.codecompletion.model.gradledsl;

import java.io.IOException;

import de.jcup.egradle.core.codecompletion.model.Type;

public interface GradleDSLFileLoader {

	/**
	 * Loads type for given string
	 * @param string
	 * @return type
	 * @throws IOException
	 */
	Type load(String string) throws IOException;

}
