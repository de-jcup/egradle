package de.jcup.egradle.core.token.filter;

import de.jcup.egradle.core.token.Token;

public abstract class AbstractTokenFilter implements TokenFilter {

	@Override
	public final boolean isFiltered(Token token) {
		if (token==null){
			return handleNull();
		}
		return isSafeFiltered(token);
	}

	protected boolean handleNull() {
		return true;
	}

	protected abstract boolean isSafeFiltered(Token tokenNotNull);

}
