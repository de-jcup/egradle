package de.jcup.egradle.core.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public void setItemType(ItemType type) {
		this.itemType = type;
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

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
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

}