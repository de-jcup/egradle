package de.jcup.egradle.codeassist;

import de.jcup.egradle.codeassist.dsl.CodeTemplateBuilder;
import de.jcup.egradle.codeassist.dsl.Property;

public class PropertyAssignmentCodeBuilder extends AbstractLazyCodeBuilder{

	private Property property;
	private CodeTemplateBuilder builder;

	public PropertyAssignmentCodeBuilder(Property property, CodeTemplateBuilder builder) {
		this.property=property;
		this.builder=builder;
	}

	protected String createTemplate() {
		return builder.createPropertyAssignment(property);
	}

}
