package de.jcup.egradle.core.codecompletion;

public interface Proposal {

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

}