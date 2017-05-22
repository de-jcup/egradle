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

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.MethodUtils;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.core.model.Item;

class MethodEstimationStrategy extends AbstractEstimationStrategy {

	@Override
	EstimationData visitImpl(Type current, Item item) {
		return findByMethods(current, item);
	}
	
	private EstimationData findByMethods(Type currentType, Item item) {
		if (currentType == null) {
			return null;
		}
		if (item == null) {
			return null;
		}
		String checkItemName = item.getIdentifier();
		if (checkItemName == null) {
			return null;
		}

		MethodIdentificationData idData = new MethodIdentificationData();

		for (Method method : currentType.getMethods()) {
			boolean hasGroovyClosureAsParameter = MethodUtils.hasGroovyClosureAsParameter(method);
			if (hasGroovyClosureAsParameter != item.isClosureBlock()) {
				/* different, so not compatible! speed guard close... */
				continue;
			}
			int percent = MethodUtils.calculateMethodIdentificationPercentage(method, checkItemName,
					item.getParameters());
			if (percent == 100) {
				return createEstimationData(method, hasGroovyClosureAsParameter, percent);
			}
			if (percent > idData.percent) {
				idData.percent = percent;
				idData.method = method;
			}

		}

		/* no 100% reached */
		if (idData.method != null) {
			if (idData.percent >= 50) {
				boolean hasGroovyClosureAsParameter = MethodUtils.hasGroovyClosureAsParameter(idData.method);
				return createEstimationData(idData.method, hasGroovyClosureAsParameter, idData.percent);
			}
		}

		return null;
	}

}