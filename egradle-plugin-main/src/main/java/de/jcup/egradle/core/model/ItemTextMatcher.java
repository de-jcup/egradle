/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
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

import de.jcup.egradle.core.text.AbstractTextMatcher;

public class ItemTextMatcher extends AbstractTextMatcher<Item> {

	@Override
	protected String createItemText(Item item) {
		/*
		 * We build search string without type - reason: otherwise a
		 * "public String getName()" results in "String getName()" and user has
		 * to type "*get" to get the wished content. This behavior would be different to
		 * standards, like java editor quick outline.
		 */
		String itemText = item.buildSearchString(false);
		return itemText;
	}

	
}