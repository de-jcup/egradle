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
package de.jcup.egradle.core.model;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.jcup.egradle.core.api.Matcher;

public class ItemTextMatcher implements Matcher<Item> {

	private Pattern PATTERN_DEREGEX_ASTERISK = Pattern.compile("\\*");
	private Pattern PATTERN_DEREGEX_DOT = Pattern.compile("\\.");

	private Pattern filterPattern;

	public boolean hasFilterPattern() {
		return filterPattern != null;
	}

	@Override
	public boolean matches(Item item) {
		if (item == null) {
			return false;
		}

		if (filterPattern == null) {
			/* no filter matches all... */
			return true;
		}

		/*
		 * We build search string without type - reason: otherwise a
		 * "public String getName()" results in "String getName()" and user has
		 * to type "*get" to get the wished content. This behavior would be different to
		 * standards, like java editor quick outline.
		 */
		String itemText = item.buildSearchString(false);
		if (itemText.length() == 0) {
			return false;
		}

		/* filter pattern set */
		boolean filterPatternMatches = filterPattern.matcher(itemText).matches();
		return filterPatternMatches;
	}

	public void setFilterText(String filterText) {
		this.filterPattern = null;
		if (filterText == null) {
			return;
		}
		filterText = filterText.trim();

		if (filterText.length() == 0) {
			return;
		}
		/*
		 * make user entry not being a regular expression but simple wild card
		 * handled
		 */
		// change "bla*" to "bla.*"
		// change "bla." to "bla\."
		String newPattern = filterText;
		if (!newPattern.endsWith("*")) {
			newPattern += "*";
		}
		newPattern = PATTERN_DEREGEX_ASTERISK.matcher(newPattern).replaceAll(".*");
		newPattern = PATTERN_DEREGEX_DOT.matcher(newPattern).replaceAll("\\.");

		try {
			Pattern filterPattern = Pattern.compile(newPattern, Pattern.CASE_INSENSITIVE);
			this.filterPattern = filterPattern;
		} catch (PatternSyntaxException e) {
			/* ignore - filterPattern is now null, fall back is used */
		}
	}
}