package de.jcup.egradle.core.parser;

import java.util.ArrayList;
import java.util.List;

public class GradleAST {
	
	/**
	 * Internal source code representation
	 */
	List<String> lines= new ArrayList<>();
	private List<AbstractGradleToken> elements=new ArrayList<>();

	public List<AbstractGradleToken> getRootElements() {
		return elements;
	}

	public void add(AbstractGradleToken element) {
		elements.add(element);
	}

}
