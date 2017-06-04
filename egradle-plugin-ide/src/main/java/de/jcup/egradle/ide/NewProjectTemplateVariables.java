package de.jcup.egradle.ide;

public enum NewProjectTemplateVariables implements TemplateVariable{
	
	VAR__NAME_OF_TEMPLATE ("egradle.template.name"),
	VAR__NAME_OF_PROJECT ("egradle.template.projectname"),
	VAR__NAME_OF_GROUP ("egradle.template.groupname"),
	VAR__NAME_OF_SUBPROJECT ("egradle.template.subprojectname"),
	VAR__MULTIPROJECTS__INCLUDE_SUBPROJECTS ("egradle.template.subprojects.include"),
	VAR__JAVA__VERSION("egradle.template.java.version"),
	;
	
	private String variableName;

	private NewProjectTemplateVariables(String variableName){
		this.variableName=variableName;
	}
	
	public String getVariableName() {
		return variableName;
	}
}
