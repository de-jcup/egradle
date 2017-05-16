package de.jcup.egradle.eclipse.ide.wizards;

import static org.apache.commons.lang3.Validate.*;

public enum NewProjectInitType {

	GROOVY_LIBRARY("groovy-library", "Creates a gradle project initialized for Groovy"),

	SCALA_LIBRARY("scala-library", "Creates a gradle project initialized for Scala"),

	JAVA_LIBRARY("java-library", "Creates a gradle project initialized for Java"),

	BASIC("basic", "Creates a gradle project containing a simple build file with example code");

	private String type;
	private String description;

	NewProjectInitType(String type, String description) {
		notNull(type, "'type' may not be null");
		notNull(description, "'description' may not be null");

		this.type = type;
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}
}