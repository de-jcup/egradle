package de.jcup.egradle.core.codecompletion;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Itemable;

public class ItemProposalImpl extends AbstractProposalImpl implements Itemable{

	private Item item;
	private String code;

	public ItemProposalImpl(Item item) {
		if (item==null){
			return;
		}
		this.name=item.getName();
		this.code=item.getName();
		this.type=item.getType();
		
		this.item=item;
	}

	@Override
	public String toString() {
		return "ItemProposalImpl [code=" + code + ", item=" + item + "]";
	}

	@Override
	public Item getItem() {
		return item;
	}

}