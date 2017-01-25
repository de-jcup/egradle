/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.egradle.core.model;

import java.util.SortedMap;
import java.util.TreeMap;

public class ModelImpl implements Model {

	protected SortedMap<Integer, Item> map = new TreeMap<>();
	private Item root = new Item();
	private boolean offsetRegistrationDone;

	public ModelImpl() {
		super();
	}

	@Override
	public Item getItemAt(int offset) {
		if (!offsetRegistrationDone) {
			startOffsetRegistration();
		}
		synchronized(map){
			Item item = map.get(offset);
			if (item == null) {
				item = findApplyableItem(offset);
			}
			return item;
		}
	}

	/**
	 * Example:
	 * <pre>
	 * 
	 * offset1a
	 * 		offset2a
	 * 		 x
	 * 		offset2b
	 *   y-->given offset
	 * offset1b
	 * </pre>
	 * The result should be item having offset1a/b
	 * @param offset offset to scan for
	 * @return item most appliable to given offset
	 */
	private Item findApplyableItem(int offset) {
	
		int scanStart=offset;
		while (scanStart>0){
			Item item = map.get(--scanStart);
			if (item==null){
				continue;
			}
			int start = item.getOffset();
			int end = start+item.getLength();
			if (offset>start && offset<end){
				return item;
			}
		}
		Item simpleFallback = getSimpleFallbackForOffset(offset);
		return simpleFallback;
	}

	private Item getSimpleFallbackForOffset(int offset) {
		Item simpleFallback = null;
		/* fall back - select part before */
		int scanStart = offset;
		while (scanStart>0 && simpleFallback==null){
			simpleFallback = map.get(--scanStart);
		}
		return simpleFallback;
	}

	@Override
	public Item getRoot() {
		return root;
	}

	private void startOffsetRegistration() {
		offsetRegistrationDone=true;
		synchronized(map){
			map.clear();
			register(root,true);
		}
	}

	private void register(Item item, boolean registerOnlyChildren) {
		if (!registerOnlyChildren){
			map.put(item.getOffset(), item);
		}
		for (Item child: item.getChildren()){
			if (child!=null){
				register(child,false);
			}
		}
	}

}