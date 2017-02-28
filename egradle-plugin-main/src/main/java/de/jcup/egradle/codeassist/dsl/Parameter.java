package de.jcup.egradle.codeassist.dsl;

/**
 * Represents a parameter
 * @author Albert Tregnaghi
 *
 */
public interface Parameter extends LanguageElement, Comparable<Parameter> {

	public Type getType();
	
	public String getTypeAsString();
}
