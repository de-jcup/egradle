package de.jcup.egradle.core.model;

public interface ItemCreator<T> {

	/**
	 * Creates an item by given element
	 * @param element
	 * @return item, never <code>null</code>
	 */
	public Item createItem(T element);

}