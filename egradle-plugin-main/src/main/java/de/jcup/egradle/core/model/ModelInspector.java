/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
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
		addItemsWithGivenTypeToList(result, model.getRoot(),type,true);
		return result;
	}

	private void addItemsWithGivenTypeToList(List<Item> result, Item item, ItemType type, boolean recursive) {
		if (type.equals(item.getItemType())){
			result.add(item);
		}
		if (!recursive || !item.hasChildren()){
			return;
		}
		for (Item child : item.getChildren()){
			addItemsWithGivenTypeToList(result,child, type,recursive);
		}
	}

	public List<Item> findAllItemsOfType(ItemType type, Item node) {
		if (type==null){
			return Collections.emptyList();
		}
		List<Item> result = new ArrayList<>();
		addItemsWithGivenTypeToList(result, node,type,true);
		return result;
	}
}
