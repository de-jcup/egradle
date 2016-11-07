package de.jcup.egradle.core.token.filter;

import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.core.token.TokenType;

public class CommentFilter extends AbstractTokenFilter {

	@Override
	protected boolean isSafeFiltered(Token tokenNotNull) {
		TokenType type = tokenNotNull.getType();
		if (TokenType.COMMENT__SINGLE_LINE.equals(type)){
			return true;
		}
		if (TokenType.COMMENT__MULTI_LINE.equals(type)){
			return true;
		}
		return false;
	}

}
