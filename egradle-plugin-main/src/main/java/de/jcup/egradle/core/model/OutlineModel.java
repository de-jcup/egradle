package de.jcup.egradle.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import de.jcup.egradle.core.token.Token;

/**
 * A model which can be used for outline views
 * @author Albert Tregnaghi
 *
 */
public class OutlineModel {

	/* sorted map, so integer possible next entry easier to find...*/
	private SortedMap<Integer, Item> map= new TreeMap<>();
	private Item root = new Item();
	
	public Item createItem(Token tokenForOffset){
		if (tokenForOffset==null){
			throw new IllegalArgumentException("tokenImpl may not be null!");
		}
		Item item = new Item();
		item.tokenImpl=tokenForOffset;
		return item;
	}
	
	/**
	 * Finds item, starting at given offset. If no item
	 * found at given start offset algorighm tries to resolve
	 * next possible items. If no item found <code>null</code> is returned
	 * @param offset
	 * @return item or <code>null</code>
	 */
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
	
	public Item getRoot(){
		return root;
	}
	private static final Item[] EMPTY = new Item[]{};

	public class Item{
		
		private List<Item> children;
		private Token tokenImpl;
		private Item parent;
		
		public void add(Item child){
			if (child==null){
				throw new IllegalArgumentException("child may not be null");
			}
			if (children==null){
				children = new ArrayList<>(1);
			}
			map.put(child.tokenImpl.getOffset(), child);
			children.add(child);
			child.parent=this;
		}
		
		public Item[] getChildren(){
			if (children==null){
				return EMPTY;
			}
			return children.toArray(new Item[children.size()]);
		}
		
		public boolean hasChildren(){
			return children!=null;
		}

		public Token getToken() {
			return tokenImpl;
		}

		public Item getParent() {
			return parent;
		}
	}

}
