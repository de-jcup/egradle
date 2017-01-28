package de.jcup.egradle.codeassist;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.model.Item;

public class ItemPathCreator {

	/**
	 * Creates a path for given item by using name of item. If name contains
	 * whitespace characters, only the first part before first whitespace is used as path
	 * identifier part.
	 * 
	 * @param item
	 *            when item is null an empty string will be returned
	 * @return path, never <code>null</code>
	 */
	public String createPath(Item item) {
		if (item == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(createPathString(item));
		Item parent = item.getParent();
		while (parent != null && !parent.isRoot()) {
			sb.insert(0, '.');
			sb.insert(0, createPathString(parent));
			parent = parent.getParent();
		}
		return sb.toString();
	}

	private String createPathString(Item item) {
		if (item.isRoot()) {
			return "";
		}
		String name = item.getName();
		if (name == null) {
			return "";
		}
		if (StringUtils.containsWhitespace(name)) {
			String[] splitted = StringUtils.splitPreserveAllTokens(name);
			if (splitted == null || splitted.length == 0) {
				/* should never happen */
				return "";
			}
			return splitted[0];
		}
		return name;
	}
}
