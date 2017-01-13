package de.jcup.egradle.core.codecompletion.model;

/**
 * Represents an element of the language
 * @author Albert Tregnaghi
 *
 */
public interface LanguageElement {

	/**
	 * @return name 
	 */
	public String getName();
	
	
	/**
	 * 
	 * @return desription or <code>null</code>
	 */
	public String getDescription();
}
