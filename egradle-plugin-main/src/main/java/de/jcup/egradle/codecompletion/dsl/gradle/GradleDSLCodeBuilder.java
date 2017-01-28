package de.jcup.egradle.codecompletion.dsl.gradle;

import static de.jcup.egradle.codecompletion.SourceCodeInsertionSupport.*;

import de.jcup.egradle.codecompletion.dsl.CodeBuilder;
import de.jcup.egradle.codecompletion.dsl.LanguageElement;
import de.jcup.egradle.codecompletion.dsl.Property;


public class GradleDSLCodeBuilder implements CodeBuilder {

	@Override
	public String createClosure(LanguageElement element) {
		String name = element.getName();
		return createClosure(name);
	}

	@Override
	public String createClosure(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append("{\n    ").append(CURSOR_VARIABLE).append("\n");
		sb.append("}");
		return sb.toString();
	}

	@Override
	public String createPropertyAssignment(Property property) {
		return property.getName() + " = " + CURSOR_VARIABLE;
	}

}
