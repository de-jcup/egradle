package de.jcup.egradle.codeassist.dsl.gradle.estimation;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.ItemType;

class ByTaskTypeEstimationStrategy extends AbstractEstimationStrategy {

	/**
	 * 
	 */
	private final GradleLanguageElementEstimater estimator;

	/**
	 * @param gradleLanguageElementEstimater
	 */
	ByTaskTypeEstimationStrategy(GradleLanguageElementEstimater gradleLanguageElementEstimater) {
		estimator = gradleLanguageElementEstimater;
	}

	@Override
	EstimationData visitImpl(Type current, Item item) {
		EstimationData found = findByTaskType(item);
		return found;
	}
	
	private EstimationData findByTaskType(Item currentPathItem) {
		if (ItemType.TASK.equals(currentPathItem.getItemType())) {
			/*
			 * Special handling for tasks , if the type is given inside we use
			 * this one . e.g "task myCopytask(type:Copy)" shall use DSL from
			 * copy task...
			 */
			String taskType = currentPathItem.getType();
			return buildEstimationDataByTaskType(taskType);

		} else if (ItemType.TASKS.equals(currentPathItem.getItemType())) {
			String taskType = currentPathItem.getType();
			return buildEstimationDataByTaskType(taskType);
		}
		return null;
	}
	
	private EstimationData buildEstimationDataByTaskType(String taskType) {
		if (StringUtils.isNotBlank(taskType)) {
			Type potentialTask = estimator.typeProvider.getType(taskType);
			if (potentialTask != null) {
				/*
				 * TODO ATR, 27.02.2017: should be improved as in outcommented
				 * parts, but inheritance information inside generated xml is
				 * not complete currently
				 */
				// /* check its really a task...*/
//				if (potentialTask.isDescendantOf("org.gradle.api.Task")){
					EstimationData found = new EstimationData();
					found.element = potentialTask;
					found.type = potentialTask;
					found.percent = 100;
					
					return found;
//				}
			}
		}
		return null;
	}
}