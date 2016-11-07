package de.jcup.egradle.core.token;

import java.util.Iterator;
import java.util.List;

public class TokenChainer {

	/**
	 * Start chaining of all tokens contained inside token
	 * @param tokenImpl
	 */
	public void chain(Token tokenImpl) {
		inspecAllChildren(tokenImpl);
	}

	private void inspecAllChildren(Token tokenImpl) {
		if (tokenImpl.hasChildren()){
			inspectAll(tokenImpl.getChildren());
		}
	}

	private void inspectAll(List<TokenImpl> children) {
		TokenImpl tokenBefore=null;
		for (Iterator<TokenImpl> it = children.iterator();it.hasNext();){
			TokenImpl tokenImpl = it.next();
			if (tokenBefore!=null){
				tokenBefore.defineForward(tokenImpl);
			}
			tokenImpl.defineBackward(tokenBefore);
			inspecAllChildren(tokenImpl);
			tokenBefore=tokenImpl;
		}
	}

}
