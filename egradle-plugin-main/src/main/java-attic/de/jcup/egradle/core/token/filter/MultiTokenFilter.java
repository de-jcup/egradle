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
