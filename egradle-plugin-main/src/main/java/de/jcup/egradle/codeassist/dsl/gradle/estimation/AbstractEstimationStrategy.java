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