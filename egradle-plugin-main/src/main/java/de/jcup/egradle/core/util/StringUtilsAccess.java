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

import org.apache.commons.lang3.StringUtils;

/**
 * Simple possibility to use apache string utils from lib inside main plugin -
 * avoids additional binary lib folder in this plugin...
 * 
 * @author Albert Tregnaghi
 *
 */
public class StringUtilsAccess {

	public static boolean isBlank(CharSequence cs) {
		return StringUtils.isBlank(cs);
	}

	public static String substring(String str, int start) {
		return StringUtils.substring(str, start);
	}

	public static String substring(String str, int start, int end) {
		return StringUtils.substring(str, start, end);
	}

	public static String abbreviate(String currentHTML, int maxWidth) {
		return StringUtils.abbreviate(currentHTML, maxWidth);
	}
}
