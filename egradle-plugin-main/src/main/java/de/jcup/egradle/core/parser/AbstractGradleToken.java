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
	 * Returns unmodifiable list of elements, never <code>null</code>
	 * @return unmodifiable list of elements, never <code>null</code>
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
	 * @return offset inside complete source
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

	@Override
	public String toString() {
		return getClass().getSimpleName()+"[name=" + name + ", lineNumber=" + lineNumber + ", offset=" + offset + "]";
	}
	
	
}
