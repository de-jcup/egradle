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

import static org.junit.Assert.*;

import java.io.File;

public class TestUtil {

	public static File SRC_TEST_RES_FOLDER = new File("egradle-plugin-main/src/test/res/");
	static {
		if (!SRC_TEST_RES_FOLDER.exists()) {
			/*
			 * fall back - to be testable by eclipse in sub projects and also
			 * via gradle from root project.
			 */
			SRC_TEST_RES_FOLDER = new File("src/test/res/");
		}
	}
	
	public static File SRC_MAIN_RES_FOLDER = new File("egradle-plugin-main/src/main/res/");
	static {
		if (!SRC_MAIN_RES_FOLDER.exists()) {
			/*
			 * fall back - to be testable by eclipse in sub projects and also
			 * via gradle from root project.
			 */
			SRC_MAIN_RES_FOLDER = new File("src/main/res/");
		}
	}
	
	public static File SDK__SRC_MAIN_RES_FOLDER = new File("egradle-plugin-sdk/src/main/res/");
	static {
		if (!SDK__SRC_MAIN_RES_FOLDER.exists()) {
			/*
			 * fall back - to be testable by eclipse in sub projects and also
			 * via gradle from root project.
			 */
			SDK__SRC_MAIN_RES_FOLDER = new File("./../egradle-plugin-sdk/src/main/res/");
		}
	}
	
	public static final File ROOTFOLDER_1 = new File(SRC_TEST_RES_FOLDER, "rootproject1");
	
	/**
	 * Rootfolder 2 - is a multi project - contains itself a .project, which must be ignored at imports, because we prefer virtual root project...
	 */
	public static final File ROOTFOLDER_2 = new File(SRC_TEST_RES_FOLDER, "rootproject2");
	
	/**
	 * Rootfolder 3 - is a single project (contains no subfolders with .project)
	 */
	public static final File ROOTFOLDER_3 = new File(SRC_TEST_RES_FOLDER, "rootproject3");
	
	public static final File ROOTFOLDER_2_ECLIPSE_PROJECT1 = new File(ROOTFOLDER_2, "eclipse-project1");
	public static final File ROOTFOLDER_2_ECLIPSE_PROJECT2 = new File(ROOTFOLDER_2, "eclipse-project2");
	public static final File ROOTFOLDER_2_ECLIPSE_PROJECT2_README = new File(ROOTFOLDER_2_ECLIPSE_PROJECT2, "readme.txt");
	
	public static final File ROOTFOLDER_2_NO_ECLIPSE_PROJECT1 = new File(ROOTFOLDER_2, "no-eclipse-project1");
	public static final File ROOTFOLDER_2_NO_ECLIPSE_PROJECT2 = new File(ROOTFOLDER_2, "no-eclipse-project1");
	
	/**
	 * Rootfolder 4 - contains only gradle test files used by parser tests
	 */
	public static final File ROOTFOLDER_4 = new File(SRC_TEST_RES_FOLDER, "rootproject4");
	
	public static final File ROOTFOLDER_4_TEST1_GRADLE = new File(ROOTFOLDER_4, "test1.gradle");
	public static final File ROOTFOLDER_4_TEST2_GRADLE = new File(ROOTFOLDER_4, "test2.gradle");
	public static final File ROOTFOLDER_4_TEST3_GRADLE = new File(ROOTFOLDER_4, "test3.gradle");
	public static final File ROOTFOLDER_4_TEST4_GRADLE = new File(ROOTFOLDER_4, "test4.gradle");
	public static final File ROOTFOLDER_4_TEST5_GRADLE = new File(ROOTFOLDER_4, "test5.gradle");

	/**
	 * Calculates index of search string in given text. E.g. a search string "alpha" in text "alpha beta gamma" returns 5 because it is the end position of "alpha"  
	 * @param text
	 * @param searchString
	 * @return index end
	 * @throws IllegalArgumentException when text is <code>null</code> or searchstring is <code>null</code> or when search string is not found
	 */
	public static int calculateIndexEndOf(String text, String searchString) {
		if (text==null){
			throw new IllegalArgumentException("test case corrupt, argument 'text' may not be null!");
		}
		if (searchString==null){
			throw new IllegalArgumentException("test case corrupt, argument 'searchString' may not be null!");
		}
		int index = text.indexOf(searchString);
		if (index==-1){
			throw new IllegalArgumentException("test case corrupt - did not found searchString:'"+searchString+"' inside text:"+text);
		}
		index=index+searchString.length();
		return index;
	}
}
