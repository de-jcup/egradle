package de.jcup.egradle.codecompletion.dsl.gradle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.jcup.egradle.codecompletion.dsl.Method;
import de.jcup.egradle.codecompletion.dsl.Parameter;
import de.jcup.egradle.codecompletion.dsl.Property;
import de.jcup.egradle.codecompletion.dsl.Type;
import de.jcup.egradle.codecompletion.dsl.TypeProvider;
import de.jcup.egradle.core.model.Item;

public class GradleTypeEstimater {

	private TypeProvider typeProvider;

	public GradleTypeEstimater(TypeProvider provider) {
		if (provider == null) {
			throw new IllegalArgumentException("provider may not be null");
		}
		this.typeProvider = provider;
	}

	public Type estimateFromGradleProjectAsRoot(Item item) {
		return estimate(item, "org.gradle.api.Project");
	}

	protected Type estimate(Item item, String rootItemType) {
		if (item == null) {
			return null;
		}
		Type rootType = typeProvider.getType(rootItemType);
		if (rootType == null) {
			return null;
		}
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

		/* reverse - so root-child is on top */
		Collections.reverse(path);

		Iterator<Item> pathIterator = path.iterator();
		/*
		 * check if current type is applyable for next - and identify next type
		 */
		Type estimatedType = null;
		Type currentType = rootType;
		while (pathIterator.hasNext()) {
			Item checkItem = pathIterator.next();
			String checkItemName = checkItem.getName();
			if (checkItemName == null) {
				continue;
			}

			Type newCurrentType = findByMethods(currentType, checkItemName);
			if (newCurrentType==null){
				newCurrentType = findByProperties(currentType, checkItemName);
			}
			currentType=newCurrentType;
		}
		estimatedType=currentType;
		return estimatedType;
	}

	/* FIXME ATR, 20.01.2017: write a test case for properties */
	private Type findByProperties(Type currentType, String checkItemName) {
		if (currentType==null){
			return null;
		}
		for (Property p: currentType.getProperties()){
			String propertyName = p.getName();
			if (checkItemName.equals(propertyName)) {
				return p.getType();
			}
		}
		return null;
	}

	private Type findByMethods(Type currentType, String checkItemName) {
		if (currentType==null){
			return null;
		}
		for (Method m : currentType.getMethods()) {
			String methodName = m.getName();
			/* FIXME ATR, 20.01.2017: get groovy magic resolving must be improved */
			if (checkItemName.equals(methodName) || ("get"+checkItemName).equalsIgnoreCase(methodName)) {
				List<Parameter> parameters = m.getParameters();
				for (Parameter p: parameters){
					Type paramType = p.getType();
					/* ignore string parameters */
					if (!isString(paramType)){
						return paramType;
					}
				}
				/* fall back to return type if no param */
				return m.getReturnType();
			}
		}
		return null;
	}

	private boolean isString(Type paramType) {
		if (paramType==null){
			return false;
		}
		return "java.lang.String".equals(paramType.getName());
	}

}
