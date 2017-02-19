package de.jcup.egradle.codeassist.dsl;

public interface ModifiableProperty extends Property{
	
	public void setType(Type returnType);
	
	public void setParent(Type parentType);
	
	public void setDocumented(boolean partOfGradleDSLDocumentation);
}
