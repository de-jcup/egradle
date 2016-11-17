package de.jcup.egradle.core.token.filter;

import de.jcup.egradle.core.token.Token;

public interface TokenFilter {

	/**
	 * Returns <code>true</code> when given token is filtered
	 * @param token
	 * @return <code>true</code> when given token is filtered
	 */
	boolean isFiltered(Token token);

}