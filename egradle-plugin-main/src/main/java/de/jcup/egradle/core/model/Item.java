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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Item {

	@Override
	public String toString() {
		return "Item ['" + name + "', itemType=" + itemType + " offset=" + offset + ", length=" + length + "]";
	}

	private static final Item[] NO_CHILDREN = new Item[] {};

	private List<Item> children;
	private Item parent;

	private int offset;
	private int length;

	private String name;
	private ItemType itemType;
	private Modifier modifier;

	private String info;

	private int column;

	private int line;

	private String type;

	private boolean aPossibleParent;

	private boolean isClosureBlock;

	private String identifier;

	public boolean isClosureBlock() {
		return isClosureBlock;
	}

	public void setClosureBlock(boolean isClosure) {
		this.isClosureBlock = isClosure;
	}

	public void setItemType(ItemType type) {
		this.itemType = type;
	}

	public boolean isRoot() {
		return parent == null;
	}

	/**
	 * @return modifier never <code>null</code>
	 */
	public Modifier getModifier() {
		if (modifier == null) {
			modifier = Modifier.DEFAULT;
		}
		return modifier;
	}

	public void setModifier(Modifier modifier) {
		this.modifier = modifier;
	}

	/**
	 * @return type never <code>null</code>
	 */
	public ItemType getItemType() {
		if (itemType == null) {
			itemType = ItemType.UNKNOWN;
		}
		return itemType;
	}

	public void add(Item child) {
		if (child == null) {
			throw new IllegalArgumentException("child may not be null");
		}
		if (children == null) {
			children = new ArrayList<>(1);
		}
		children.add(child);
		child.parent = this;
	}

	public Item[] getChildren() {
		if (children == null) {
			return NO_CHILDREN;
		}
		return children.toArray(new Item[children.size()]);
	}

	public boolean hasChildren() {
		return children != null;
	}

	public Item getParent() {
		return parent;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Set name and calculate identifier - if you want to explicit set identifer you must it after calling this method via
	 * {@link #setIdentifier(String)}
	 * 
	 * @param name
	 */
	public void setName(String name) {
		if (name == null) {
			name = "";
		}
		this.name = name;
		String[] splitted = StringUtils.split(name.trim());
		if (splitted == null || splitted.length == 0) {
			this.identifier = "";
		} else {
			this.identifier = splitted[0];
		}
	}

	/**
	 * Get the name of the item. Can contain additional parts - e.g. "task
	 * myTask"
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Be aware: {@link #setName(String)} does change identifier too! Calling this method
	 * should only be necessary in some special cases!
	 * @param identifier
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Get the identifier of the item, means name without additional parts -
	 * e.g. name="task myTask", identifier="task"
	 * 
	 * @return identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	public void print(PrintStream out) {
		print(this, out, 0);
	}

	private static final Map<Integer, String> INDENT_CACHE = new HashMap<>();

	private void print(Item item, PrintStream out, int indent) {
		String indentStr = getIndent(indent);
		out.println(indentStr + item.toString());

		for (Item child : item.getChildren()) {
			print(child, out, indent + 3);
		}
	}

	private boolean closed;

	private String target;

	private String configuration;

	private String[] parameters;

	private ItemType lastChainedItemType;

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public boolean isClosed() {
		return closed;
	}

	private static String getIndent(int indent) {
		String indentStr = INDENT_CACHE.get(indent);
		if (indentStr == null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < indent; i++) {
				sb.append(" ");
			}
			indentStr = sb.toString();
			INDENT_CACHE.put(indent, indentStr);
		}
		return indentStr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + ((itemType == null) ? 0 : itemType.hashCode());
		result = prime * result + length;
		result = prime * result + line;
		result = prime * result + ((modifier == null) ? 0 : modifier.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + offset;
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (column != other.column)
			return false;
		if (itemType != other.itemType)
			return false;
		if (length != other.length)
			return false;
		if (line != other.line)
			return false;
		if (modifier != other.modifier)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (offset != other.offset)
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public void setInfo(String string) {
		this.info = string;
	}

	/**
	 * @return additional info string or <code>null</code>
	 */
	public String getInfo() {
		return info;
	}

	public boolean hasChild(Item item) {
		if (item == null) {
			return false;
		}
		if (children == null) {
			return false;
		}
		boolean childFound = children.contains(item);
		return childFound;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getLine() {
		return line;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public boolean hasAlreadyItemTypeDefined() {
		return !ItemType.UNKNOWN.equals(getItemType());
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	/**
	 * @return parameters or <code>null</code>
	 */
	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String... parameters) {
		this.parameters = parameters;
	}

	/**
	 * Creates a string containing all information of this item, separated by
	 * space, parameters with comma
	 * @param withType - when <code>true</code> the type (if set) is inside search string, otherwise not
	 * @return full string
	 */
	String buildSearchString(boolean withType) {
		StringBuilder sb = new StringBuilder();
		if (withType){
			if (type != null) {
				sb.append(type);
				sb.append(" ");
			}
		}
		if (configuration != null) {
			sb.append(configuration);
			sb.append(" ");
		}
		if (name != null) {
			sb.append(name);
			sb.append(" ");
		}
		if (target != null) {
			sb.append(target);
			sb.append(" ");
		}
		if (parameters != null) {
			for (String param : parameters) {
				if (param != null) {
					sb.append(param);
					sb.append(",");
				}

			}
		}
		if (info != null) {
			sb.append(info);
			sb.append(" ");
		}
		String itemText = sb.toString();
		return itemText;
	}

	/**
	 * Set item to be a possible parent or not
	 * 
	 * @param canBeParent
	 */
	public void setAPossibleParent(boolean canBeParent) {
		this.aPossibleParent = canBeParent;
	}

	/**
	 * 
	 * @return <code>true</code> when this item can be a parent
	 */
	public boolean isAPossibleParent() {
		return aPossibleParent;
	}

	public ItemType getLastChainedItemType() {
		return lastChainedItemType;
	}
	
	public void setLastChainedItemType(ItemType lastItemType) {
		this.lastChainedItemType = lastItemType;
	}
	
}