package de.jcup.egradle.codeassist.dsl;

import java.util.Map;
import java.util.Set;

/**
 * Represents a type. In java or groovy this can be a class, an interface , an enum etc.
 * @author Albert Tregnaghi
 *
 */
public interface Type extends LanguageElement{

	/**
	 * Return properties (defined + inherited), never <code>null</code>
	 * @return properties, never <code>null</code>
	 */
	public Set<Property> getProperties();
	
	/**
	 * Return defined properties, never <code>null</code>
	 * @return properties, never <code>null</code>
	 */
	public Set<Property> getDefinedProperties();
	
	/**
	 * Return methods (defined + inherited), never <code>null</code>
	 * @return methods, never <code>null</code>
	 */
	public Set<Method> getMethods();
	
	/**
	 * Return defined method (not inherited), never <code>null</code>
	 * @return methods, never <code>null</code>
	 */
	public Set<Method> getDefinedMethods();


	/**
	 * 
	 * @return a short/simple name
	 */
	public String getShortName();
	
	public Map<String, Type> getExtensions();
	
	/**
	 * Returns the reason for given extension id
	 * @param extensionId
	 * @return reason or <code>null</code> if no reason specified
	 */
	public Reason getReasonForExtension(String extensionId);
	
	/**
	 * Returns reason for element. A reason will contain information about plugin or super type.
	 * reason will be null if element is not contained, or element is defined in type itself.
	 * @param element
	 * @return reason or <code>null</code> when no reason is specified
	 */
	public Reason getReasonFor(LanguageElement element);

	/**
	 * Returns the version for this type
	 * @return version, never null. If not version set "current" will be returned
	 */
	public String getVersion();

	/**
	 * Returns super type as string or <code>null</code>
	 * @return name of super type or <code>null</code>
	 */
	public String getSuperTypeAsString();

	public boolean isDescendantOf(String type);

		
}
