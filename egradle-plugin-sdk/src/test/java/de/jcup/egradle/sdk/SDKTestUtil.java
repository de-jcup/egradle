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
package de.jcup.egradle.sdk;

import java.io.File;

public class SDKTestUtil {

	public static File SRC_TEST_RES_FOLDER = new File("egradle-plugin-sdk/src/test/res/");
	static {
		if (!SRC_TEST_RES_FOLDER.exists()) {
			/*
			 * fall back - to be testable by eclipse in sub projects and also
			 * via gradle from root project.
			 */
			SRC_TEST_RES_FOLDER = new File("src/test/res/");
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

	/**
	 * Calculates index of search string in given text. E.g. a search string
	 * "alpha" in text "alpha beta gamma" returns 5 because it is the end
	 * position of "alpha"
	 * 
	 * @param text
	 * @param searchString
	 * @return index end	}

	 * @throws IllegalArgumentException
	 *             when text is <code>null</code> or searchstring is
	 *             <code>null</code> or when search string is not found
	 */
	public static int calculateIndexEndOf(String text, String searchString) {
		if (text == null) {
			throw new IllegalArgumentException("test case corrupt, argument 'text' may not be null!");
		}
		if (searchString == null) {
			throw new IllegalArgumentException("test case corrupt, argument 'searchString' may not be null!");
		}
		int index = text.indexOf(searchString);
		if (index == -1) {
			throw new IllegalArgumentException(
					"test case corrupt - did not found searchString:'" + searchString + "' inside text:" + text);
		}
		index = index + searchString.length();
		return index;
	}

	/**
	 * Calculates index of search string in given text. E.g. a search string
	 * "alpha" in text "alpha beta gamma" returns 5 because it is the end
	 * position of "alpha"
	 * 
	 * @param text
	 * @param searchString
	 * @return index end
	 * @throws IllegalArgumentException
	 *             when text is <code>null</code> or searchstring is
	 *             <code>null</code> or when search string is not found
	 */
	public static int calculateIndexBefore(String text, String searchString) {
		if (text == null) {
			throw new IllegalArgumentException("test case corrupt, argument 'text' may not be null!");
		}
		if (searchString == null) {
			throw new IllegalArgumentException("test case corrupt, argument 'searchString' may not be null!");
		}
		int index = text.indexOf(searchString);
		if (index == -1) {
			throw new IllegalArgumentException(
					"test case corrupt - did not found searchString:'" + searchString + "' inside text:" + text);
		}
		index = index - 1;
		return index;
	}

}
