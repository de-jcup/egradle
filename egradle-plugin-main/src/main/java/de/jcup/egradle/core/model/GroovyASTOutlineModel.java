package de.jcup.egradle.core.model;

import groovyjarjarantlr.collections.AST;

/**
 * A model which can be used for outline views
 * @author Albert Tregnaghi
 *
 */
public class GroovyASTOutlineModel extends AbstractOutlineModel<AST> {

	public Item createItem(AST ast){
		if (ast==null){
			throw new IllegalArgumentException("ast may not be null!");
		}
		Item item = new Item();
		item.setOffset(0); /* FIXME ATR, 09.11.2016 : this is only to test and still a problem !*/
		item.setLength(1);
		item.setText(ast.toString());//+" - children:"+ast.getNumberOfChildren());
		map.put(item.getOffset(), item);
		return item;
	}

	


}
