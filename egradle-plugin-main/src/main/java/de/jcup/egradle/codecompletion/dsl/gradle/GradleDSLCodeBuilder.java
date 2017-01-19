package de.jcup.egradle.codecompletion.dsl.gradle;

import static de.jcup.egradle.codecompletion.SourceCodeInsertionSupport.*;

import de.jcup.egradle.codecompletion.dsl.CodeBuilder;
import de.jcup.egradle.codecompletion.dsl.Method;
import de.jcup.egradle.codecompletion.dsl.Property;
/* FIXME ATR, 19.01.2017: not clever enough . even properties are done as closures in gradle!
 * must be improved */
public class GradleDSLCodeBuilder implements CodeBuilder {

	@Override
	public String createClosure(Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getName());
		sb.append("{\n    ").append(CURSOR_VARIABLE).append("\n");
		sb.append("}");
		return sb.toString();
	}

	@Override
	public String createPropertyAssignment(Property property) {
		return property.getName() + " = " + CURSOR_VARIABLE;
	}

}
