package de.jcup.egradle.core.token.filter;

import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.core.token.TokenType;

/**
 * Filters all type of tokens
 * @author Albert Tregnaghi
 *
 */
public class UnknownTokenFilter extends AbstractTokenFilter {

	@Override
	protected boolean isSafeFiltered(Token tokenNotNull) {
		TokenType type = tokenNotNull.getType();
		if (TokenType.UNKNOWN.equals(type)){
			return true;
		}
		return false;
	}
}
