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