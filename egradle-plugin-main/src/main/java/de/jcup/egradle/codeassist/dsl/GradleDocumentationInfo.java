package de.jcup.egradle.codeassist.dsl;

/**
 * Implementations provide information, if they are part of the official gradle DSL documentation
 * or not.
 * @author Albert Tregnaghi
 *
 */
public interface GradleDocumentationInfo {

	/**
	 * @return <code>true</code> when part of gradle DSL documentation 
	 */
	boolean isDocumented();
	
	/**
	 * Set <code>true</code> when this is a part documented at gradles official website
	 * @param documented
	 */
	void setDocumented(boolean documented);
}
