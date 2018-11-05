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
package de.jcup.egradle.junit;

public enum EGradleJUnitTestTasksType {
	CLEAN_ALL("egradle.junit.clean.all", "clean test"),

	CLEAN_ONLY_TESTS("egradle.junit.clean.onlytests", "cleanTest test"),

	CLEAN_NOTHING("egradle.junit.clean.nothing", "test");

	private String id;
	private String testTasks;

	EGradleJUnitTestTasksType(String id, String testTasks) {
		this.id = id;
		this.testTasks = testTasks;
	}

	public String getId() {
		return id;
	}

	public String getTestTasks() {
		return testTasks;
	}

	public static EGradleJUnitTestTasksType findById(String id) {
		for (EGradleJUnitTestTasksType c : values()) {
			if (c.getId().equals(id)) {
				return c;
			}
		}
		return null;
	}
}