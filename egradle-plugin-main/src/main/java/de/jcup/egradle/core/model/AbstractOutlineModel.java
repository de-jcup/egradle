package de.jcup.egradle.core.model;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class AbstractOutlineModel<T> implements OutlineModel, ItemCreator<T> {

	protected SortedMap<Integer, Item> map = new TreeMap<>();
	private Item root = new Item();

	public AbstractOutlineModel() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.model.OutlineModel#getItemAt(int)
	 */
	@Override
	public Item getItemAt(int offset) {
		Item item = map.get(offset);
		if (item==null){
			/* scan inside next parts of sorted tree map...*/
			Set<Integer> sortedKeys = map.keySet();
			for (int key: sortedKeys){
				if (key<offset){
					/* ignore because we start at offset!*/
					continue;
				}
				item = map.get(key);
				break;
			}
		}
		return item;
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.model.OutlineModel#getRoot()
	 */
	@Override
	public Item getRoot() {
		return root;
	}

}