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
	public void inheritFrom(Type superType);
	
	/**
	 * Set <code>true</code> when this type is part of official gradle DSL documentation
	 * @param partOfGradleDSLDocumentation
	 */
	public void setDocumented(boolean partOfGradleDSLDocumentation);
	

}
