package de.jcup.egradle.core.codecompletion;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IAdaptable;

import de.jcup.egradle.core.model.Item;

public class ProposalImpl implements Proposal, IAdaptable{

	
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

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (Item.class.equals(adapter)){
			return (T) item;
		}
		return null;
	}
	
}
