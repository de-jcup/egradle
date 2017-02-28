package de.jcup.egradle.codeassist;

import de.jcup.egradle.codeassist.dsl.CodeTemplateBuilder;

public class ClosureByStringCodeBuilder extends AbstractLazyCodeBuilder{

	private String content;
	private CodeTemplateBuilder builder;

	public ClosureByStringCodeBuilder(String content, CodeTemplateBuilder builder) {
		this.content=content;
		this.builder=builder;
	}

	
	@Override
	protected String createTemplate() {
		return builder.createClosure(content);
	}

}
