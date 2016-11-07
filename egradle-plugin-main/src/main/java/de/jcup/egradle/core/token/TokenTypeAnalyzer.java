package de.jcup.egradle.core.token;

public interface TokenTypeAnalyzer {

	/**
	 * Resolves token type by token information
	 * @param tokenImpl
	 * @return token type or <code>null</code> if analyzer does not know the type of the token
	 */
	public TokenType analyze(Token tokenImpl);
}
