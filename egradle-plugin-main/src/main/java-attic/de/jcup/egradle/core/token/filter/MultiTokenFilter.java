package de.jcup.egradle.core.token.filter;

import java.util.ArrayList;
import java.util.List;

import de.jcup.egradle.core.token.Token;

public class MultiTokenFilter implements TokenFilter{

	private List<TokenFilter> filters = new ArrayList<>();
	
	public void add(TokenFilter filter){
		if (filter==null){
			throw new IllegalArgumentException("filter may not be null");
		}
		
		filters.add(filter);
	}
	
	@Override
	public boolean isFiltered(Token token) {
		for (TokenFilter filter: filters){
			if (filter.isFiltered(token)){
				return true;
			}
		}
		return false;
	}

}
