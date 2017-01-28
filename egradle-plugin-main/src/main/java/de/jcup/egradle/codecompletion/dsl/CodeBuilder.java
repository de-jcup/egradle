package de.jcup.egradle.codecompletion.dsl;

public interface CodeBuilder {

	String createClosure(LanguageElement element);
	
	String createPropertyAssignment(Property property);

	/**
	 * Creates a simple closure setup like
	 * <pre>
	 *    name{
	 *    }
	 * </pre>
	 * @param name
	 * @return code never <code>null</code>
	 */
	String createClosure(String name);

}
