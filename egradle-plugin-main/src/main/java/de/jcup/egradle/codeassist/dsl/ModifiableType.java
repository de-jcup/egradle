package de.jcup.egradle.codeassist.dsl;

public interface ModifiableType extends Type {
	
	/**
	 * Mixin methods and properties from given mixin type
	 * @param mixinType
	 * @param reason
	 */
	public void mixin(Type mixinType, Reason reason);

	/**
	 * Add an extension to this type
	 * @param extensionId
	 * @param extensionType
	 * @param reason
	 */
	public void addExtension(String extensionId, Type extensionType, Reason reason);
	
	/**
	 * Inherit methods and properties from super type. If this type is an interface it can only extend
	 * interface types, otherwise it can only extend from (one) super type. The method  
	 * does not throw any error when trying to do a wrong extension but does nothing
	 * 
	 * @param superType
	 */
	public void extendFrom(Type superType);
	
	/**
	 * Set <code>true</code> when this type is part of official gradle DSL documentation
	 * @param partOfGradleDSLDocumentation
	 */
	public void setDocumented(boolean partOfGradleDSLDocumentation);

	public void setDescription(String description);
	

}
