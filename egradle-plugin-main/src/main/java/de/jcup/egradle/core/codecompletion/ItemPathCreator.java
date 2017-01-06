package de.jcup.egradle.core.codecompletion;

import de.jcup.egradle.core.model.Item;

public class ItemPathCreator {

	
	/* FIXME albert,06.01.2017:maybe we must integrate a UID inside item instead of name, or we truncate parts like xyz( to xyz only! */
	public String createPath(Item item){
		StringBuilder sb = new StringBuilder();
		sb.append(createPathString(item));
		Item parent = item.getParent();
		while (parent!=null && !parent.isRoot()){
			sb.insert(0, '.');
			sb.insert(0, createPathString(parent));
			parent=parent.getParent();
		}
		return sb.toString();
	}

	private String createPathString(Item item) {
		if (item.isRoot()){
			return "";
		}
		return item.getName();
	}
}
