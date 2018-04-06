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
package de.jcup.egradle.codeassist.dsl.gradle;

/**
 * See types of gradle scripts at
 * <a href="https://docs.gradle.org/current/dsl/">Gradle Build Language
 * Reference</a>
 * 
 * @author Albert Tregnaghi
 *
 */
public enum GradleFileType {
	GRADLE_BUILD_SCRIPT("org.gradle.api.Project"), GRADLE_INIT_SCRIPT(
			"org.gradle.api.invocation.Gradle"), GRADLE_SETTINGS_SCRIPT("org.gradle.api.initialization.Settings"),

	UNKNOWN("unknown");

	private String rootType;

	GradleFileType(String rootType) {
		this.rootType = rootType;
	}

	public String getRootType() {
		return rootType;
	}
}