package de.jcup.egradle.codeassist;

public class TemplateCodeBuilder extends  AbstractLazyCodeBuilder{
	
	private String templateContent;

	TemplateCodeBuilder(Template template){
		this.templateContent=template.getContent();
	}
	
	@Override
	protected String createTemplate() {
		return templateContent;
	}

}
