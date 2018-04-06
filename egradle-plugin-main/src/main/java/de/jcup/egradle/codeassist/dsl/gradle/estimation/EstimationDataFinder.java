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

import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.core.model.Item;

class EstimationDataFinder {

	/**
	 * Find estimation data by given visitor. If alreadyFound is not
	 * <code>null</code> but found percentage of alreadyFound is not 100, the
	 * visitor will be called and found result will be compared to already
	 * existing one and the more suitable will be returned
	 * 
	 * @param alreadyFound
	 *            - if percentage is already 100 always this parameter will be
	 *            returned as result. Can be <code>null</code>
	 * @param item
	 *            - current item from model. If <code>null</code> always null is
	 *            returned
	 * @param visitor
	 *            - visitor to use
	 * @return
	 */
	protected EstimationData findMostSuitable(EstimationData alreadyFound, Type current, Item item,
			EstimationStrategy visitor) {
		/* if already found is 100 percent stop calculation */
		if (alreadyFound != null) {
			if (alreadyFound.percent == 100) {
				return alreadyFound;
			}
		}

		EstimationData visitorResult = visitor.estimate(current, item);
		if (visitorResult != null) {
			if (alreadyFound == null || alreadyFound.percent < visitorResult.percent) {
				return visitorResult;
			}
		}
		return alreadyFound;
	}

}