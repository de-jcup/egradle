package de.jcup.egradle.codeassist.dsl;

import java.util.List;

import de.jcup.egradle.core.api.StringUtilsAccess;

/**
 * Represents a method / function
 * @author Albert Tregnaghi
 *
 */
public interface Method extends LanguageElement, TypeChild{

	public Type getReturnType();
	
	/**
	 * Returns parameters never <code>null</code>
	 * @return parameter list, never <code>null</code>
	 */
	public List<Parameter> getParameters();
	
}
