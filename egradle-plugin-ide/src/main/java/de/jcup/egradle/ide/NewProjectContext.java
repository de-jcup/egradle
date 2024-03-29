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
package de.jcup.egradle.ide;

import static de.jcup.egradle.ide.NewProjectTemplateVariables.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.template.Features;
import de.jcup.egradle.template.FileStructureTemplate;

public class NewProjectContext {

    private static final String VAR_PROJECT_NAME = "${projectName}";

    private static final Pattern GROUP_NAME_PATTERN = Pattern.compile("[^a-z0-9\\.]");
    private static final Pattern VAR_PROJECT_NAME_PATTERN = Pattern.compile(Pattern.quote(VAR_PROJECT_NAME));

    String lastValidationProblem;

    private String javaSourceCompatibility;
    private List<String> multiProjectsList;
    private String projectName;
    private FileStructureTemplate selectedTemplate;
    private String multiProjectsAsIncludeString;
    private String javaHome;

    private String groupName;

    private String gradleVersion;

    private boolean gradleWrapperEnabled;

    private String unprocessedMultiProjectNames;

    public String getJavaSourceCompatibility() {
        return javaSourceCompatibility;
    }

    public String getLastValidationProblem() {
        return lastValidationProblem;
    }

    public List<String> getMultiProjectsAsList() {
        return multiProjectsList;
    }

    public String getMultiProjectsAsIncludeString() {
        StringBuilder sb = new StringBuilder();
        sb.append(multiProjectsAsIncludeString);
        List<String> predefined = selectedTemplate.getPredefinedSubprojects();
        if (!predefined.isEmpty()) {
            for (String predefinedProject : predefined) {
                sb.append(",\n'");
                sb.append(predefinedProject);
                sb.append("'");
            }
        }
        return sb.toString();
    }

    public String getProjectName() {
        return projectName;
    }

    public FileStructureTemplate getSelectedTemplate() {
        return selectedTemplate;
    }

    public boolean isSupportingHeadlessImport() {
        if (selectedTemplate == null) {
            return false;
        }
        return selectedTemplate.hasFeature(Features.NEW_PROJECT__SUPPORTS_HEADLESS_IMPORT);
    }

    public boolean isMultiProject() {
        if (selectedTemplate == null) {
            return false;
        }
        return selectedTemplate.hasFeature(Features.NEW_PROJECT__TYPE_MULTI_PROJECT);
    }

    public void setGradleWrapperEnabled(boolean gradleWrapperEnabled) {
        this.gradleWrapperEnabled = gradleWrapperEnabled;
    }

    /**
     * @return <code>true</code> when wrapper is enabled AND wrapper is supported by
     *         template
     */
    public boolean isGradleWrapperSupportedAndEnabled() {
        return isSupportingGradleWrapper() && gradleWrapperEnabled;
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
        this.unprocessedMultiProjectNames = multiProjects;
        this.multiProjectsList = createMultiProjects(multiProjects);
        this.multiProjectsAsIncludeString = createMultiProjectsAsIncludeString(multiProjectsList);
    }

    private String createMultiProjectsAsIncludeString(List<String> list) {
        if (list == null) {
            return "";
        }
        if (list.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Iterator<String> it = list.iterator(); it.hasNext();) {
            String data = it.next();
            sb.append('\'');
            sb.append(data);
            sb.append('\'');
            if (it.hasNext()) {
                sb.append(",\n");
            }
        }
        return sb.toString();
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public String getJavaHome() {
        return javaHome;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        if (groupName == null || groupName.trim().length() == 0) {
            return suggestGroupName(projectName);
        }
        return groupName;
    }

    static String suggestGroupName(String name) {
        if (name == null) {
            return "";
        }
        return GROUP_NAME_PATTERN.matcher(name.trim().toLowerCase()).replaceAll(".");

    }

    public void setProjectName(String projectName) {
        if (projectName == null) {
            projectName = "";
        }
        this.projectName = projectName.trim();
    }

    public void setSelectedTemplate(FileStructureTemplate selectedTemplate) {
        this.selectedTemplate = selectedTemplate;
        this.gradleWrapperEnabled = isSupportingGradleWrapper(); // per default
                                                                 // enabled
                                                                 // when
                                                                 // gradlewrapper
                                                                 // is
                                                                 // supported
    }

    public void setGradleVersion(String version) {
        this.gradleVersion = version;
    }

    public String getGradleVersion() {
        if (gradleVersion == null || gradleVersion.trim().length() == 0) {
            gradleVersion = VAR__GRADLE__VERSION.getDefaultValue();
        }
        return gradleVersion;
    }

    public Properties toProperties() {
        Properties p = new Properties();
        set(p, VAR__JAVA__VERSION, getJavaSourceCompatibility());
        set(p, VAR__MULTIPROJECTS__INCLUDE_SUBPROJECTS, getMultiProjectsAsIncludeString());
        set(p, VAR__NAME_OF_PROJECT, getProjectName());
        set(p, VAR__NAME_OF_GROUP, getGroupName());
        set(p, VAR__GRADLE__VERSION, getGradleVersion());
        String templateName = null;
        if (selectedTemplate != null) {
            templateName = selectedTemplate.getName();
        }
        set(p, VAR__NAME_OF_TEMPLATE, templateName);

        return p;
    }

    /**
     * Validates if context is currently valid. The template must be not null and
     * the template features must have all necessary data inside this context
     * 
     * @return <code>true</code> when valid
     */
    public boolean validate() {
        /* reset validation problem */
        lastValidationProblem = null;

        if (selectedTemplate == null) {
            lastValidationProblem = "No template selected";
            return false;
        }
        if (StringUtils.isBlank(projectName)) {
            lastValidationProblem = "No project name set";
            return false;
        }
        if (!validateMultiProject()) {
            return false;
        }
        if (!validateJavaSupport()) {
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
            if (StringUtils.isNotBlank(javaHome)) {
                File file = new File(javaHome);
                if (!file.exists()) {
                    lastValidationProblem = javaHome + " does not exist!";
                    return false;
                }
                if (!file.isDirectory()) {
                    lastValidationProblem = javaHome + " is not a directory!";
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validateMultiProject() {
        if (isMultiProject()) {
            if (multiProjectsList == null || multiProjectsList.isEmpty()) {
                lastValidationProblem = "No multiprojects given";
                return false;
            }
        }
        return true;
    }

    private List<String> createMultiProjects(String multiProjectsText) {
        String[] splitted = StringUtils.split(multiProjectsText, ",");
        if (splitted == null) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (String split : splitted) {
            if (StringUtils.isBlank(split)) {
                continue;
            }
            String text = split.trim();
            String replacement = getProjectName();
            String subProjectName = replaceProjectNameVariable(text, replacement);
            list.add(subProjectName);
        }
        Collections.sort(list);

        return list;
    }

    public static String replaceProjectNameVariable(String text, String replacement) {
        if (replacement == null) {
            replacement = "";
        }
        return VAR_PROJECT_NAME_PATTERN.matcher(text).replaceAll(replacement);
    }

    private void set(Properties p, TemplateVariable var, String value) {
        if (value == null) {
            value = "";
        }
        p.setProperty(var.getVariableName(), value);
    }

    public String getUnProcessedMultiProjectNames() {
        return unprocessedMultiProjectNames;
    }

}
