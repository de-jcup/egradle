package de.jcup.egradle.core.util;

public interface Transformer<T> {

	/**
	 * Returns transformed source or source again
	 * @param source
	 * @return transformed source or source again never <code>null</code>
	 */
	public T transform(T source);
}
