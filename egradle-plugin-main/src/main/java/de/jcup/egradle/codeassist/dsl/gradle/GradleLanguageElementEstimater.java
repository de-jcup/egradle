package de.jcup.egradle.codeassist.dsl.gradle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.MethodUtils;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.TypeProvider;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.ItemType;

/**
 * Estimates language elements by given items from model
 * 
 * @author Albert Tregnaghi
 *
 */
public class GradleLanguageElementEstimater {

	private TypeProvider typeProvider;

	public GradleLanguageElementEstimater(TypeProvider provider) {
		if (provider == null) {
			throw new IllegalArgumentException("provider may not be null");
		}
		this.typeProvider = provider;
	}

	/**
	 * Estimate language type for given item and file type
	 * 
	 * @param item
	 * @param fileType
	 *            must be set, if <code>null</code> returned value is always
	 *            <code>null</code>!
	 * @return type or <code>null</code>
	 */
	public EstimationResult estimate(Item item, GradleFileType fileType) {

		if (fileType == null) {
			return null;
		}
		String rootItemType = fileType.getRootType();
		if (item == null) {
			return null;
		}
		Type rootType = typeProvider.getType(rootItemType);
		if (rootType == null) {
			return null;
		}
		EstimationResult result = new EstimationResult();
		/* check what is possible */
		List<Item> path = new ArrayList<>();
		Item pathItem = item;
		while (pathItem != null) {
			if (pathItem.isRoot()) {
				/* short cut to prevent root node added */
				break;
			}
			path.add(pathItem);
			pathItem = pathItem.getParent();
		}
		if (path.isEmpty()) {
			result.element = rootType;
			result.mode = TypeContext.PARENT_TYPE_IS_CONFIGURATION_CLOSURE;
			return result;
		}
		/* reverse - so root-child is on top */
		Collections.reverse(path);

		Iterator<Item> pathIterator = path.iterator();
		/*
		 * check if current type is applyable for next - and identify next type
		 */
		InternalEstimationData current = new InternalEstimationData();
		current.type = rootType;
		String extensionName = null;
		double averagePercentage = 100;
		while (pathIterator.hasNext()) {
			extensionName = null;
			Item currentPathItem = pathIterator.next();
			String currentPathItemName = currentPathItem.getIdentifier();
			if (currentPathItemName == null) {
				continue; // FIXME ATR, 06.02.2017: is this correct to continue
							// here ?!?!
			}
			TypeContext currentMode = TypeContext.UNKNOWN;
			if (currentPathItem.isClosureBlock()) {
				currentMode = TypeContext.PARENT_TYPE_IS_CONFIGURATION_CLOSURE;
			}
			InternalEstimationData found = null;
			if (found == null) {
				found = findByTaskType(currentPathItem, found);
			}
			if (found == null) {
				found = findByExtensions(current.type, currentPathItem);
				if (found != null) {
					extensionName = currentPathItemName;
				}
			}
			if (found == null) {
				found = findByMethods(current.type, currentPathItem);
			}
			if (found == null) {
				found = findByProperties(current.type, currentPathItem);
			}
			int currentReliability;
			if (found != null) {
				current = found;
				currentReliability = current.percent;
				result.mode = currentMode;
			} else {
				currentReliability = 0;
				return null;
			}
			/* make average reliability */
			if (averagePercentage < 99) {
				double formerProblemPercent = 100 - averagePercentage;
				currentReliability -= formerProblemPercent;
			}
			averagePercentage += currentReliability;
			averagePercentage /= 2;
			if (current.type == null) {
				/*
				 * jar{ manifest {} } - when manifest is missing delegation
				 * target so current type is null! so just do a break except
				 * when element is also not null
				 */
				if (current.element == null) {
					return null;
				}
				break;
			}
		}
		result.extensionName = extensionName;
		result.element = current.element;
		result.reliability = averagePercentage;

		return result;
	}

	private InternalEstimationData findByTaskType(Item currentPathItem, InternalEstimationData found) {
		if (ItemType.TASK.equals(currentPathItem.getItemType())) {
			/*
			 * Special handling for tasks , if the type is given inside we use
			 * this one . e.g "task myCopytask(type:Copy)" shall use DSL from
			 * copy task...
			 */
			String taskType = currentPathItem.getType();
			found = buildEstimationDataByTaskType(found, taskType);

		}else if (ItemType.TASKS.equals(currentPathItem.getItemType())){
			String taskType = currentPathItem.getType();
			found = buildEstimationDataByTaskType(found, taskType);
		}
		return found;
	}

	private InternalEstimationData buildEstimationDataByTaskType(InternalEstimationData found, String taskType) {
		if (StringUtils.isNotBlank(taskType)) {
			Type potentialTask = typeProvider.getType(taskType);
			if (potentialTask != null) {
				/*
				 * TODO ATR, 27.02.2017: should be improved as in
				 * outcommented parts, but inheritance information inside
				 * generated xml is not complete currently
				 */
				// /* check its really a task...*/
				// if (potentialTask.isDescendantOf("org.gradle.api.Task")){
				found = new InternalEstimationData();
				found.element = potentialTask;
				found.type = potentialTask;
				found.percent = 100;
				// }
			}
		}
		return found;
	}

	private InternalEstimationData findByProperties(Type currentType, Item item) {
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
				InternalEstimationData r = new InternalEstimationData();
				r.type = p.getType();
				r.element = p;
				r.percent = 100;
				return r;
			}
		}
		return null;
	}

	private InternalEstimationData findByExtensions(Type currentType, Item item) {
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
				InternalEstimationData r = new InternalEstimationData();
				r.type = type;
				r.element = type;
				r.percent = 100;
				return r;
			}
		}
		return null;
	}

	private InternalEstimationData findByMethods(Type currentType, Item item) {
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

	private InternalEstimationData createEstimationData(Method m, boolean hasGroovyClosureAsParameter, int percent) {
		InternalEstimationData r = new InternalEstimationData();
		if (hasGroovyClosureAsParameter) {
			r.type = m.getDelegationTarget();
		} else {
			r.type = m.getReturnType();
		}
		r.element = m;
		r.percent = percent;
		return r;
	}

	private class MethodIdentificationData {
		private int percent;
		private Method method;
	}

	public enum TypeContext {
		PARENT_TYPE_IS_METHODCALL,

		PARENT_TYPE_IS_CONFIGURATION_CLOSURE,

		UNKNOWN
	}

	public class EstimationResult implements LanguageElementMetaData {
		private double reliability;
		private LanguageElement element;
		private TypeContext mode;
		private String extensionName;

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.jcup.egradle.codeassist.dsl.gradle.LanguageElementMetaData#
		 * isTypeFromExtensionConfigurationPoint()
		 */
		@Override
		public boolean isTypeFromExtensionConfigurationPoint() {
			return extensionName != null;
		}

		/**
		 * Returns reliability in percentage
		 * 
		 * @return reliability of estimation
		 */
		public double getReliability() {
			return reliability;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.jcup.egradle.codeassist.dsl.gradle.LanguageElementMetaData#
		 * getExtensionName()
		 */
		@Override
		public String getExtensionName() {
			return extensionName;
		}

		public LanguageElement getElement() {
			return element;
		}

		public TypeContext getMode() {
			return mode;
		}

		/**
		 * Returns element type used for next action (e.g. proposal)
		 * 
		 * @return element type for next asistant action
		 */
		public Type getElementType() {
			if (element instanceof Type) {
				Type type = (Type) element;
				return type;

			} else if (element instanceof Method) {
				Method m = (Method) element;
				if (mode == TypeContext.PARENT_TYPE_IS_CONFIGURATION_CLOSURE) {
					return m.getDelegationTarget();
				}
				return m.getReturnType();
			} else if (element instanceof Property) {
				Property p = (Property) element;
				return p.getType();
			}
			return null;

		}
	}

	private class InternalEstimationData {
		public int percent;
		private Type type;
		private LanguageElement element;
	}

}