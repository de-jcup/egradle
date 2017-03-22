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
 package de.jcup.egradle.codeassist.dsl.gradle.estimation;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.core.model.Item;

abstract class AbstractEstimationStrategy implements EstimationStrategy {

	@Override
	public EstimationData estimate(Type current, Item item) {
		if (current == null) {
			return null;
		}
		if (item == null) {
			return null;
		}
		return visitImpl(current, item);
	}

	abstract EstimationData visitImpl(Type current, Item item);
	
	EstimationData createEstimationData(Method m, boolean hasGroovyClosureAsParameter, int percent) {
		EstimationData r = new EstimationData();
		if (hasGroovyClosureAsParameter) {
			r.type = m.getDelegationTarget();
		} else {
			r.type = m.getReturnType();
		}
		r.element = m;
		r.percent = percent;
		return r;
	}
}