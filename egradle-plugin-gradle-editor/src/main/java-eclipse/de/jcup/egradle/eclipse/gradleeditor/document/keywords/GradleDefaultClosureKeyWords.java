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
package de.jcup.egradle.eclipse.gradleeditor.document.keywords;

import de.jcup.egradle.eclipse.document.DocumentKeyWord;

public enum GradleDefaultClosureKeyWords implements DocumentKeyWord {

    IMPLEMENTATION("implementation"),

    TEST_IMPLEMENTATION("testImplementation"),

    RUNTIME_ONLY("runtimeOnly"),

    TEST_RUNTIME_ONLY("testRuntimeOnly"),

    CLASSPATH("classpath"),

    COPY("copy"),

    API("api"),

    FROM("from"),

    INTO("into"),

    INCLUDE("include"),

    EXCLUDE("exclude"),

    DEPENDENCIES("dependencies"),

    REPOSITORIES("repositories"),

    ALL_PROJECTS("allprojects"),

    SUB_PROJECTS("subprojects"),

    ARTIFACTS("artifacts"),

    TEST("test"),

    CLEAN("clean"),

    BUILDSCRIPT("buildscript"),

    FILES("files"),

    FILETREE("fileTree"),

    CONFIGURATIONS("configurations"),

    /* wellknown */
    ASCIIDOCTORJ("asciidoctorj"),

    ASCIIDOCTOR("asciidoctor"),

    MODULES("modules"),

    ATTRIBUTES("attributes"),

    ;

    private String text;

    private GradleDefaultClosureKeyWords(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
