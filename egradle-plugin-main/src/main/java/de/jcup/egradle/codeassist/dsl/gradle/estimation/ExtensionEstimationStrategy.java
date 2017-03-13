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