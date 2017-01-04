package de.jcup.egradle.core.codecompletion;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Itemable;

public class ProposalImpl implements Proposal, Itemable{

	
	private Item item;
	private String code;

	public ProposalImpl(Item item) {
		// TODO check if item is lightweight enough!
		this.item=item;
	}

	@Override
	public String getName() {
		return item.getName();
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Override
	public String getCode() {
		if (code==null){
			return StringUtils.EMPTY;
		}
		return code;
	}

	@Override
	public String getType() {
		return item.getType();
	}

	public Item getItem() {
		return item;
	}
	
}
