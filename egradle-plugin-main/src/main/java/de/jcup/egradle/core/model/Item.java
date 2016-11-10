package de.jcup.egradle.core.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Item {
	@Override
	public String toString() {
		return "Item ['" + text + "', offset=" + offset + ", length=" + length + "]";
	}

	private static final Item[] EMPTY = new Item[] {};

	private List<Item> children;
	private Item parent;

	private int offset;
	private int length;

	private String text;

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
			return EMPTY;
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

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
	public void print(PrintStream out) {
		print(this, out,0);
	}
	
	private static final Map<Integer, String> INDENT_CACHE = new HashMap<>();
	
	private void print(Item item, PrintStream out, int indent) {
		String indentStr = getIndent(indent);
		out.println(indentStr+item.toString());
		
		for (Item child: item.getChildren()){
			print(child, out, indent +3);
		}
	}

	private static String getIndent(int indent) {
		String indentStr = INDENT_CACHE.get(indent);
		if (indentStr==null){
			StringBuilder sb = new StringBuilder();
			for (int i=0;i<indent;i++){
				sb.append(" ");
			}
			indentStr=sb.toString();
			INDENT_CACHE.put(indent, indentStr);
		}
		return indentStr;
	}
}