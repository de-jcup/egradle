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
 package de.jcup.egradle.core.util;

/**
 * Implementation must be able to transform GString text containing gradle variables to normal text.<br><br>
 * 
 * e.g.
 * <br>
 * root project resides in "/develop/great-project"<br>
 * <code>"${rootproject}/subproject1/build.gradle"</code>
 *  will be transformed to <code>"/develop/great-project/subproject1/build.gradle"</code>
 * @author Albert Tregnaghi
 *
 */
public interface GradleStringTransformer {

	public static final String ROOTPROJECT_PROJECTDIR="rootProject.projectDir";
	
	/**
	 * Transforms text
	 * @param text
	 * @return transformed text - parts not able to transform will still be contained as variables
	 */
	public String transform(String text);
}
