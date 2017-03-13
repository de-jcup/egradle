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