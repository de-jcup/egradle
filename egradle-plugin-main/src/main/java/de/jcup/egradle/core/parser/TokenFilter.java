package de.jcup.egradle.core.parser;

public interface TokenFilter {

	/**
	 * Returns <code>true</code> when given token is filtered
	 * @param token
	 * @return <code>true</code> when given token is filtered
	 */
	boolean isFiltered(Token token);

}