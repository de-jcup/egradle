package de.jcup.egradle.core.outline.token;

import java.util.List;

import de.jcup.egradle.core.outline.OutlineItem;
import de.jcup.egradle.core.outline.OutlineModel;
import de.jcup.egradle.core.outline.OutlineModelBuilder;
import de.jcup.egradle.core.outline.OutlineModelImpl;
import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.core.token.filter.ClosingBracesFilter;
import de.jcup.egradle.core.token.filter.CommentFilter;
import de.jcup.egradle.core.token.filter.MultiTokenFilter;
import de.jcup.egradle.core.token.filter.ParameterFilter;
import de.jcup.egradle.core.token.filter.TokenFilter;
import de.jcup.egradle.core.token.filter.UnknownTokenFilter;

public class DefaultTokenOutlineModelBuilder implements OutlineModelBuilder {


	private Token rootToken;
	private TokenFilter filter;

	public DefaultTokenOutlineModelBuilder(Token rootToken){
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
	public OutlineModel build() throws OutlineModelBuilderException{
		OutlineModelImpl model = new OutlineModelImpl();
		
		OutlineItem parentItem = model.getRoot();
		List<Token> children = rootToken.getChildren();
		for (Token childToken: children){
			build(model, parentItem, childToken);
		}
		return model;
	}
	
	public OutlineItem createItem(Token tokenForOffset){
		if (tokenForOffset==null){
			throw new IllegalArgumentException("tokenImpl may not be null!");
		}
		OutlineItem outlineItem = new OutlineItem();
		outlineItem.setOffset(tokenForOffset.getOffset());
		outlineItem.setLength(tokenForOffset.getLength());
		outlineItem.setName(tokenForOffset.getValue());
		return outlineItem;
	}


	private void build(OutlineModelImpl model, OutlineItem parentItem, Token tokenImpl) {
		if (filter.isFiltered(tokenImpl)){
			return;
		}
		OutlineItem outlineItem = createItem(tokenImpl);
		parentItem.add(outlineItem);
		if (tokenImpl.hasChildren()){
			for (Token childToken: tokenImpl.getChildren()){
				build(model, outlineItem, childToken);
			}
		}
	}

}
