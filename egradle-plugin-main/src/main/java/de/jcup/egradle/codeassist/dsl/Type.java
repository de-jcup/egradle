package de.jcup.egradle.codeassist.dsl;

import java.util.Map;
import java.util.Set;

/**
 * Represents a type. In java or groovy this can be a class, an interface , an enum etc.
 * @author Albert Tregnaghi
 *
 */
public interface Type extends LanguageElement{

	public Set<Property> getProperties();
	
	public Set<Method> getMethods();

	/**
	 * 
	 * @return a short/simple name
	 */
	public String getShortName();
	
	public void mixin(Type mixinType, Reason reason);

	public void addExtension(String extensionId, Type extensionType, Reason reason);

	public Map<String, Type> getExtensions();
	
	/**
	 * Returns the reason for given extension id
	 * @param extensionId
	 * @return reason or <code>null</code> if no reason specified
	 */
	public Reason getReasonForExtension(String extensionId);
	
	/**
	 * Returns reason for method
	 * @param method
	 * @return reason or <code>null</code> when no reason is specified
	 */
	public Reason getReasonForMethod(Method method);

	/**
	 * Returns the version for this type
	 * @return version, never null. If not version set "current" will be returned
	 */
	public String getVersion();
		
}
