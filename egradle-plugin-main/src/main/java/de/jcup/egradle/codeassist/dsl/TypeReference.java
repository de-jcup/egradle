package de.jcup.egradle.codeassist.dsl;

public interface TypeReference extends Comparable<TypeReference>{

	public Type getType();
	
	public String getTypeAsString();
	
}
