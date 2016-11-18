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

public enum GradleTaskKeyWords implements DocumentKeyWord{

	TASKDEFINITION("task"),
	
	DO_FIRST_SHORT("<<"),
	
	DO_FIRST("doFirst"),
	
	DO_LAST("doLast"),
	
	TYPE("type:"),
	
	DEPENDS_ON("dependsOn:"),
	;

	private String text;

	private GradleTaskKeyWords(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}
}
