package de.jcup.egradle.codeassist.dsl;

import java.util.List;

/**
 * Represents a method / function
 * @author Albert Tregnaghi
 *
 */
public interface Method extends LanguageElement, TypeChild, Comparable<Method>{

	public Type getReturnType();
	
	public String getReturnTypeAsString();
	
	
	public Type getDelegationTarget();
	
	/**
	 * Returns delegation target or <code>null</code>
	 * @return delegation target or <code>null</code>
	 */
	public String getDelegationTargetAsString();
	
	/**
	 * Returns parameters never <code>null</code>
	 * @return parameter list, never <code>null</code>
	 */
	public List<Parameter> getParameters();
	
}
