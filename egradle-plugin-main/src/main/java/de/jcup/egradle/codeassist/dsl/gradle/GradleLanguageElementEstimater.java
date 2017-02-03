package de.jcup.egradle.codeassist.dsl.gradle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.TypeProvider;
import de.jcup.egradle.core.model.Item;

public class GradleLanguageElementEstimater {

	private TypeProvider typeProvider;

	public GradleLanguageElementEstimater(TypeProvider provider) {
		if (provider == null) {
			throw new IllegalArgumentException("provider may not be null");
		}
		this.typeProvider = provider;
	}

	public enum CreationMode {
		PARENT_TYPE_IS_METHODCALL, PARENT_TYPE_IS_CONFIGURATION_CLOSURE
	}

	public class EstimationResult implements LanguageElementMetaData {
		private LanguageElement element;
		private CreationMode mode;
		private String extensionName;

		/* (non-Javadoc)
		 * @see de.jcup.egradle.codeassist.dsl.gradle.LanguageElementMetaData#isTypeFromExtensionConfigurationPoint()
		 */
		@Override
		public boolean isTypeFromExtensionConfigurationPoint() {
			return extensionName != null;
		}

		/* (non-Javadoc)
		 * @see de.jcup.egradle.codeassist.dsl.gradle.LanguageElementMetaData#getExtensionName()
		 */
		@Override
		public String getExtensionName() {
			return extensionName;
		}

		public LanguageElement getElement() {
			return element;
		}

		public CreationMode getMode() {
			return mode;
		}

		public Type getElementType() {
			if (element instanceof Type) {
				Type type = (Type) element;
				return type;

			} else if (element instanceof Method) {
				Method m = (Method) element;
				return m.getReturnType();
			} else if (element instanceof Property) {
				Property p = (Property) element;
				return p.getType();
			}
			return null;

		}
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
			result.mode = CreationMode.PARENT_TYPE_IS_CONFIGURATION_CLOSURE;
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
		while (pathIterator.hasNext()) {
			extensionName = null;
			Item currentPathItem = pathIterator.next();
			String currentPathItemName = currentPathItem.getName();
			if (currentPathItemName == null) {
				continue;
			}

			InternalEstimationData found = null;
			if (found == null) {
				found = findByExtensions(current.type, currentPathItemName);
				if (found != null) {
					extensionName = currentPathItemName;
				}
			}
			if (found == null) {
				found = findByMethods(current.type, currentPathItemName);
			}
			if (found == null) {
				found = findByProperties(current.type, currentPathItemName);
			}
			if (found != null) {
				current = found;

				if (currentPathItem.isClosureBlock()) {
					result.mode = CreationMode.PARENT_TYPE_IS_CONFIGURATION_CLOSURE;
				}

			}
			if (current.type == null) {
				break;
			}
		}
		result.extensionName = extensionName;
		result.element = current.element;

		return result;
	}

	private class InternalEstimationData {
		private Type type;
		private LanguageElement element;
	}

	/* FIXME ATR, 20.01.2017: write a test case for properties */
	private InternalEstimationData findByProperties(Type currentType, String checkItemName) {
		if (currentType == null) {
			return null;
		}
		for (Property p : currentType.getProperties()) {
			String propertyName = p.getName();
			if (checkItemName.equals(propertyName)) {
				InternalEstimationData r = new InternalEstimationData();
				r.type = p.getType();
				r.element = p;
				return r;
			}
		}
		return null;
	}

	private InternalEstimationData findByExtensions(Type currentType, String checkItemName) {
		if (currentType == null) {
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
				return r;
			}
		}
		return null;
	}

	private InternalEstimationData findByMethods(Type currentType, String checkItemName) {
		if (currentType == null) {
			return null;
		}
		String getMagicPendant = null;
		// if (checkItemName.length() > 1) {
		// getMagicPendant = "get" + checkItemName.substring(0, 1).toUpperCase()
		// + checkItemName.substring(1);
		// }
		for (Method m : currentType.getMethods()) {
			String methodName = m.getName();
			if (checkItemName.equals(methodName) || (getMagicPendant != null && getMagicPendant.equals(methodName))) {
				InternalEstimationData r = new InternalEstimationData();
				r.type = m.getReturnType();
				r.element = m;
				return r;
			}
		}
		return null;
	}

}
