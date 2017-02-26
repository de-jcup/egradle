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
	 * Inherit methdods and properties from super type
	 * 
	 * @param superType
	 */
	public void extendFromSuperClass(Type superType);
	
	/**
	 * Inherit methods and properties from other interface type, can be called multiple times
	 * @param interfaceType
	 */
	public void extendFromInterface(Type interfaceType);
	
	/**
	 * Set <code>true</code> when this type is part of official gradle DSL documentation
	 * @param partOfGradleDSLDocumentation
	 */
	public void setDocumented(boolean partOfGradleDSLDocumentation);

	public void setDescription(String description);
	

}
