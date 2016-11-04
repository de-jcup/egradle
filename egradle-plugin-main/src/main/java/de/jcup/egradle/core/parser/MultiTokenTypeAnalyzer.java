package de.jcup.egradle.core.parser;

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
	public TokenType analyze(Token token) {
		for (TokenTypeAnalyzer analyzer: list){
			TokenType type = analyzer.analyze(token);
			if (type!=null){
				return type;
			}
		}
		return null;
	}

}
