package de.jcup.egradle.codeassist;

import de.jcup.egradle.codeassist.dsl.CodeTemplateBuilder;
import de.jcup.egradle.codeassist.dsl.Method;

public class SmartMethodCodeBuilder extends  AbstractLazyCodeBuilder{
	
	private Method method;
	private CodeTemplateBuilder builder;

	SmartMethodCodeBuilder(Method method, CodeTemplateBuilder builder){
		this.method=method;
		this.builder=builder;
	}
	
	@Override
	protected String createTemplate() {
		return builder.createSmartMethodCall(method);
	}

}
