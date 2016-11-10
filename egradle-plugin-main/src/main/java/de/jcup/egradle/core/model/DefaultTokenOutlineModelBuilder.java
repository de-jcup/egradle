package de.jcup.egradle.core.model;

import java.util.List;

import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.core.token.filter.TokenFilter;

public class DefaultTokenOutlineModelBuilder implements OutlineModelBuilder {


	private Token rootToken;
	private TokenFilter filter;

	/**
	 * Creates a new builder
	 * @param rootToken - token to start from, only its children will be inside the outline model! May not be <code>null</code>
	 * @param filter - filter for model building. May not be <code>null</code>
	 */
	public DefaultTokenOutlineModelBuilder(Token rootToken, TokenFilter filter){
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
	public OutlineModel build() {
		TokenOutlineModel model = new TokenOutlineModel();
		
		Item parentItem = model.getRoot();
		List<Token> children = rootToken.getChildren();
		for (Token childToken: children){
			build(model, parentItem, childToken);
		}
		return model;
	}


	private void build(TokenOutlineModel model, Item parentItem, Token tokenImpl) {
		if (filter.isFiltered(tokenImpl)){
			return;
		}
		Item item = model.createItem(tokenImpl);
		parentItem.add(item);
		if (tokenImpl.hasChildren()){
			for (Token childToken: tokenImpl.getChildren()){
				build(model, item, childToken);
			}
		}
	}

}
