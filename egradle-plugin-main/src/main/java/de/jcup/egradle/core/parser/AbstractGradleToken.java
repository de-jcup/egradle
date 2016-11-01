package de.jcup.egradle.core.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractGradleToken {

	String name;
	int lineNumber=-1;
	AbstractGradleToken parent;
	String content;
	List<AbstractGradleToken> elements = new ArrayList<>();
	int offset;

	public String getName() {
		return name;
	}

	public int getLineNumber() {
		return lineNumber;
	}


	/**
	 * Returns undmodifiable list of elements, never <code>null</code>
	 * @return
	 */
	public List<AbstractGradleToken> getElements() {
		return Collections.unmodifiableList(elements);
	}

	/**
	 * Returns parent element or <code>null</code>
	 * @return parent element or <code>null</code>
	 */
	public AbstractGradleToken getParent() {
		return parent;
	}

	public boolean hasChildren() {
		return elements.size()>0;
	}

	/**
	 * Returns offset inside complete source
	 * @return
	 */
	public int getOffset() {
		return offset;
	}

	public int getLength() {
		if (name==null){
			return 0;
		}
		return name.length();
	}
	
}
