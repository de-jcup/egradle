package de.jcup.egradle.codeassist.dsl;

public interface TypeExtension extends Comparable<TypeExtension> {

	public String getId();

	public String getTargetTypeAsString();
	
	public String getExtensionTypeAsString();
	
	public String getMixinTypeAsString();


}
