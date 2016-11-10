package de.jcup.egradle.core.outline;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class OutlineModelImpl implements OutlineModel {

	protected SortedMap<Integer, OutlineItem> map = new TreeMap<>();
	private OutlineItem root = new OutlineItem();
	private boolean offsetRegistrationDone;

	public OutlineModelImpl() {
		super();
	}

	@Override
	public OutlineItem getItemAt(int offset) {
		if (!offsetRegistrationDone) {
			startOffsetRegistration();
		}
		synchronized(map){
			OutlineItem outlineItem = map.get(offset);
			if (outlineItem == null) {
				/* scan inside next parts of sorted tree map... */
				Set<Integer> sortedKeys = map.keySet();
				for (int key : sortedKeys) {
					if (key < offset) {
						/* ignore because we start at offset! */
						continue;
					}
					outlineItem = map.get(key);
					break;
				}
			}
			return outlineItem;
		}
	}

	@Override
	public OutlineItem getRoot() {
		return root;
	}

	private void startOffsetRegistration() {
		offsetRegistrationDone=true;
		synchronized(map){
			map.clear();
			register(root,true);
		}
	}

	private void register(OutlineItem outlineItem, boolean registerOnlyChildren) {
		if (!registerOnlyChildren){
			map.put(outlineItem.getOffset(), outlineItem);
		}
		for (OutlineItem child: outlineItem.getChildren()){
			if (child!=null){
				register(child,false);
			}
		}
	}

}