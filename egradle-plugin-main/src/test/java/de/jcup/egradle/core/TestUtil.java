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
 package de.jcup.egradle.core;

import java.io.File;

public class TestUtil {

	public static File PARENT_OF_TEST = new File("egradle-plugin-main/src/test/res/");
	static {
		if (!PARENT_OF_TEST.exists()) {
			/*
			 * fall back - to be testable by eclipse in sub projects and also
			 * via gradle from root project.
			 */
			PARENT_OF_TEST = new File("src/test/res/");
		}
	}
	public static final File ROOTFOLDER_1 = new File(PARENT_OF_TEST, "rootproject1");
	
	/**
	 * Rootfolder 2 - is a multi project - contains itself a .project, which must be ignored at imports, because we prefer virtual root project...
	 */
	public static final File ROOTFOLDER_2 = new File(PARENT_OF_TEST, "rootproject2");
	
	/**
	 * Rootfolder 3 - is a single project (contains no subfolders with .project)
	 */
	public static final File ROOTFOLDER_3 = new File(PARENT_OF_TEST, "rootproject3");
	
	public static final File ROOTFOLDER_2_ECLIPSE_PROJECT1 = new File(ROOTFOLDER_2, "eclipse-project1");
	public static final File ROOTFOLDER_2_ECLIPSE_PROJECT2 = new File(ROOTFOLDER_2, "eclipse-project2");
	public static final File ROOTFOLDER_2_NO_ECLIPSE_PROJECT1 = new File(ROOTFOLDER_2, "no-eclipse-project1");
	public static final File ROOTFOLDER_2_NO_ECLIPSE_PROJECT2 = new File(ROOTFOLDER_2, "no-eclipse-project1");
	
	/**
	 * Rootfolder 4 - contains only gradle test files used by parser tests
	 */
	public static final File ROOTFOLDER_4 = new File(PARENT_OF_TEST, "rootproject4");
	
	public static final File ROOTFOLDER_4_TEST1_GRADLE = new File(ROOTFOLDER_4, "test1.gradle");
	public static final File ROOTFOLDER_4_TEST2_GRADLE = new File(ROOTFOLDER_4, "test2.gradle");
	public static final File ROOTFOLDER_4_TEST3_GRADLE = new File(ROOTFOLDER_4, "test3.gradle");
	public static final File ROOTFOLDER_4_TEST4_GRADLE = new File(ROOTFOLDER_4, "test4.gradle");
	public static final File ROOTFOLDER_4_TEST5_GRADLE = new File(ROOTFOLDER_4, "test5.gradle");
}
