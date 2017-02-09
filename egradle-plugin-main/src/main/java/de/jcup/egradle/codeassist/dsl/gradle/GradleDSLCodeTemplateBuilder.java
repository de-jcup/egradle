package de.jcup.egradle.codeassist.dsl.gradle;

import static de.jcup.egradle.codeassist.SourceCodeInsertionSupport.*;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.CodeTemplateBuilder;
import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Parameter;
import de.jcup.egradle.codeassist.dsl.Property;
import static de.jcup.egradle.codeassist.dsl.TypeConstants.*;
public class GradleDSLCodeTemplateBuilder implements CodeTemplateBuilder {
	

	@Override
	public String createClosure(LanguageElement element) {
		String name = element.getName();
		return createClosure(name);
	}

	@Override
	public String createClosure(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		return createClosureSkeleton(sb, true);
	}

	private String createClosureSkeleton(StringBuilder sb, boolean addCursor) {
		sb.append("{\n    ");
		if (addCursor) {
			sb.append(CURSOR_VARIABLE);
		}
		sb.append("\n");
		sb.append("}");
		return sb.toString();
	}

	@Override
	public String createPropertyAssignment(Property property) {
		if (property == null) {
			return "";
		}
		String typeAsString = property.getTypeAsString();
		StringBuilder sb = new StringBuilder();
		sb.append(property.getName());
		sb.append(" = ");
		if (JAVA_STRING.equals(typeAsString)) {
			sb.append("'");
			sb.append(CURSOR_VARIABLE);
			sb.append("'");
		} else if (JAVA_FILE.equals(typeAsString)) {
			sb.append("file('");
			sb.append(CURSOR_VARIABLE);
			sb.append("')");
		} else {
			sb.append(CURSOR_VARIABLE);
		}
		return sb.toString();
	}

	@Override
	public String createSmartMethodCall(Method method) {
		if (method == null) {
			return "";
		}
		int closurePos = -1;

		List<Parameter> parameters = method.getParameters();
		if (parameters.size() == 0) {
			return method.getName() + "()" + CURSOR_VARIABLE;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(method.getName());
		sb.append(" "); // groovy way, no opening bracket but space
		int pos = -1;
		for (Iterator<Parameter> pit = parameters.iterator(); pit.hasNext();) {
			pos++;
			Parameter param = pit.next();
			String typeAsString = param.getTypeAsString();
			if (JAVA_STRING.equals(typeAsString)) {
				sb.append('"');
				if (pos == 0) {
					sb.append(CURSOR_VARIABLE);
				}
				sb.append('"');
				if (pit.hasNext()) {
					sb.append(" ");// groovy way, no commata, but space to
									// separate arguments
				}
			} else if (GROOVY_CLOSURE.equals(typeAsString)) {
				if (closurePos == -1) {
					closurePos = pos;
				}
				createClosureSkeleton(sb, pos == 0);
				if (pit.hasNext()) {
					sb.append("\n");
				}
			} else if (JAVA_FILE.equals(typeAsString)) {
				sb.append("file('");
				if (pos == 0) {
					sb.append(CURSOR_VARIABLE);
				}
				sb.append("')");
				if (pit.hasNext()) {
					sb.append(" ");// groovy way, no commata, but space to
									// separate arguments
				}
			} else {
				String name = param.getName();
				if (StringUtils.isBlank(name)) {
					name = "arg" + pos;
				}
				sb.append(name);
				if (pos == 0) {
					sb.append(CURSOR_VARIABLE);
				}
				if (pit.hasNext()) {
					sb.append(" ");// groovy way, no commata, but space to
									// separate arguments
				}
			}
		}
		return sb.toString();
	}

}
