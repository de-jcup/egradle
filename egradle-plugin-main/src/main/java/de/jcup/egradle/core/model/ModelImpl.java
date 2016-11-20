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

import java.util.Set;
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
				/* scan inside parts before of sorted tree map... */
				Set<Integer> sortedKeys = map.keySet();
				int keyBefore=-1;
				for (int key : sortedKeys) {
					if (key < offset) {
						keyBefore=key;
						/* ignore because we start at offset! */
						continue;
					}
					if (keyBefore==-1){
						break;
					}
					item = map.get(keyBefore);
					break;
				}
			}
			return item;
		}
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