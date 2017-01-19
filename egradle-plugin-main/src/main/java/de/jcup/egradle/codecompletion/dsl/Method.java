package de.jcup.egradle.codecompletion.dsl;

import java.util.List;

/**
 * Represents a method / function
 * @author Albert Tregnaghi
 *
 */
public interface Method extends LanguageElement{

	public Type getReturnType();
	
	public List<Parameter> getParameters();
}
