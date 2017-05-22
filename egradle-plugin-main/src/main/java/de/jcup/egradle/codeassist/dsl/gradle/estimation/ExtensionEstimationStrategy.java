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
 package de.jcup.egradle.codeassist.dsl.gradle.estimation;

import java.util.Map;

import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.core.model.Item;

class ExtensionEstimationStrategy extends AbstractEstimationStrategy {

	@Override
	EstimationData visitImpl(Type current, Item item) {
		EstimationData found = findByExtensions(current, item);
		if (found == null) {
			return null;
		}
		found.extensionName = item.getIdentifier();
		return found;
	}
	
	private EstimationData findByExtensions(Type currentType, Item item) {
		if (currentType == null) {
			return null;
		}
		if (item == null) {
			return null;
		}
		if (!item.isClosureBlock()) {
			return null;
		}
		String checkItemName = item.getIdentifier();
		if (checkItemName == null) {
			return null;
		}
		Map<String, Type> extensions = currentType.getExtensions();
		if (extensions.isEmpty()) {
			return null;
		}
		for (String extensionId : extensions.keySet()) {
			if (checkItemName.equals(extensionId)) {
				Type type = extensions.get(extensionId);
				EstimationData r = new EstimationData();
				r.type = type;
				r.element = type;
				r.percent = 100;
				return r;
			}
		}
		return null;
	}
}