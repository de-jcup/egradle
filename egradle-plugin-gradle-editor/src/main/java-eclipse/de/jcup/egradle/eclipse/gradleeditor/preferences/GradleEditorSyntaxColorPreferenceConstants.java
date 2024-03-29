package de.jcup.egradle.eclipse.gradleeditor.preferences;
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

import de.jcup.egradle.eclipse.util.PreferenceIdentifiable;
import de.jcup.egradle.eclipse.util.PreferenceLabeled;

/**
 * Constant definitions for plug-in preferences
 */
public enum GradleEditorSyntaxColorPreferenceConstants implements PreferenceIdentifiable, PreferenceLabeled {
    COLOR_NORMAL_TEXT("colorNormalText", "Normal text color"),

    COLOR_JAVA_KEYWORD("colorJavaKeywords", "Java keywords"),

    COLOR_JAVA_LITERAL("colorJavaLiteralKeywords", "Java literal keywords"),

    COLOR_GROOVY_KEYWORD("colorGroovyKeywords", "Groovy keywords"),

    COLOR_GROOVY_DOC("colorGroovyDoc", "Groovy doc"),

    COLOR_NORMAL_STRING("colorNormalStrings", "Normal strings"),

    COLOR_GSTRING("colorGStrings", "GStrings"),

    COLOR_COMMENT("colorComments", "Comment"),

    COLOR_ANNOTATION("colorAnnotations", "Annotations"),

    COLOR_GRADLE_APPLY_KEYWORD("colorApplyKeywords", "Gradle apply keywords"),

    COLOR_GRADLE_TASK_KEYWORD("colorGradleTaskKeywords", "Gradle task keywords"),

    COLOR_GRADLE_OTHER_KEYWORD("colorGradleOtherKeywords", "Gradle other keywords"),

    COLOR_GRADLE_VARIABLE("colorGradleVariables", "Gradle variables"),

    COLOR_GRADLE_DEPRECATED_KEYWORD("colorGradleDeprecatedKeywords", "Gradle deprecated keywords"),

    ;

    private String id;
    private String labelText;

    private GradleEditorSyntaxColorPreferenceConstants(String id, String labelText) {
        this.id = id;
        this.labelText = labelText;
    }

    public String getLabelText() {
        return labelText;
    }

    public String getId() {
        return id;
    }

}
