/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
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
