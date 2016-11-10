package de.jcup.egradle.core.model;

import de.jcup.egradle.core.token.Token;

/**
 * A model which can be used for outline views
 * @author Albert Tregnaghi
 *
 */
public class TokenOutlineModel extends AbstractOutlineModel<Token> {

	public Item createItem(Token tokenForOffset){
		if (tokenForOffset==null){
			throw new IllegalArgumentException("tokenImpl may not be null!");
		}
		Item item = new Item();
		item.setOffset(tokenForOffset.getOffset());
		item.setLength(tokenForOffset.getLength());
		item.setText(tokenForOffset.getValue());
		map.put(item.getOffset(), item);
		return item;
	}

	


}
