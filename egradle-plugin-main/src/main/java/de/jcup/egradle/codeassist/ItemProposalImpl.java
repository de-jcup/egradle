package de.jcup.egradle.codeassist;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Itemable;

public class ItemProposalImpl extends AbstractProposalImpl implements Itemable{

	private Item item;

	public ItemProposalImpl(Item item) {
		if (item==null){
			return;
		}
		String name = item.getName();
		if (name.startsWith("task ")){
			name=name.substring(5);
		}
		setName(name);
		setLazyCodeBuilder(new SimpleStringCodeBuilder(item.getName()));
		String type = item.getType();
		setType(type);
		StringBuilder sb = new StringBuilder();
		sb.append("<h3>");
		sb.append(item.getItemType());
		sb.append("</h3>");
		sb.append("<i>Line:</i>");
		sb.append(item.getLine());
		sb.append("<br>");
		if (type!=null){
			sb.append("<i>Type:</i>");
			sb.append(type);
			sb.append("<br>");
		}
		setDescription(sb.toString());
		this.item=item;
	}

	@Override
	public String toString() {
		return "ItemProposalImpl [code=" + getCode() + ", item=" + item + "]";
	}

	@Override
	public Item getItem() {
		return item;
	}

}