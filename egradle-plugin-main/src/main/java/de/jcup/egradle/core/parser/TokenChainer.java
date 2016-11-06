package de.jcup.egradle.core.parser;

import java.util.Iterator;
import java.util.List;

public class TokenChainer {

	/**
	 * Start chaining of all tokens contained inside token
	 * @param token
	 */
	public void chain(Token token) {
		inspecAllChildren(token);
	}

	private void inspecAllChildren(Token token) {
		if (token.hasChildren()){
			inspectAll(token.getChildren());
		}
	}

	private void inspectAll(List<Token> children) {
		Token tokenBefore=null;
		for (Iterator<Token> it = children.iterator();it.hasNext();){
			Token token = it.next();
			if (tokenBefore!=null){
				tokenBefore.defineForward(token);
			}
			token.defineBackward(tokenBefore);
			inspecAllChildren(token);
			tokenBefore=token;
		}
	}

}
