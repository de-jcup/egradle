/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.egradle.codeassist.dsl.gradle;

import static de.jcup.egradle.codeassist.SourceCodeInsertionSupport.*;
import static de.jcup.egradle.codeassist.dsl.TypeConstants.*;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.CodeTemplateBuilder;
import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Parameter;
import de.jcup.egradle.codeassist.dsl.Property;

public class GradleDSLCodeTemplateBuilder implements CodeTemplateBuilder {

	private static final String GRADLE_JAVA_VERSION = "org.gradle.api.JavaVersion";

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
		} else if (JAVA_FILE.equals(typeAsString)) {
			sb.append("file('");
			sb.append(CURSOR_VARIABLE);
			sb.append("')");
		} else if (JAVA_MAP.equals(typeAsString)) { // no generic info, so
													// simply do a key
			sb.append("[ id: 1");
			sb.append(CURSOR_VARIABLE);
			sb.append("]");
		} else if (JAVA_COLLECTION.equals(typeAsString)) {
			sb.append("[ 1");
			sb.append(CURSOR_VARIABLE);
			sb.append(", 2]");
		} else if (JAVA_SIMPLE_BOOLEAN.equals(typeAsString)) {
			sb.append("true");
			sb.append(CURSOR_VARIABLE);
		} else if (GRADLE_JAVA_VERSION.equals(typeAsString)) {
			sb.append("JavaVersion.java8");
			sb.append(CURSOR_VARIABLE);
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
			} else if (JAVA_SIMPLE_BOOLEAN.equals(typeAsString)) {
				sb.append(" true");
				if (pos == 0) {
					sb.append(CURSOR_VARIABLE);
				}
				if (pit.hasNext()) {
					sb.append(" ");// groovy way, no commata, but space to
									// separate arguments
				}
			} else if (JAVA_COLLECTION.equals(typeAsString)) {
				sb.append(" [1");
				if (pos == 0) {
					sb.append(CURSOR_VARIABLE);
				}
				sb.append(",2 ]");
				if (pit.hasNext()) {
					sb.append(" ");// groovy way, no commata, but space to
									// separate arguments
				}
			} else if (JAVA_MAP.equals(typeAsString)) {
				sb.append(" [id:");
				if (pos == 0) {
					sb.append(CURSOR_VARIABLE);
				}
				sb.append("1 ]");
				if (pit.hasNext()) {
					sb.append(" ");// groovy way, no commata, but space to
									// separate arguments
				}
			} else if (GRADLE_JAVA_VERSION.equals(typeAsString)) {
				sb.append("JavaVersion.java8");
				if (pos == 0) {
					sb.append(CURSOR_VARIABLE);
				}
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
