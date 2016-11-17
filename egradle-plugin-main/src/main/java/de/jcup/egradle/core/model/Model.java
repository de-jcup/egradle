package de.jcup.egradle.core.model;

/**
 * A model for outline views.
 * @author Albert Tregnaghi
 *
 */
public interface Model {

	/**
	 * Finds item, starting at given offset. If no item found at given start
	 * offset algorighm tries to resolve next possible items. If no item found
	 * <code>null</code> is returned
	 * 
	 * @param offset
	 * @return item or <code>null</code>
	 */
	Item getItemAt(int offset);

	/**
	 * Returns the root item
	 * 
	 * @return root item, never <code>null</code>
	 */
	Item getRoot();
	

}