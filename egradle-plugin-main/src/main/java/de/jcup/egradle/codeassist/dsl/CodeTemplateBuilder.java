package de.jcup.egradle.codeassist.dsl;

public interface CodeTemplateBuilder {

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

	String createSmartMethodCall(Method method);

}
