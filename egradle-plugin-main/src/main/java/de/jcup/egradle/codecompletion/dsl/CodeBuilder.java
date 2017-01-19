package de.jcup.egradle.codecompletion.dsl;

public interface CodeBuilder {

	String createClosure(Method method);
	
	String createPropertyAssignment(Property property);

}
