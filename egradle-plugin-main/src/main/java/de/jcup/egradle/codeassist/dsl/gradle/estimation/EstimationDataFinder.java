package de.jcup.egradle.codeassist.dsl.gradle.estimation;

import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.core.model.Item;

class EstimationDataFinder {

	/**
	 * Find estimation data by given visitor. If alreadyFound is not <code>null</code>
	 * but found percentage of alreadyFound is not 100, the visitor will be called and found
	 * result will be compared to already existing one and the more suitable
	 * will be returned
	 * 
	 * @param alreadyFound
	 *            - if percentage is already 100 always this parameter will
	 *            be returned as result. Can be <code>null</code>
	 * @param item - current item from model. If <code>null</code> always null is returned
	 * @param visitor - visitor to use 
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