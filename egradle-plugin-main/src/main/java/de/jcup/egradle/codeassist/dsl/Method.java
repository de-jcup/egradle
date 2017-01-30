package de.jcup.egradle.codeassist.dsl;

import java.util.List;

/**
 * Represents a method / function
 * @author Albert Tregnaghi
 *
 */
public interface Method extends LanguageElement{

	public Type getReturnType();
	
	/**
	 * Returns parameters never <code>null</code>
	 * @return parameter list, never <code>null</code>
	 */
	public List<Parameter> getParameters();

}
