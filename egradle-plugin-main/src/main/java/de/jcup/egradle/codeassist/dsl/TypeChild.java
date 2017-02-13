package de.jcup.egradle.codeassist.dsl;

public interface TypeChild extends LanguageElement{
	/**
	 * Returns the declaring parent type
	 * @return parent type
	 */
	public Type getParent();
}
