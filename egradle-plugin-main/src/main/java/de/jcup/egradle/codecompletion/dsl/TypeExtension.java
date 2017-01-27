package de.jcup.egradle.codecompletion.dsl;

public interface TypeExtension {

	public Object getId();

	public Type getTargetType();
	
	public Type getExtensionType();
	
	public Type getMixinType();


}
