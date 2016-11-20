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
 package de.jcup.egradle.core.model.token;

import java.util.List;

import de.jcup.egradle.core.model.BuildContext;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.model.ModelBuilder;
import de.jcup.egradle.core.model.ModelImpl;
import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.core.token.filter.MultiTokenFilter;
import de.jcup.egradle.core.token.filter.TokenFilter;

public class DefaultTokenModelBuilder implements ModelBuilder {


	private Token rootToken;
	private TokenFilter filter;

	public DefaultTokenModelBuilder(Token rootToken){
		this(rootToken, new MultiTokenFilter());
//		MultiTokenFilter multi = (MultiTokenFilter) filter;
//		multi.add(new ParameterFilter());
//		multi.add(new CommentFilter());
//		multi.add(new UnknownTokenFilter());
//		multi.add(new ClosingBracesFilter());
		
		
	}
	/**
	 * Creates a new builder
	 * @param rootToken - token to start from, only its children will be inside the outline model! May not be <code>null</code>
	 * @param filter - filter for model building. May not be <code>null</code>
	 */
	public DefaultTokenModelBuilder(Token rootToken, TokenFilter filter){
		if (rootToken==null){
			throw new IllegalArgumentException("rootToken token may not be null!");
		}
		if (filter==null){
			throw new IllegalArgumentException("token filter may not be null!");
		}
		this.rootToken=rootToken;
		this.filter=filter;
	}
	
	
	@Override
	public Model build(BuildContext context) throws OutlineModelBuilderException{
		ModelImpl model = new ModelImpl();
		
		Item parentItem = model.getRoot();
		List<Token> children = rootToken.getChildren();
		for (Token childToken: children){
			build(model, parentItem, childToken);
		}
		return model;
	}
	
	public Item createItem(Token tokenForOffset){
		if (tokenForOffset==null){
			throw new IllegalArgumentException("tokenImpl may not be null!");
		}
		Item item = new Item();
		item.setOffset(tokenForOffset.getOffset());
		item.setLength(tokenForOffset.getLength());
		item.setName(tokenForOffset.getValue());
		return item;
	}


	private void build(ModelImpl model, Item parentItem, Token tokenImpl) {
		if (filter.isFiltered(tokenImpl)){
			return;
		}
		Item item = createItem(tokenImpl);
		parentItem.add(item);
		if (tokenImpl.hasChildren()){
			for (Token childToken: tokenImpl.getChildren()){
				build(model, item, childToken);
			}
		}
	}

}
