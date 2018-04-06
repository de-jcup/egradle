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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.TypeProvider;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.LanguageElementMetaData;
import de.jcup.egradle.core.model.Item;

/**
 * Estimates language elements by given items from model
 * 
 * @author Albert Tregnaghi
 *
 */
public class GradleLanguageElementEstimater {

	TypeProvider typeProvider;

	MethodEstimationStrategy methodVisitor = new MethodEstimationStrategy();
	PropertyEstimationStrategy propertyVisitor = new PropertyEstimationStrategy();
	ExtensionEstimationStrategy extensionVisitor = new ExtensionEstimationStrategy();
	ByTaskTypeEstimationStrategy byItemTaskTypeVisitor = new ByTaskTypeEstimationStrategy(this);

	EstimationDataFinder finder = new EstimationDataFinder();

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
			result.mode = EstimationTypeContext.PARENT_TYPE_IS_CONFIGURATION_CLOSURE;
			return result;
		}
		/* reverse - so root-child is on top */
		Collections.reverse(path);

		Iterator<Item> pathIterator = path.iterator();
		/*
		 * check if current type is applyable for next - and identify next type
		 */
		EstimationData current = new EstimationData();
		current.type = rootType;
		String extensionName = null;
		double averagePercentage = 100;
		while (pathIterator.hasNext()) {
			Item currentPathItem = pathIterator.next();
			String currentPathItemName = currentPathItem.getIdentifier();
			if (currentPathItemName == null) {
				continue;
			}
			EstimationTypeContext currentMode = EstimationTypeContext.UNKNOWN;
			if (currentPathItem.isClosureBlock()) {
				currentMode = EstimationTypeContext.PARENT_TYPE_IS_CONFIGURATION_CLOSURE;
			}
			EstimationData found = null;
			found = finder.findMostSuitable(found, current.type, currentPathItem, byItemTaskTypeVisitor);
			found = finder.findMostSuitable(found, current.type, currentPathItem, extensionVisitor);
			found = finder.findMostSuitable(found, current.type, currentPathItem, methodVisitor);
			found = finder.findMostSuitable(found, current.type, currentPathItem, propertyVisitor);

			if (found != null) {
				extensionName = found.extensionName;
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

	public class EstimationResult implements LanguageElementMetaData {
		private double reliability;
		private LanguageElement element;
		private EstimationTypeContext mode;
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

		public EstimationTypeContext getMode() {
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
				if (mode == EstimationTypeContext.PARENT_TYPE_IS_CONFIGURATION_CLOSURE) {
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

}
