package de.jcup.egradle.codeassist.dsl;

/**
 * Represents an type of the language
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
