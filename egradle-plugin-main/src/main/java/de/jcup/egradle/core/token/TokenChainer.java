package de.jcup.egradle.core.token;

import java.util.Iterator;
import java.util.List;

/**
 * Token chainer - supports only {@link TokenImpl}!
 * @author Albert Tregnaghi
 *
 */
public class TokenChainer {

	/**
	 * Start chaining of all tokens contained inside token
	 * @param Token
	 */
	public void chain(Token Token) {
		inspecAllChildren(Token);
	}

	private void inspecAllChildren(Token token) {
		if (token.hasChildren()){
			inspectAll(token.getChildren());
		}
	}
	

	private void inspectAll(List<Token> children) {
		TokenImpl tokenImplBefore=null;
		for (Iterator<Token> it = children.iterator();it.hasNext();){
			Token token = it.next();
			if (token instanceof TokenImpl){
				TokenImpl tokenImpl = (TokenImpl) token;
				if (tokenImplBefore!=null){
					tokenImplBefore.chain(tokenImpl);
				}
				inspecAllChildren(token);
				tokenImplBefore=tokenImpl;
				
			}else{
				throw new IllegalArgumentException("Only TokenImpl supported!");
			}
		}
	}

}
