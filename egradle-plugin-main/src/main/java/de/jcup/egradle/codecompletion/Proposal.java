package de.jcup.egradle.codecompletion;

public interface Proposal extends Comparable<Proposal>{

	/**
	 * 
	 * @return name, never <code>null</code>
	 */
	String getName();

	/**
	 * @return code, never <code>null</code>
	 */
	String getCode();
	
	/**
	 * 
	 * @return type or <code>null</code>
	 */
	String getType();
	
	/**
	 * 
	 * @return description or <code>null</code>
	 */
	String getDescription();

	/**
	 * Returns cursor position or -1
	 * @return cursor position or -1
	 */
	int getCursorPos();
}