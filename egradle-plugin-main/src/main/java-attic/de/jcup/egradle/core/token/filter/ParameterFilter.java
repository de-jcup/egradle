package de.jcup.egradle.core.token.filter;

import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.core.token.TokenType;

public class ParameterFilter extends AbstractTokenFilter {

	@Override
	protected boolean isSafeFiltered(Token tokenNotNull) {
		TokenType type = tokenNotNull.getType();
		if (TokenType.BRACKET_OPENING.equals(type)) {
			return true;
		}
		if (TokenType.BRACKET_CLOSING.equals(type)) {
			return true;
		}
		return false;
	}

}
