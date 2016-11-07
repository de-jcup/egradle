package de.jcup.egradle.core.token;

import java.util.ArrayList;
import java.util.List;

public class MultiTokenTypeAnalyzer implements TokenTypeAnalyzer{

	private List<TokenTypeAnalyzer> list =new ArrayList<>();
	
	public void add(TokenTypeAnalyzer analyzer){
		if (analyzer==null){
			return;
		}
		list.add(analyzer);
	}
	
	@Override
	public TokenType analyze(Token tokenImpl) {
		for (TokenTypeAnalyzer analyzer: list){
			TokenType type = analyzer.analyze(tokenImpl);
			if (type!=null){
				return type;
			}
		}
		return null;
	}

}
