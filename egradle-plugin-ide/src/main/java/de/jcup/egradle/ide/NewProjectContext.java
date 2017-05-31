package de.jcup.egradle.ide;

import static de.jcup.egradle.ide.NewProjectTemplateVariables.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.template.Features;
import de.jcup.egradle.template.FileStructureTemplate;

public class NewProjectContext {
	
	String lastValidationProblem;

	private String javaSourceCompatibility;
	private List<String> multiProjectsList;
	private String projectName;
	private FileStructureTemplate selectedTemplate;


	public String getJavaSourceCompatibility() {
		return javaSourceCompatibility;
	}

	public String getLastValidationProblem() {
		return lastValidationProblem;
	}

	public List<String> getMultiProjectsAsList() {
		return multiProjectsList;
	}

	public String getProjectName() {
		return projectName;
	}

	public FileStructureTemplate getSelectedTemplate() {
		return selectedTemplate;
	}

	public boolean isMultiProject() {
		if (selectedTemplate == null) {
			return false;
		}
		return selectedTemplate.hasFeature(Features.NEW_PROJECT__TYPE_MULTI_PROJECT);
	}

	public boolean isSupportingGradleWrapper() {
		if (selectedTemplate == null) {
			return false;
		}
		return selectedTemplate.hasFeature(Features.NEW_PROJECT__SUPPORTS_GRADLEWRAPPER);
	}

	public boolean isSupportingJava() {
		if (selectedTemplate == null) {
			return false;
		}
		return selectedTemplate.hasFeature(Features.NEW_PROJECT__SUPPORTS_JAVA);
	}

	public void setJavaSourceCompatibility(String javaSourceCompatibility) {
		this.javaSourceCompatibility = javaSourceCompatibility;
	}

	public void setMultiProjects(String multiProjects) {
		this.multiProjectsList = createMultiProjects(multiProjects);
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setSelectedTemplate(FileStructureTemplate selectedTemplate) {
		this.selectedTemplate = selectedTemplate;
	}

	public Properties toProperties() {
		Properties p = new Properties();
		set(p, VAR__JAVA__VERSION, javaSourceCompatibility);
		set(p, VAR__MULTIPROJECTS__INCLUDE_SUBPROJECTS, createIncludeSubProjects());
		set(p, VAR__NAME_OF_PROJECT, projectName);
		String templateName = null;
		if (selectedTemplate!=null){
			templateName=selectedTemplate.getName();
		}
		set(p, VAR__NAME_OF_TEMPLATE, templateName);
		
		return p;
	}

	/**
	 * Validates if context is currently valid. The template must be not null
	 * and the template features must have all necessary data inside this
	 * context
	 * 
	 * @return <code>true</code> when valid
	 */
	public boolean validate() {
		/* reset validation problem */
		lastValidationProblem=null;
		
		if (selectedTemplate == null) {
			lastValidationProblem = "No template selected";
			return false;
		}
		if (StringUtils.isBlank(projectName)){
			lastValidationProblem = "No project name set";
			return false;
		}
		if (!validateMultiProject()){
			return false;
		}
		if (!validateJavaSupport()){
			return false;
		}
		return true;
	}

	public boolean validateJavaSupport() {
		if (isSupportingJava()) {
			if (StringUtils.isBlank(javaSourceCompatibility)) {
				lastValidationProblem = "Java source compatibility not set";
				return false;
			}
		}
		return true;
	}

	public boolean validateMultiProject() {
		if (isMultiProject()) {
			if (multiProjectsList==null || multiProjectsList.isEmpty()) {
				lastValidationProblem = "No multiprojects given";
				return false;
			}
		}
		return true;
	}

	private String createIncludeSubProjects() {
		if (multiProjectsList==null || multiProjectsList.isEmpty()){
			return "";
		}
		return StringUtils.join(multiProjectsList, ", ");
	}

	private List<String> createMultiProjects(String multiProjectsText) {
		String[] splitted = StringUtils.split(multiProjectsText, ",");
		if (splitted==null){
			return Collections.emptyList();
		}
		List<String> list = new ArrayList<>();
		for (String split: splitted){
			if (StringUtils.isBlank(split)){
				continue;
			}
			list.add(split.trim());
		}
		return list;
	}

	private void set(Properties p, TemplateVariable var, String value) {
		if (value==null){
			value="";
		}
		p.setProperty(var.getVariableName(), value);
	}
}
