package de.jcup.egradle.codeassist.dsl.gradle.estimation;

import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.core.model.Item;

class PropertyEstimationStrategy extends AbstractEstimationStrategy {

	@Override
	EstimationData visitImpl(Type current, Item item) {
		return findByProperties(current, item);
	}
	
	private EstimationData findByProperties(Type currentType, Item item) {
		if (currentType == null) {
			return null;
		}
		if (item == null) {
			return null;
		}
		if (item.isClosureBlock()) {
			return null;
		}
		String checkItemName = item.getIdentifier();
		if (checkItemName == null) {
			return null;
		}
		for (Property p : currentType.getProperties()) {
			String propertyName = p.getName();
			if (checkItemName.equals(propertyName)) {
				EstimationData r = new EstimationData();
				r.type = p.getType();
				r.element = p;
				r.percent = 100;
				return r;
			}
		}
		return null;
	}

}