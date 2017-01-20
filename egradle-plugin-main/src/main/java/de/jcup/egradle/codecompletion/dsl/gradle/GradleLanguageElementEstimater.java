package de.jcup.egradle.codecompletion.dsl.gradle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.jcup.egradle.codecompletion.dsl.LanguageElement;
import de.jcup.egradle.codecompletion.dsl.Method;
import de.jcup.egradle.codecompletion.dsl.Parameter;
import de.jcup.egradle.codecompletion.dsl.Property;
import de.jcup.egradle.codecompletion.dsl.Type;
import de.jcup.egradle.codecompletion.dsl.TypeProvider;
import de.jcup.egradle.core.model.Item;

public class GradleLanguageElementEstimater {

	private TypeProvider typeProvider;

	public GradleLanguageElementEstimater(TypeProvider provider) {
		if (provider == null) {
			throw new IllegalArgumentException("provider may not be null");
		}
		this.typeProvider = provider;
	}
	
	/**
	 * Estimate language element for given item and file type
	 * @param item
	 * @param fileType must be set, if <code>null</code> returned value is always <code>null</code>!
	 * @return type or <code>null</code>
	 */
	public LanguageElement estimate(Item item, GradleFileType fileType) {
		
		if (fileType==null){
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
		if (path.isEmpty()){
			return rootType;
		}
		/* reverse - so root-child is on top */
		Collections.reverse(path);

		Iterator<Item> pathIterator = path.iterator();
		/*
		 * check if current type is applyable for next - and identify next type
		 */
		Estimation current = new Estimation();
		current.type=rootType;
		while (pathIterator.hasNext()) {
			Item currentPathItem = pathIterator.next();
			String currentPathItemName = currentPathItem.getName();
			if (currentPathItemName == null) {
				continue;
			}

			Estimation found = findByProperties(current.type, currentPathItemName);
			if (found==null){
				found = findByMethods(current.type, currentPathItemName);
			}
			if (found!=null){
				current=found;
			}
			if (current.type==null){
				break;
			}
		}
		return current.element;
	}
	private class Estimation{
		private Type type;
		private LanguageElement element;
	}

	/* FIXME ATR, 20.01.2017: write a test case for properties */
	private Estimation findByProperties(Type currentType, String checkItemName) {
		if (currentType==null){
			return null;
		}
		for (Property p: currentType.getProperties()){
			String propertyName = p.getName();
			if (checkItemName.equals(propertyName)) {
				Estimation r = new Estimation();
				r.type=p.getType();
				r.element=p;
				return r;
			}
		}
		return null;
	}

	private Estimation findByMethods(Type currentType, String checkItemName) {
		if (currentType==null){
			return null;
		}
		String getMagicPendant = null;
		if (checkItemName.length()>1){
			getMagicPendant= "get"+checkItemName.substring(0, 1).toUpperCase()+checkItemName.substring(1);
		}
		for (Method m : currentType.getMethods()) {
			String methodName = m.getName();
			if (checkItemName.equals(methodName) || (getMagicPendant!=null && getMagicPendant.equals(methodName))) {
				Estimation r = new Estimation();
				r.type=m.getReturnType();
				r.element=m;
				return r;
			}
		}
		return null;
	}

	

}
