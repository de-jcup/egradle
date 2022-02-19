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

public enum NewProjectTemplateVariables implements TemplateVariable {

    VAR__NAME_OF_TEMPLATE("egradle.template.name"),

    VAR__NAME_OF_PROJECT("egradle.template.projectname"),

    VAR__NAME_OF_GROUP("egradle.template.groupname"),

    VAR__NAME_OF_SUBPROJECT("egradle.template.subprojectname"),

    VAR__MULTIPROJECTS__INCLUDE_SUBPROJECTS("egradle.template.subprojects.include","${projectName}-web,${projectName}-core,${projectName}-cli"),

    VAR__JAVA__VERSION("egradle.template.java.version","11"),

    VAR__GRADLE__VERSION("egradle.template.gradle.version", "7.4"),
    
    ;

    private String variableName;
    private String defaultValue;

    private NewProjectTemplateVariables(String variableName) {
        this(variableName, null);
    }

    private NewProjectTemplateVariables(String variableName, String defaultValue) {
        this.variableName = variableName;
        this.defaultValue = defaultValue;
    }

    public String getVariableName() {
        return variableName;
    }

    public boolean hasDefaultValue() {
        return defaultValue != null;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

}
