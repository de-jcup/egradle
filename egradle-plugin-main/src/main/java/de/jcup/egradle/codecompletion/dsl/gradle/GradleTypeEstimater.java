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
		Type currentType = rootType;
		while (pathIterator.hasNext()) {
			Item checkItem = pathIterator.next();
			String checkItemName = checkItem.getName();
			if (checkItemName == null) {
				continue;
			}

			/* FIXME ATR, 20.01.2017: add properties can also use closures because of return type */
			for (Method m : currentType.getMethods()) {
				if (currentType==null){
					break;
				}
				String methodName = m.getName();
				if (checkItemName.equals(methodName)) {
					List<Parameter> parameters = m.getParameters();
					for (Parameter p: parameters){
						Type paramType = p.getType();
						/* ignore string parameters */
						if (!isString(paramType)){
							currentType=paramType;
							break;
						}
					}
					if (currentType==null){
						currentType = m.getReturnType();
					}
					break;
				}
			}
		}
		Type estimatedType = currentType;
		return estimatedType;
	}

	private boolean isString(Type paramType) {
		if (paramType==null){
			return false;
		}
		return "java.lang.String".equals(paramType.getName());
	}

}
