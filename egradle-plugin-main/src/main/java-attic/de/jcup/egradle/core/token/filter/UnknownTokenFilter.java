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
 package de.jcup.egradle.core.token.filter;

import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.core.token.TokenType;

/**
 * Filters all type of tokens
 * @author Albert Tregnaghi
 *
 */
public class UnknownTokenFilter extends AbstractTokenFilter {

	@Override
	protected boolean isSafeFiltered(Token tokenNotNull) {
		TokenType type = tokenNotNull.getType();
		if (TokenType.UNKNOWN.equals(type)){
			return true;
		}
		return false;
	}
}
