package de.jcup.egradle.core.codecompletion;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Itemable;

public class ItemProposalImpl implements Proposal, Itemable{

	
	private Item item;
	private String code;

	public ItemProposalImpl(Item item) {
		// TODO check if item is lightweight enough!
		this.item=item;
	}

	@Override
	public String getName() {
		return item.getName();
	}

	@Override
	public String toString() {
		return "ItemProposalImpl [code=" + code + ", item=" + item + "]";
	}

	@Override
	public String getCode() {
		return item.getName();
	}

	@Override
	public String getType() {
		return item.getType();
	}

	public Item getItem() {
		return item;
	}
	
}
