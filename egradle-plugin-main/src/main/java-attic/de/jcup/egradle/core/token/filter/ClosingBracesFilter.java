package de.jcup.egradle.core.token.filter;

import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.core.token.TokenType;

public class ClosingBracesFilter extends AbstractTokenFilter {

	@Override
	protected boolean isSafeFiltered(Token tokenNotNull) {
		TokenType type = tokenNotNull.getType();
		if (TokenType.BRACE_CLOSING.equals(type)){
			return true;
		}
		return false;
	}

}
