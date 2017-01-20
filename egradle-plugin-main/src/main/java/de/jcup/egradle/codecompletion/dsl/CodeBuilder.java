package de.jcup.egradle.codecompletion.dsl;

public interface CodeBuilder {

	String createClosure(LanguageElement element);
	
	String createPropertyAssignment(Property property);

}
