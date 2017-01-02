package de.jcup.egradle.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides inspection methods for model
 * @author albert
 *
 */
public class ModelInspector {

	/**
	 * Find all items of given item type inside model
	 * @param type
	 * @param model
	 * @return item list never <code>null</code>
	 */
	public List<Item> findAllItemsOfType(ItemType type, Model model){
		if (type==null){
			return Collections.emptyList();
		}
		List<Item> result = new ArrayList<>();
		addItemsWithGivenTypeToList(result, model.getRoot(),type);
		return result;
	}

	private void addItemsWithGivenTypeToList(List<Item> result, Item item, ItemType type) {
		if (type.equals(item.getItemType())){
			result.add(item);
		}
		if (!item.hasChildren()){
			return;
		}
		for (Item child : item.getChildren()){
			addItemsWithGivenTypeToList(result,child, type);
		}
	}
}
