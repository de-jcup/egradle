package de.jcup.egradle.core.outline;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An outline item represents a tree node of an outline view. <br>
 * <br>
 * It simply contains information for label provider to do name and image
 * rendering. It also has to contain data like offset and length etc.
 * 
 * @author Albert Tregnaghi
 *
 */
public class OutlineItem {
	
	@Override
	public String toString() {
		return "OutlineItem ['" + name + "', itemType=" + itemType + " offset=" + offset + ", length=" + length + "]";
	}

	private static final OutlineItem[] NO_CHILDREN = new OutlineItem[] {};

	private List<OutlineItem> children;
	private OutlineItem parent;

	private int offset;
	private int length;

	private String name;
	private OutlineItemType itemType;

	private String info;

	public void setItemType(OutlineItemType type) {
		this.itemType = type;
	}

	public OutlineItemType getItemType() {
		if (itemType==null){
			itemType = OutlineItemType.UNKNOWN;
		}
		return itemType;
	}

	public void add(OutlineItem child) {
		if (child == null) {
			throw new IllegalArgumentException("child may not be null");
		}
		if (children == null) {
			children = new ArrayList<>(1);
		}
		children.add(child);
		child.parent = this;
	}

	public OutlineItem[] getChildren() {
		if (children == null) {
			return NO_CHILDREN;
		}
		return children.toArray(new OutlineItem[children.size()]);
	}

	public boolean hasChildren() {
		return children != null;
	}

	public OutlineItem getParent() {
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

	private void print(OutlineItem outlineItem, PrintStream out, int indent) {
		String indentStr = getIndent(indent);
		out.println(indentStr + outlineItem.toString());

		for (OutlineItem child : outlineItem.getChildren()) {
			print(child, out, indent + 3);
		}
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

	public boolean hasChild(OutlineItem item) {
		if (item==null){
			return false;
		}
		if (children==null){
			return false;
		}
		boolean childFound = children.contains(item);
		return childFound;
	}


}