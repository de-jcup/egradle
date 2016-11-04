package de.jcup.egradle.core.parser;

import java.util.HashSet;
import java.util.Set;

public class TokenTypeFilter implements TokenFilter {

	private Set<TokenType> setOfIgnoredTypes= new HashSet<>();
	
	public void addIgnored(TokenType type){
		setOfIgnoredTypes.add(type);
	}
	
	@Override
	public boolean isFiltered(Token token){
		if (token==null){
			return true;
		}
		TokenType type = token.getType();
		return setOfIgnoredTypes.contains(type);
	}
}
