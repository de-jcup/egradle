package de.jcup.egradle.codeassist.dsl.gradle.estimation;

import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.core.model.Item;

interface EstimationStrategy {

	/**
	 * Estimate for current type and given item inside this type
	 * @param current
	 * @param item
	 * @return data or <code>null</code>
	 */
	EstimationData estimate(Type current, Item item);

}