package de.jcup.egradle.codeassist.dsl;

public interface ModifiableType extends Type {
	public void mixin(Type mixinType, Reason reason);

	public void addExtension(String extensionId, Type extensionType, Reason reason);

	/**
	 * Extends this type by given one
	 * 
	 * @param superType
	 */
	public void extendBySuperType(Type superType);

}
